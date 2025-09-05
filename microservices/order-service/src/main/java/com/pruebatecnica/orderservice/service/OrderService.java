package com.pruebatecnica.orderservice.service;

import com.pruebatecnica.orderservice.dto.*;
import com.pruebatecnica.orderservice.entity.Order;
import com.pruebatecnica.orderservice.entity.OrderItem;
import com.pruebatecnica.orderservice.repository.OrderRepository;
import com.pruebatecnica.orderservice.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductService productService;
    
    @Value("${order.number.prefix:ORD}")
    private String orderNumberPrefix;
    
    @Value("${order.number.length:8}")
    private int orderNumberLength;
    
    @Transactional(readOnly = true)
    public List<OrderDto> getUserOrders(Long userId) {
        log.info("Getting orders for user ID: {}", userId);
        
        List<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
        log.info("Found {} orders for user ID: {}", orders.size(), userId);
        
        return orders.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public Page<OrderDto> getUserOrdersPaginated(Long userId, Pageable pageable) {
        log.info("Getting paginated orders for user ID: {} with page: {}", userId, pageable);
        
        Page<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        log.info("Found {} orders for user ID: {} on page {}", orders.getContent().size(), userId, pageable.getPageNumber());
        
        return orders.map(this::convertToDto);
    }
    
    @Transactional(readOnly = true)
    public List<OrderDto> getUserOrdersByStatus(Long userId, Order.OrderStatus status) {
        log.info("Getting orders for user ID: {} with status: {}", userId, status);
        
        List<Order> orders = orderRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, status);
        log.info("Found {} orders with status {} for user ID: {}", orders.size(), status, userId);
        
        return orders.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public OrderDto getOrderByNumber(String orderNumber, Long userId) {
        log.info("Getting order with number: {} for user ID: {}", orderNumber, userId);
        
        Optional<Order> orderOpt = orderRepository.findByOrderNumberAndUserIdWithItems(orderNumber, userId);
        if (orderOpt.isEmpty()) {
            throw new IllegalArgumentException("Orden no encontrada con número: " + orderNumber);
        }
        
        Order order = orderOpt.get();
        log.info("Found order with ID: {} for user ID: {}", order.getId(), userId);
        
        return convertToDto(order);
    }
    
    @Transactional(readOnly = true)
    public OrderDto getOrderById(Long orderId, Long userId) {
        log.info("Getting order with ID: {} for user ID: {}", orderId, userId);
        
        Optional<Order> orderOpt = orderRepository.findByIdWithItems(orderId);
        if (orderOpt.isEmpty()) {
            throw new IllegalArgumentException("Orden no encontrada con ID: " + orderId);
        }
        
        Order order = orderOpt.get();
        
        // Security check - ensure order belongs to the user
        if (!order.getUserId().equals(userId)) {
            throw new IllegalArgumentException("No tienes permisos para ver esta orden");
        }
        
        log.info("Found order with number: {} for user ID: {}", order.getOrderNumber(), userId);
        return convertToDto(order);
    }
    
    public OrderDto createOrder(CreateOrderRequest request, Long userId, String userEmail) {
        log.info("Creating order for user ID: {} with {} items", userId, request.getItems().size());
        
        // Validate and fetch product information for each item
        List<OrderItem> orderItems = validateAndPrepareOrderItems(request.getItems());
        
        // Generate unique order number
        String orderNumber = generateOrderNumber();
        
        // Create the order
        Order order = Order.builder()
                .orderNumber(orderNumber)
                .userId(userId)
                .userEmail(userEmail)
                .status(Order.OrderStatus.PENDING)
                .currency("GTQ")
                .notes(request.getNotes())
                .shippingAddress(request.getShippingAddress())
                .billingAddress(request.getBillingAddress())
                .paymentMethod(request.getPaymentMethod())
                .paymentReference(request.getPaymentReference())
                .build();
        
        // Save the order first to get the ID
        order = orderRepository.save(order);
        log.info("Created order with ID: {} and number: {}", order.getId(), order.getOrderNumber());
        
        // Set the order reference for each item and save them
        final Order savedOrder = order;
        orderItems.forEach(item -> {
            item.setOrder(savedOrder);
        });
        orderItemRepository.saveAll(orderItems);
        
        // Update order totals
        order.setItems(orderItems);
        order.calculateTotals();
        order = orderRepository.save(order);
        
        log.info("Order {} created successfully with {} items, total: {} {}", 
                order.getOrderNumber(), order.getTotalItems(), order.getTotalInCurrency(), order.getCurrency());
        
        return convertToDto(order);
    }
    
    public OrderDto updateOrderStatus(String orderNumber, Long userId, UpdateOrderStatusRequest request) {
        log.info("Updating order status for order: {} to status: {}", orderNumber, request.getStatus());
        
        Optional<Order> orderOpt = orderRepository.findByOrderNumberAndUserId(orderNumber, userId);
        if (orderOpt.isEmpty()) {
            throw new IllegalArgumentException("Orden no encontrada con número: " + orderNumber);
        }
        
        Order order = orderOpt.get();
        Order.OrderStatus newStatus = Order.OrderStatus.valueOf(request.getStatus());
        Order.OrderStatus currentStatus = order.getStatus();
        
        // Validate status transition
        validateStatusTransition(currentStatus, newStatus);
        
        // Update status
        order.setStatus(newStatus);
        
        // Update timestamps based on status
        if (newStatus == Order.OrderStatus.SHIPPED && order.getShippedAt() == null) {
            order.setShippedAt(LocalDateTime.now());
        } else if (newStatus == Order.OrderStatus.DELIVERED && order.getDeliveredAt() == null) {
            order.setDeliveredAt(LocalDateTime.now());
        }
        
        // Update notes if provided
        if (request.getNotes() != null && !request.getNotes().trim().isEmpty()) {
            order.setNotes(request.getNotes());
        }
        
        order = orderRepository.save(order);
        log.info("Order {} status updated from {} to {}", orderNumber, currentStatus, newStatus);
        
        return convertToDto(orderRepository.findByIdWithItems(order.getId()).orElse(order));
    }
    
    public void cancelOrder(String orderNumber, Long userId) {
        log.info("Cancelling order: {} for user: {}", orderNumber, userId);
        
        Optional<Order> orderOpt = orderRepository.findByOrderNumberAndUserId(orderNumber, userId);
        if (orderOpt.isEmpty()) {
            throw new IllegalArgumentException("Orden no encontrada con número: " + orderNumber);
        }
        
        Order order = orderOpt.get();
        
        if (!order.canBeCancelled()) {
            throw new IllegalArgumentException("No se puede cancelar una orden con estado: " + order.getStatus());
        }
        
        order.setStatus(Order.OrderStatus.CANCELLED);
        orderRepository.save(order);
        
        log.info("Order {} cancelled successfully", orderNumber);
    }
    
    @Transactional(readOnly = true)
    public List<OrderDto> searchUserOrders(Long userId, String searchTerm) {
        log.info("Searching orders for user: {} with term: {}", userId, searchTerm);
        
        List<Order> orders = orderRepository.searchOrdersByUserId(userId, searchTerm);
        log.info("Found {} orders matching search term: {}", orders.size(), searchTerm);
        
        return orders.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    private List<OrderItem> validateAndPrepareOrderItems(List<OrderItemRequest> itemRequests) {
        return itemRequests.stream()
                .map(this::validateAndPrepareOrderItem)
                .collect(Collectors.toList());
    }
    
    private OrderItem validateAndPrepareOrderItem(OrderItemRequest itemRequest) {
        // Get product information from product service
        ProductService.ProductInfo productInfo = productService.getProductInfo(itemRequest.getProductId());
        if (productInfo == null) {
            throw new IllegalArgumentException("Producto no encontrado con ID: " + itemRequest.getProductId());
        }
        
        if (!productInfo.getIsActive()) {
            throw new IllegalArgumentException("El producto con ID " + itemRequest.getProductId() + " no está disponible");
        }
        
        // Use product info from service, fallback to request data if needed
        String productName = productInfo.getName() != null ? productInfo.getName() : itemRequest.getProductName();
        String productDescription = productInfo.getDescription() != null ? productInfo.getDescription() : itemRequest.getProductDescription();
        String productImageUrl = productInfo.getImageUrl() != null ? productInfo.getImageUrl() : itemRequest.getProductImageUrl();
        Integer priceCents = productInfo.getPriceCents() != null ? productInfo.getPriceCents() : 
                            (itemRequest.getPrice() != null ? (int) Math.round(itemRequest.getPrice() * 100) : 0);
        String currency = productInfo.getCurrency() != null ? productInfo.getCurrency() : 
                         (itemRequest.getCurrency() != null ? itemRequest.getCurrency() : "GTQ");
        
        return OrderItem.builder()
                .productId(itemRequest.getProductId())
                .productName(productName)
                .productDescription(productDescription)
                .productImageUrl(productImageUrl)
                .quantity(itemRequest.getQuantity())
                .priceCents(priceCents)
                .currency(currency)
                .build();
    }
    
    private void validateStatusTransition(Order.OrderStatus currentStatus, Order.OrderStatus newStatus) {
        // Define valid status transitions
        switch (currentStatus) {
            case PENDING:
                if (newStatus != Order.OrderStatus.CONFIRMED && newStatus != Order.OrderStatus.CANCELLED) {
                    throw new IllegalArgumentException("No se puede cambiar de PENDING a " + newStatus);
                }
                break;
            case CONFIRMED:
                if (newStatus != Order.OrderStatus.PROCESSING && newStatus != Order.OrderStatus.CANCELLED) {
                    throw new IllegalArgumentException("No se puede cambiar de CONFIRMED a " + newStatus);
                }
                break;
            case PROCESSING:
                if (newStatus != Order.OrderStatus.SHIPPED && newStatus != Order.OrderStatus.CANCELLED) {
                    throw new IllegalArgumentException("No se puede cambiar de PROCESSING a " + newStatus);
                }
                break;
            case SHIPPED:
                if (newStatus != Order.OrderStatus.DELIVERED) {
                    throw new IllegalArgumentException("No se puede cambiar de SHIPPED a " + newStatus);
                }
                break;
            case DELIVERED:
                if (newStatus != Order.OrderStatus.REFUNDED) {
                    throw new IllegalArgumentException("No se puede cambiar de DELIVERED a " + newStatus);
                }
                break;
            case CANCELLED:
            case REFUNDED:
                throw new IllegalArgumentException("No se puede cambiar el estado de una orden " + currentStatus);
        }
    }
    
    private String generateOrderNumber() {
        // Generate a unique order number with format: ORD12345
        long timestamp = System.currentTimeMillis();
        String number = String.valueOf(timestamp % 1000000); // Last 6 digits of timestamp
        return orderNumberPrefix + String.format("%0" + (orderNumberLength - orderNumberPrefix.length()) + "d", 
                Long.parseLong(number));
    }
    
    private OrderDto convertToDto(Order order) {
        List<OrderItemDto> itemDtos = order.getItems().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        
        return OrderDto.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .userId(order.getUserId())
                .userEmail(order.getUserEmail())
                .status(order.getStatus().name())
                .totalItems(order.getTotalItems())
                .subtotal(order.getSubtotalInCurrency())
                .tax(order.getTaxInCurrency())
                .total(order.getTotalInCurrency())
                .currency(order.getCurrency())
                .notes(order.getNotes())
                .shippingAddress(order.getShippingAddress())
                .billingAddress(order.getBillingAddress())
                .paymentMethod(order.getPaymentMethod())
                .paymentReference(order.getPaymentReference())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .shippedAt(order.getShippedAt())
                .deliveredAt(order.getDeliveredAt())
                .items(itemDtos)
                .canBeCancelled(order.canBeCancelled())
                .canBeShipped(order.canBeShipped())
                .canBeDelivered(order.canBeDelivered())
                .build();
    }
    
    private OrderItemDto convertToDto(OrderItem item) {
        return OrderItemDto.builder()
                .id(item.getId())
                .productId(item.getProductId())
                .productName(item.getProductName())
                .productDescription(item.getProductDescription())
                .productImageUrl(item.getProductImageUrl())
                .quantity(item.getQuantity())
                .price(item.getPriceInCurrency())
                .totalPrice(item.getTotalPrice())
                .currency(item.getCurrency())
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt())
                .build();
    }
}