package com.pruebatecnica.cartservice.service;

import com.pruebatecnica.cartservice.dto.AddToCartRequest;
import com.pruebatecnica.cartservice.dto.CartDto;
import com.pruebatecnica.cartservice.dto.CartItemDto;
import com.pruebatecnica.cartservice.entity.Cart;
import com.pruebatecnica.cartservice.entity.CartItem;
import com.pruebatecnica.cartservice.repository.CartRepository;
import com.pruebatecnica.cartservice.repository.CartItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CartService {
    
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductService productService;
    
    @Value("${cart.session.expiration:2592000000}") // 30 days default
    private long sessionExpirationMs;
    
    @Transactional(readOnly = true)
    public CartDto getCart(String sessionId, Long userId) {
        log.info("Getting cart for sessionId: {}, userId: {}", sessionId, userId);
        
        Optional<Cart> cartOpt;
        
        if (userId != null) {
            cartOpt = cartRepository.findByUserIdAndStatusWithItems(userId, Cart.CartStatus.ACTIVE);
        } else if (sessionId != null) {
            cartOpt = cartRepository.findBySessionIdAndStatusWithItems(sessionId, Cart.CartStatus.ACTIVE);
        } else {
            throw new IllegalArgumentException("Session ID o User ID es requerido");
        }
        
        if (cartOpt.isEmpty()) {
            return createEmptyCartDto(sessionId, userId);
        }
        
        Cart cart = cartOpt.get();
        
        if (cart.isExpired()) {
            log.info("Cart {} is expired, marking as expired", cart.getId());
            cart.setStatus(Cart.CartStatus.EXPIRED);
            cartRepository.save(cart);
            return createEmptyCartDto(sessionId, userId);
        }
        
        return convertToDto(cart);
    }
    
    public CartDto addToCart(AddToCartRequest request, Long userId) {
        log.info("Adding product {} to cart. SessionId: {}, UserId: {}, Quantity: {}", 
                request.getProductId(), request.getSessionId(), userId, request.getQuantity());
        
        // Get product details from product service
        var productInfo = productService.getProductInfo(request.getProductId());
        if (productInfo == null) {
            throw new IllegalArgumentException("Producto no encontrado con ID: " + request.getProductId());
        }
        
        // Get or create cart
        Cart cart = getOrCreateCart(request.getSessionId(), userId);
        
        // Check if item already exists in cart
        Optional<CartItem> existingItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), request.getProductId());
        
        if (existingItem.isPresent()) {
            // Update quantity
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
            cartItemRepository.save(item);
            log.info("Updated quantity for product {} in cart {}", request.getProductId(), cart.getId());
        } else {
            // Create new cart item
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .productId(request.getProductId())
                    .productName(productInfo.getName())
                    .productImageUrl(productInfo.getImageUrl())
                    .quantity(request.getQuantity())
                    .priceCents(productInfo.getPriceCents())
                    .currency(productInfo.getCurrency())
                    .build();
            
            cartItemRepository.save(newItem);
            log.info("Added new product {} to cart {}", request.getProductId(), cart.getId());
        }
        
        // Update cart totals
        updateCartTotals(cart);
        
        return convertToDto(cartRepository.findByIdWithItems(cart.getId()).orElse(cart));
    }
    
    public CartDto updateCartItem(String sessionId, Long userId, Long productId, Integer quantity) {
        log.info("Updating cart item. SessionId: {}, UserId: {}, ProductId: {}, Quantity: {}", 
                sessionId, userId, productId, quantity);
        
        Cart cart = findActiveCart(sessionId, userId);
        if (cart == null) {
            throw new IllegalArgumentException("Carrito no encontrado");
        }
        
        Optional<CartItem> itemOpt = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId);
        if (itemOpt.isEmpty()) {
            throw new IllegalArgumentException("Producto no encontrado en el carrito");
        }
        
        CartItem item = itemOpt.get();
        
        if (quantity <= 0) {
            // Remove item
            cartItemRepository.delete(item);
            log.info("Removed product {} from cart {}", productId, cart.getId());
        } else {
            // Update quantity
            item.setQuantity(quantity);
            cartItemRepository.save(item);
            log.info("Updated quantity for product {} in cart {}", productId, cart.getId());
        }
        
        // Update cart totals
        updateCartTotals(cart);
        
        return convertToDto(cartRepository.findByIdWithItems(cart.getId()).orElse(cart));
    }
    
    public CartDto removeFromCart(String sessionId, Long userId, Long productId) {
        log.info("Removing product {} from cart. SessionId: {}, UserId: {}", productId, sessionId, userId);
        
        Cart cart = findActiveCart(sessionId, userId);
        if (cart == null) {
            throw new IllegalArgumentException("Carrito no encontrado");
        }
        
        cartItemRepository.deleteByCartIdAndProductId(cart.getId(), productId);
        log.info("Removed product {} from cart {}", productId, cart.getId());
        
        // Update cart totals
        updateCartTotals(cart);
        
        return convertToDto(cartRepository.findByIdWithItems(cart.getId()).orElse(cart));
    }
    
    public void clearCart(String sessionId, Long userId) {
        log.info("Clearing cart. SessionId: {}, UserId: {}", sessionId, userId);
        
        Cart cart = findActiveCart(sessionId, userId);
        if (cart != null) {
            cartItemRepository.deleteByCartId(cart.getId());
            updateCartTotals(cart);
            log.info("Cleared cart {}", cart.getId());
        }
    }
    
    public String generateSessionId() {
        return UUID.randomUUID().toString();
    }
    
    @Scheduled(cron = "${cart.cleanup.schedule:0 0 2 * * *}")
    @Transactional
    public void cleanupExpiredCarts() {
        log.info("Starting cleanup of expired carts");
        
        LocalDateTime now = LocalDateTime.now();
        
        // Mark expired carts
        int expiredCount = cartRepository.markExpiredCarts(now);
        log.info("Marked {} carts as expired", expiredCount);
        
        // Delete old expired/abandoned carts (older than 7 days)
        LocalDateTime cutoffDate = now.minusDays(7);
        int deletedCount = cartRepository.deleteOldCarts(cutoffDate);
        log.info("Deleted {} old carts", deletedCount);
    }
    
    private Cart getOrCreateCart(String sessionId, Long userId) {
        Optional<Cart> cartOpt;
        
        if (userId != null) {
            cartOpt = cartRepository.findByUserIdAndStatus(userId, Cart.CartStatus.ACTIVE);
        } else if (sessionId != null) {
            cartOpt = cartRepository.findBySessionIdAndStatus(sessionId, Cart.CartStatus.ACTIVE);
        } else {
            sessionId = generateSessionId();
            cartOpt = Optional.empty();
        }
        
        if (cartOpt.isPresent()) {
            Cart cart = cartOpt.get();
            if (cart.isExpired()) {
                cart.setStatus(Cart.CartStatus.EXPIRED);
                cartRepository.save(cart);
                return createNewCart(sessionId, userId);
            }
            return cart;
        }
        
        return createNewCart(sessionId, userId);
    }
    
    private Cart createNewCart(String sessionId, Long userId) {
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(sessionExpirationMs / 1000);
        
        Cart cart = Cart.builder()
                .sessionId(sessionId != null ? sessionId : generateSessionId())
                .userId(userId)
                .status(Cart.CartStatus.ACTIVE)
                .totalItems(0)
                .totalPriceCents(0L)
                .currency("GTQ")
                .expiresAt(expiresAt)
                .build();
        
        cart = cartRepository.save(cart);
        log.info("Created new cart with ID: {} for sessionId: {}, userId: {}", cart.getId(), sessionId, userId);
        
        return cart;
    }
    
    private Cart findActiveCart(String sessionId, Long userId) {
        if (userId != null) {
            return cartRepository.findByUserIdAndStatus(userId, Cart.CartStatus.ACTIVE).orElse(null);
        } else if (sessionId != null) {
            return cartRepository.findBySessionIdAndStatus(sessionId, Cart.CartStatus.ACTIVE).orElse(null);
        }
        return null;
    }
    
    private void updateCartTotals(Cart cart) {
        Integer totalItems = cartItemRepository.getTotalItemsByCartId(cart.getId());
        Long totalPriceCents = cartItemRepository.getTotalPriceCentsByCartId(cart.getId());
        
        cart.setTotalItems(totalItems != null ? totalItems : 0);
        cart.setTotalPriceCents(totalPriceCents != null ? totalPriceCents : 0L);
        
        cartRepository.save(cart);
    }
    
    private CartDto createEmptyCartDto(String sessionId, Long userId) {
        return CartDto.builder()
                .sessionId(sessionId != null ? sessionId : generateSessionId())
                .userId(userId)
                .status("ACTIVE")
                .totalItems(0)
                .totalPrice(0.0)
                .currency("GTQ")
                .isAnonymous(userId == null)
                .items(List.of())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    private CartDto convertToDto(Cart cart) {
        List<CartItemDto> itemDtos = cart.getItems().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        
        return CartDto.builder()
                .id(cart.getId())
                .sessionId(cart.getSessionId())
                .userId(cart.getUserId())
                .status(cart.getStatus().name())
                .totalItems(cart.getTotalItems())
                .totalPrice(cart.getTotalPriceInCurrency())
                .currency(cart.getCurrency())
                .isAnonymous(cart.isAnonymous())
                .expiresAt(cart.getExpiresAt())
                .createdAt(cart.getCreatedAt())
                .updatedAt(cart.getUpdatedAt())
                .items(itemDtos)
                .build();
    }
    
    private CartItemDto convertToDto(CartItem item) {
        return CartItemDto.builder()
                .id(item.getId())
                .productId(item.getProductId())
                .productName(item.getProductName())
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