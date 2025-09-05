package com.pruebatecnica.orderservice.repository;

import com.pruebatecnica.orderservice.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    // Find orders by user ID
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    Page<Order> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    // Find orders by user ID and status
    List<Order> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, Order.OrderStatus status);
    
    Page<Order> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, Order.OrderStatus status, Pageable pageable);
    
    // Find order by order number
    Optional<Order> findByOrderNumber(String orderNumber);
    
    // Find order by order number and user ID (for security)
    Optional<Order> findByOrderNumberAndUserId(String orderNumber, Long userId);
    
    // Find orders with items
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items WHERE o.id = :orderId")
    Optional<Order> findByIdWithItems(@Param("orderId") Long orderId);
    
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items WHERE o.orderNumber = :orderNumber")
    Optional<Order> findByOrderNumberWithItems(@Param("orderNumber") String orderNumber);
    
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items WHERE o.orderNumber = :orderNumber AND o.userId = :userId")
    Optional<Order> findByOrderNumberAndUserIdWithItems(@Param("orderNumber") String orderNumber, @Param("userId") Long userId);
    
    // Statistics queries
    @Query("SELECT COUNT(o) FROM Order o WHERE o.userId = :userId")
    long countOrdersByUserId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.userId = :userId AND o.status = :status")
    long countOrdersByUserIdAndStatus(@Param("userId") Long userId, @Param("status") Order.OrderStatus status);
    
    @Query("SELECT SUM(o.totalCents) FROM Order o WHERE o.userId = :userId AND o.status IN ('CONFIRMED', 'PROCESSING', 'SHIPPED', 'DELIVERED')")
    Long getTotalSpentByUserId(@Param("userId") Long userId);
    
    // Admin queries
    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate ORDER BY o.createdAt DESC")
    List<Order> findOrdersByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT o FROM Order o WHERE o.status = :status ORDER BY o.createdAt DESC")
    List<Order> findOrdersByStatus(@Param("status") Order.OrderStatus status);
    
    Page<Order> findByStatusOrderByCreatedAtDesc(Order.OrderStatus status, Pageable pageable);
    
    // Search orders
    @Query("SELECT o FROM Order o WHERE " +
           "(LOWER(o.orderNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(o.userEmail) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "ORDER BY o.createdAt DESC")
    List<Order> searchOrders(@Param("searchTerm") String searchTerm);
    
    @Query("SELECT o FROM Order o WHERE o.userId = :userId AND " +
           "LOWER(o.orderNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "ORDER BY o.createdAt DESC")
    List<Order> searchOrdersByUserId(@Param("userId") Long userId, @Param("searchTerm") String searchTerm);
}