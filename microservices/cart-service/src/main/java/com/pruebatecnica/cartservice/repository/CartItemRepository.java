package com.pruebatecnica.cartservice.repository;

import com.pruebatecnica.cartservice.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    
    List<CartItem> findByCartId(Long cartId);
    
    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);
    
    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.sessionId = :sessionId AND ci.productId = :productId")
    Optional<CartItem> findBySessionIdAndProductId(@Param("sessionId") String sessionId, @Param("productId") Long productId);
    
    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.userId = :userId AND ci.productId = :productId AND ci.cart.status = 'ACTIVE'")
    Optional<CartItem> findByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);
    
    @Query("SELECT SUM(ci.quantity) FROM CartItem ci WHERE ci.cart.id = :cartId")
    Integer getTotalItemsByCartId(@Param("cartId") Long cartId);
    
    @Query("SELECT SUM(ci.quantity * ci.priceCents) FROM CartItem ci WHERE ci.cart.id = :cartId")
    Long getTotalPriceCentsByCartId(@Param("cartId") Long cartId);
    
    void deleteByCartId(Long cartId);
    
    void deleteByCartIdAndProductId(Long cartId, Long productId);
}