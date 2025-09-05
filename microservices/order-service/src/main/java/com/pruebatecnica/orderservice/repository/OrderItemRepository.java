package com.pruebatecnica.orderservice.repository;

import com.pruebatecnica.orderservice.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    
    List<OrderItem> findByOrderId(Long orderId);
    
    List<OrderItem> findByProductId(Long productId);
    
    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.userId = :userId")
    List<OrderItem> findByUserId(@Param("userId") Long userId);
    
    @Query("SELECT SUM(oi.quantity) FROM OrderItem oi WHERE oi.order.id = :orderId")
    Integer getTotalQuantityByOrderId(@Param("orderId") Long orderId);
    
    @Query("SELECT SUM(oi.quantity * oi.priceCents) FROM OrderItem oi WHERE oi.order.id = :orderId")
    Long getTotalAmountByOrderId(@Param("orderId") Long orderId);
    
    @Query("SELECT COUNT(DISTINCT oi.productId) FROM OrderItem oi WHERE oi.order.userId = :userId")
    long countDistinctProductsByUserId(@Param("userId") Long userId);
    
    @Query("SELECT oi.productId, SUM(oi.quantity) FROM OrderItem oi " +
           "WHERE oi.order.userId = :userId AND oi.order.status IN ('CONFIRMED', 'PROCESSING', 'SHIPPED', 'DELIVERED') " +
           "GROUP BY oi.productId ORDER BY SUM(oi.quantity) DESC")
    List<Object[]> getMostPurchasedProductsByUserId(@Param("userId") Long userId);
}