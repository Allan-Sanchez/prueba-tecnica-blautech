package com.pruebatecnica.orderservice.controller;

import com.pruebatecnica.orderservice.dto.*;
import com.pruebatecnica.orderservice.entity.Order;
import com.pruebatecnica.orderservice.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    
    private final OrderService orderService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderDto>>> getUserOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            HttpServletRequest request) {
        
        try {
            Long userId = getUserIdFromRequest(request);
            if (userId == null) {
                return createUnauthorizedResponse("Se requiere autenticación para ver las órdenes");
            }
            
            log.info("Getting orders for user ID: {} - page: {}, size: {}, status: {}, search: {}", 
                    userId, page, size, status, search);
            
            List<OrderDto> orders;
            
            if (search != null && !search.trim().isEmpty()) {
                orders = orderService.searchUserOrders(userId, search);
            } else if (status != null && !status.trim().isEmpty()) {
                try {
                    Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
                    orders = orderService.getUserOrdersByStatus(userId, orderStatus);
                } catch (IllegalArgumentException e) {
                    ApiResponse<List<OrderDto>> response = ApiResponse.<List<OrderDto>>builder()
                            .success(false)
                            .httpStatus(HttpStatus.BAD_REQUEST.value())
                            .appCode("INVALID_STATUS")
                            .message("Estado de orden inválido: " + status)
                            .build();
                    
                    return ResponseEntity.badRequest().body(response);
                }
            } else if (page >= 0 && size > 0) {
                // Use pagination
                Pageable pageable = PageRequest.of(page, size);
                Page<OrderDto> orderPage = orderService.getUserOrdersPaginated(userId, pageable);
                orders = orderPage.getContent();
            } else {
                orders = orderService.getUserOrders(userId);
            }
            
            ApiResponse<List<OrderDto>> response = ApiResponse.<List<OrderDto>>builder()
                    .success(true)
                    .httpStatus(HttpStatus.OK.value())
                    .appCode("ORDERS_RETRIEVED")
                    .message("Órdenes obtenidas exitosamente")
                    .data(orders)
                    .build();
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error getting orders: {}", e.getMessage());
            
            ApiResponse<List<OrderDto>> response = ApiResponse.<List<OrderDto>>builder()
                    .success(false)
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .appCode("ORDERS_GET_ERROR")
                    .message("Error al obtener las órdenes")
                    .build();
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/{orderNumber}")
    public ResponseEntity<ApiResponse<OrderDto>> getOrderByNumber(
            @PathVariable String orderNumber,
            HttpServletRequest request) {
        
        try {
            Long userId = getUserIdFromRequest(request);
            if (userId == null) {
                return createUnauthorizedResponse("Se requiere autenticación para ver la orden");
            }
            
            log.info("Getting order: {} for user: {}", orderNumber, userId);
            
            OrderDto order = orderService.getOrderByNumber(orderNumber, userId);
            
            ApiResponse<OrderDto> response = ApiResponse.<OrderDto>builder()
                    .success(true)
                    .httpStatus(HttpStatus.OK.value())
                    .appCode("ORDER_RETRIEVED")
                    .message("Orden obtenida exitosamente")
                    .data(order)
                    .build();
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("Order access denied: {}", e.getMessage());
            
            ApiResponse<OrderDto> response = ApiResponse.<OrderDto>builder()
                    .success(false)
                    .httpStatus(HttpStatus.NOT_FOUND.value())
                    .appCode("ORDER_NOT_FOUND")
                    .message(e.getMessage())
                    .build();
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            
        } catch (Exception e) {
            log.error("Error getting order: {}", e.getMessage());
            
            ApiResponse<OrderDto> response = ApiResponse.<OrderDto>builder()
                    .success(false)
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .appCode("ORDER_GET_ERROR")
                    .message("Error al obtener la orden")
                    .build();
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<OrderDto>> createOrder(
            @Valid @RequestBody CreateOrderRequest request,
            HttpServletRequest httpRequest) {
//        log para ver el body del request
        log.info("CreateOrderRequest: {}", request);
        try {
            log.info("--------------create order -------------");
            Long userId = getUserIdFromRequest(httpRequest);
            log.info("userId: {}", userId);
            String userEmail = getUserEmailFromRequest(httpRequest);
            log.info("userEmail: {}", userEmail);
            
            if (userId == null || userEmail == null) {
                log.warn("User ID or email missing in request headers");
                return createUnauthorizedResponse("Se requiere autenticación para crear una orden");
            }
            
            log.info("Creating order for user: {} with {} items", userId, request.getItems().size());
            
            OrderDto order = orderService.createOrder(request, userId, userEmail);
            
            ApiResponse<OrderDto> response = ApiResponse.<OrderDto>builder()
                    .success(true)
                    .httpStatus(HttpStatus.CREATED.value())
                    .appCode("ORDER_CREATED")
                    .message("Orden creada exitosamente")
                    .data(order)
                    .build();
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("Error creating order: {}", e.getMessage());
            
            ApiResponse<OrderDto> response = ApiResponse.<OrderDto>builder()
                    .success(false)
                    .httpStatus(HttpStatus.BAD_REQUEST.value())
                    .appCode("ORDER_CREATE_ERROR")
                    .message(e.getMessage())
                    .build();
            
            return ResponseEntity.badRequest().body(response);
            
        } catch (Exception e) {
            log.error("Unexpected error creating order: {}", e.getMessage());
            
            ApiResponse<OrderDto> response = ApiResponse.<OrderDto>builder()
                    .success(false)
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .appCode("ORDER_CREATE_ERROR")
                    .message("Error al crear la orden")
                    .build();
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PatchMapping("/{orderNumber}/status")
    public ResponseEntity<ApiResponse<OrderDto>> updateOrderStatus(
            @PathVariable String orderNumber,
            @Valid @RequestBody UpdateOrderStatusRequest request,
            HttpServletRequest httpRequest) {
        
        try {
            Long userId = getUserIdFromRequest(httpRequest);
            if (userId == null) {
                return createUnauthorizedResponse("Se requiere autenticación para actualizar la orden");
            }
            
            log.info("Updating order status: {} to {} for user: {}", orderNumber, request.getStatus(), userId);
            
            OrderDto order = orderService.updateOrderStatus(orderNumber, userId, request);
            
            ApiResponse<OrderDto> response = ApiResponse.<OrderDto>builder()
                    .success(true)
                    .httpStatus(HttpStatus.OK.value())
                    .appCode("ORDER_STATUS_UPDATED")
                    .message("Estado de la orden actualizado exitosamente")
                    .data(order)
                    .build();
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("Error updating order status: {}", e.getMessage());
            
            ApiResponse<OrderDto> response = ApiResponse.<OrderDto>builder()
                    .success(false)
                    .httpStatus(HttpStatus.BAD_REQUEST.value())
                    .appCode("ORDER_UPDATE_ERROR")
                    .message(e.getMessage())
                    .build();
            
            return ResponseEntity.badRequest().body(response);
            
        } catch (Exception e) {
            log.error("Unexpected error updating order status: {}", e.getMessage());
            
            ApiResponse<OrderDto> response = ApiResponse.<OrderDto>builder()
                    .success(false)
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .appCode("ORDER_UPDATE_ERROR")
                    .message("Error al actualizar el estado de la orden")
                    .build();
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @DeleteMapping("/{orderNumber}")
    public ResponseEntity<ApiResponse<Void>> cancelOrder(
            @PathVariable String orderNumber,
            HttpServletRequest request) {
        
        try {
            Long userId = getUserIdFromRequest(request);
            if (userId == null) {
                return createUnauthorizedResponse("Se requiere autenticación para cancelar la orden");
            }
            
            log.info("Cancelling order: {} for user: {}", orderNumber, userId);
            
            orderService.cancelOrder(orderNumber, userId);
            
            ApiResponse<Void> response = ApiResponse.<Void>builder()
                    .success(true)
                    .httpStatus(HttpStatus.OK.value())
                    .appCode("ORDER_CANCELLED")
                    .message("Orden cancelada exitosamente")
                    .build();
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("Error cancelling order: {}", e.getMessage());
            
            ApiResponse<Void> response = ApiResponse.<Void>builder()
                    .success(false)
                    .httpStatus(HttpStatus.BAD_REQUEST.value())
                    .appCode("ORDER_CANCEL_ERROR")
                    .message(e.getMessage())
                    .build();
            
            return ResponseEntity.badRequest().body(response);
            
        } catch (Exception e) {
            log.error("Unexpected error cancelling order: {}", e.getMessage());
            
            ApiResponse<Void> response = ApiResponse.<Void>builder()
                    .success(false)
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .appCode("ORDER_CANCEL_ERROR")
                    .message("Error al cancelar la orden")
                    .build();
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .httpStatus(HttpStatus.OK.value())
                .appCode("SERVICE_HEALTHY")
                .message("Order Service is running")
                .data("OK")
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    private Long getUserIdFromRequest(HttpServletRequest request) {
        String userIdHeader = request.getHeader("X-User-Id");
        if (userIdHeader != null && !userIdHeader.isEmpty()) {
            try {
                return Long.parseLong(userIdHeader);
            } catch (NumberFormatException e) {
                log.warn("Invalid user ID header: {}", userIdHeader);
            }
        }
        return null;
    }
    
    private String getUserEmailFromRequest(HttpServletRequest request) {
        return request.getHeader("X-User-Email");
    }
    
    @SuppressWarnings("unchecked")
    private <T> ResponseEntity<ApiResponse<T>> createUnauthorizedResponse(String message) {
        ApiResponse<T> response = ApiResponse.<T>builder()
                .success(false)
                .httpStatus(HttpStatus.UNAUTHORIZED.value())
                .appCode("AUTHENTICATION_REQUIRED")
                .message(message)
                .build();
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
}