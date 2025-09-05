package com.pruebatecnica.orderservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders", indexes = {
    @Index(name = "idx_orders_user_id", columnList = "userId"),
    @Index(name = "idx_orders_order_number", columnList = "orderNumber", unique = true),
    @Index(name = "idx_orders_status_created", columnList = "status, createdAt"),
    @Index(name = "idx_orders_user_status", columnList = "userId, status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "order_number", length = 20, unique = true, nullable = false)
    private String orderNumber;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "user_email", length = 150, nullable = false)
    private String userEmail;
    
    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "status", length = 20, nullable = false)
    private OrderStatus status = OrderStatus.PENDING;
    
    @Builder.Default
    @Column(name = "total_items", nullable = false)
    private Integer totalItems = 0;
    
    @Builder.Default
    @Column(name = "subtotal_cents", nullable = false)
    private Long subtotalCents = 0L;
    
    @Builder.Default
    @Column(name = "tax_cents", nullable = false)
    private Long taxCents = 0L;
    
    @Builder.Default
    @Column(name = "total_cents", nullable = false)
    private Long totalCents = 0L;
    
    @Builder.Default
    @Column(name = "currency", length = 3, nullable = false, columnDefinition = "CHAR(3) DEFAULT 'GTQ'")
    private String currency = "GTQ";
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "shipping_address", columnDefinition = "TEXT")
    private String shippingAddress;
    
    @Column(name = "billing_address", columnDefinition = "TEXT")
    private String billingAddress;
    
    @Column(name = "payment_method", length = 50)
    private String paymentMethod;
    
    @Column(name = "payment_reference", length = 100)
    private String paymentReference;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @Column(name = "shipped_at")
    private LocalDateTime shippedAt;
    
    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();
    
    public enum OrderStatus {
        PENDING,        // Orden creada, pendiente de pago
        CONFIRMED,      // Orden confirmada, pago recibido
        PROCESSING,     // Orden en proceso de preparación
        SHIPPED,        // Orden enviada
        DELIVERED,      // Orden entregada
        CANCELLED,      // Orden cancelada
        REFUNDED        // Orden reembolsada
    }
    
    public Double getSubtotalInCurrency() {
        return subtotalCents != null ? subtotalCents / 100.0 : 0.0;
    }
    
    public void setSubtotalInCurrency(Double amount) {
        this.subtotalCents = amount != null ? Math.round(amount * 100) : 0L;
    }
    
    public Double getTaxInCurrency() {
        return taxCents != null ? taxCents / 100.0 : 0.0;
    }
    
    public void setTaxInCurrency(Double amount) {
        this.taxCents = amount != null ? Math.round(amount * 100) : 0L;
    }
    
    public Double getTotalInCurrency() {
        return totalCents != null ? totalCents / 100.0 : 0.0;
    }
    
    public void setTotalInCurrency(Double amount) {
        this.totalCents = amount != null ? Math.round(amount * 100) : 0L;
    }
    
    public void calculateTotals() {
        this.totalItems = items.stream()
            .mapToInt(OrderItem::getQuantity)
            .sum();
        
        this.subtotalCents = items.stream()
            .mapToLong(item -> (long) item.getQuantity() * item.getPriceCents())
            .sum();
        
        // Calculate tax (12% for Guatemala)
        this.taxCents = Math.round(this.subtotalCents * 0.12);
        
        // Total = Subtotal + Tax
        this.totalCents = this.subtotalCents + this.taxCents;
    }
    
    public boolean canBeCancelled() {
        return status == OrderStatus.PENDING || status == OrderStatus.CONFIRMED;
    }
    
    public boolean canBeShipped() {
        return status == OrderStatus.CONFIRMED || status == OrderStatus.PROCESSING;
    }
    
    public boolean canBeDelivered() {
        return status == OrderStatus.SHIPPED;
    }
}