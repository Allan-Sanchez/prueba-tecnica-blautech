package com.pruebatecnica.cartservice.repository;

import com.pruebatecnica.cartservice.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    
    Optional<Cart> findBySessionIdAndStatus(String sessionId, Cart.CartStatus status);
    
    Optional<Cart> findByUserIdAndStatus(Long userId, Cart.CartStatus status);
    
    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.items WHERE c.sessionId = :sessionId AND c.status = :status")
    Optional<Cart> findBySessionIdAndStatusWithItems(@Param("sessionId") String sessionId, @Param("status") Cart.CartStatus status);
    
    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.items WHERE c.userId = :userId AND c.status = :status")
    Optional<Cart> findByUserIdAndStatusWithItems(@Param("userId") Long userId, @Param("status") Cart.CartStatus status);
    
    List<Cart> findByExpiresAtBeforeAndStatus(LocalDateTime dateTime, Cart.CartStatus status);
    
    @Modifying
    @Query("UPDATE Cart c SET c.status = 'EXPIRED', c.updatedAt = CURRENT_TIMESTAMP WHERE c.expiresAt < :now AND c.status = 'ACTIVE'")
    int markExpiredCarts(@Param("now") LocalDateTime now);
    
    @Modifying
    @Query("DELETE FROM Cart c WHERE c.status IN ('EXPIRED', 'ABANDONED') AND c.updatedAt < :cutoffDate")
    int deleteOldCarts(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    @Query("SELECT COUNT(c) FROM Cart c WHERE c.userId = :userId AND c.status = 'ACTIVE'")
    long countActiveCartsByUserId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(c) FROM Cart c WHERE c.sessionId = :sessionId AND c.status = 'ACTIVE'")
    long countActiveCartsBySessionId(@Param("sessionId") String sessionId);
    
    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.items WHERE c.id = :id")
    Optional<Cart> findByIdWithItems(@Param("id") Long id);
}