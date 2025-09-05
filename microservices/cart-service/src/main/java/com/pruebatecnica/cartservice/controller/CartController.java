package com.pruebatecnica.cartservice.controller;

import com.pruebatecnica.cartservice.dto.AddToCartRequest;
import com.pruebatecnica.cartservice.dto.ApiResponse;
import com.pruebatecnica.cartservice.dto.CartDto;
import com.pruebatecnica.cartservice.service.CartService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
@Slf4j
public class CartController {
    
    private final CartService cartService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<CartDto>> getCart(
            @RequestParam(required = false) String sessionId,
            HttpServletRequest request) {
        
        try {
            Long userId = getUserIdFromRequest(request);
            
            // If no sessionId provided and user is anonymous, generate one
            if (sessionId == null && userId == null) {
                sessionId = cartService.generateSessionId();
            }
            
            log.info("Getting cart for sessionId: {}, userId: {}", sessionId, userId);
            
            CartDto cart = cartService.getCart(sessionId, userId);
            
            ApiResponse<CartDto> response = ApiResponse.<CartDto>builder()
                    .success(true)
                    .httpStatus(HttpStatus.OK.value())
                    .appCode("CART_RETRIEVED")
                    .message("Carrito obtenido exitosamente")
                    .data(cart)
                    .build();
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error getting cart: {}", e.getMessage());
            
            ApiResponse<CartDto> response = ApiResponse.<CartDto>builder()
                    .success(false)
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .appCode("CART_GET_ERROR")
                    .message("Error al obtener el carrito")
                    .build();
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartDto>> addToCart(
            @Valid @RequestBody AddToCartRequest request,
            HttpServletRequest httpRequest) {
        
        try {
            Long userId = getUserIdFromRequest(httpRequest);
            
            // Generate sessionId if not provided and user is anonymous
            if (request.getSessionId() == null && userId == null) {
                request.setSessionId(cartService.generateSessionId());
            }
            
            log.info("Adding to cart - ProductId: {}, SessionId: {}, UserId: {}, Quantity: {}", 
                    request.getProductId(), request.getSessionId(), userId, request.getQuantity());
            
            CartDto cart = cartService.addToCart(request, userId);
            
            ApiResponse<CartDto> response = ApiResponse.<CartDto>builder()
                    .success(true)
                    .httpStatus(HttpStatus.OK.value())
                    .appCode("ITEM_ADDED_TO_CART")
                    .message("Producto agregado al carrito exitosamente")
                    .data(cart)
                    .build();
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("Error adding to cart: {}", e.getMessage());
            
            ApiResponse<CartDto> response = ApiResponse.<CartDto>builder()
                    .success(false)
                    .httpStatus(HttpStatus.BAD_REQUEST.value())
                    .appCode("CART_ADD_ERROR")
                    .message(e.getMessage())
                    .build();
            
            return ResponseEntity.badRequest().body(response);
            
        } catch (Exception e) {
            log.error("Unexpected error adding to cart: {}", e.getMessage());
            
            ApiResponse<CartDto> response = ApiResponse.<CartDto>builder()
                    .success(false)
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .appCode("CART_ADD_ERROR")
                    .message("Error al agregar producto al carrito")
                    .build();
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PutMapping("/items/{productId}")
    public ResponseEntity<ApiResponse<CartDto>> updateCartItem(
            @PathVariable Long productId,
            @RequestParam Integer quantity,
            @RequestParam(required = false) String sessionId,
            HttpServletRequest request) {
        
        try {
            Long userId = getUserIdFromRequest(request);
            
            log.info("Updating cart item - ProductId: {}, SessionId: {}, UserId: {}, Quantity: {}", 
                    productId, sessionId, userId, quantity);
            
            CartDto cart = cartService.updateCartItem(sessionId, userId, productId, quantity);
            
            ApiResponse<CartDto> response = ApiResponse.<CartDto>builder()
                    .success(true)
                    .httpStatus(HttpStatus.OK.value())
                    .appCode("CART_ITEM_UPDATED")
                    .message("Producto actualizado en el carrito")
                    .data(cart)
                    .build();
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("Error updating cart item: {}", e.getMessage());
            
            ApiResponse<CartDto> response = ApiResponse.<CartDto>builder()
                    .success(false)
                    .httpStatus(HttpStatus.BAD_REQUEST.value())
                    .appCode("CART_UPDATE_ERROR")
                    .message(e.getMessage())
                    .build();
            
            return ResponseEntity.badRequest().body(response);
            
        } catch (Exception e) {
            log.error("Unexpected error updating cart item: {}", e.getMessage());
            
            ApiResponse<CartDto> response = ApiResponse.<CartDto>builder()
                    .success(false)
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .appCode("CART_UPDATE_ERROR")
                    .message("Error al actualizar producto en el carrito")
                    .build();
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @DeleteMapping("/items/{productId}")
    public ResponseEntity<ApiResponse<CartDto>> removeFromCart(
            @PathVariable Long productId,
            @RequestParam(required = false) String sessionId,
            HttpServletRequest request) {
        
        try {
            Long userId = getUserIdFromRequest(request);
            
            log.info("Removing from cart - ProductId: {}, SessionId: {}, UserId: {}", 
                    productId, sessionId, userId);
            
            CartDto cart = cartService.removeFromCart(sessionId, userId, productId);
            
            ApiResponse<CartDto> response = ApiResponse.<CartDto>builder()
                    .success(true)
                    .httpStatus(HttpStatus.OK.value())
                    .appCode("ITEM_REMOVED_FROM_CART")
                    .message("Producto removido del carrito")
                    .data(cart)
                    .build();
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("Error removing from cart: {}", e.getMessage());
            
            ApiResponse<CartDto> response = ApiResponse.<CartDto>builder()
                    .success(false)
                    .httpStatus(HttpStatus.BAD_REQUEST.value())
                    .appCode("CART_REMOVE_ERROR")
                    .message(e.getMessage())
                    .build();
            
            return ResponseEntity.badRequest().body(response);
            
        } catch (Exception e) {
            log.error("Unexpected error removing from cart: {}", e.getMessage());
            
            ApiResponse<CartDto> response = ApiResponse.<CartDto>builder()
                    .success(false)
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .appCode("CART_REMOVE_ERROR")
                    .message("Error al remover producto del carrito")
                    .build();
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> clearCart(
            @RequestParam(required = false) String sessionId,
            HttpServletRequest request) {
        
        try {
            Long userId = getUserIdFromRequest(request);
            
            log.info("Clearing cart - SessionId: {}, UserId: {}", sessionId, userId);
            
            cartService.clearCart(sessionId, userId);
            
            ApiResponse<Void> response = ApiResponse.<Void>builder()
                    .success(true)
                    .httpStatus(HttpStatus.OK.value())
                    .appCode("CART_CLEARED")
                    .message("Carrito vaciado exitosamente")
                    .build();
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error clearing cart: {}", e.getMessage());
            
            ApiResponse<Void> response = ApiResponse.<Void>builder()
                    .success(false)
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .appCode("CART_CLEAR_ERROR")
                    .message("Error al vaciar el carrito")
                    .build();
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // This endpoint requires JWT authentication - will be used when proceeding to checkout
    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<CartDto>> proceedToCheckout(
            @RequestParam(required = false) String sessionId,
            HttpServletRequest request) {
        
        try {
            Long userId = getUserIdFromRequest(request);
            
            // This endpoint requires authentication
            if (userId == null) {
                ApiResponse<CartDto> response = ApiResponse.<CartDto>builder()
                        .success(false)
                        .httpStatus(HttpStatus.UNAUTHORIZED.value())
                        .appCode("AUTHENTICATION_REQUIRED")
                        .message("Se requiere autenticación para proceder al checkout")
                        .build();
                
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            log.info("Proceeding to checkout - SessionId: {}, UserId: {}", sessionId, userId);
            
            // TODO: Implement checkout logic - integrate with order service
            CartDto cart = cartService.getCart(sessionId, userId);
            
            ApiResponse<CartDto> response = ApiResponse.<CartDto>builder()
                    .success(true)
                    .httpStatus(HttpStatus.OK.value())
                    .appCode("CHECKOUT_READY")
                    .message("Listo para proceder al checkout")
                    .data(cart)
                    .build();
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error proceeding to checkout: {}", e.getMessage());
            
            ApiResponse<CartDto> response = ApiResponse.<CartDto>builder()
                    .success(false)
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .appCode("CHECKOUT_ERROR")
                    .message("Error al proceder al checkout")
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
                .message("Cart Service is running")
                .data("OK")
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    private Long getUserIdFromRequest(HttpServletRequest request) {
        // Extract user ID from JWT token headers set by API Gateway
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
}