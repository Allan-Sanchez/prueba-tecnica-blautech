package com.pruebatecnica.orderservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_items", indexes = {
    @Index(name = "idx_order_items_order_id", columnList = "order_id"),
    @Index(name = "idx_order_items_product_id", columnList = "productId")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
    
    @Column(name = "product_id", nullable = false)
    private Long productId;
    
    @Column(name = "product_name", length = 150, nullable = false)
    private String productName;
    
    @Column(name = "product_description", columnDefinition = "TEXT")
    private String productDescription;
    
    @Column(name = "product_image_url", length = 500)
    private String productImageUrl;
    
    @Builder.Default
    @Column(name = "quantity", nullable = false)
    private Integer quantity = 1;
    
    @Column(name = "price_cents", nullable = false)
    private Integer priceCents;
    
    @Builder.Default
    @Column(name = "currency", length = 3, nullable = false, columnDefinition = "CHAR(3) DEFAULT 'GTQ'")
    private String currency = "GTQ";
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    public Double getPriceInCurrency() {
        return priceCents != null ? priceCents / 100.0 : 0.0;
    }
    
    public void setPriceInCurrency(Double price) {
        this.priceCents = price != null ? (int) Math.round(price * 100) : 0;
    }
    
    public Double getTotalPrice() {
        return (priceCents != null && quantity != null) ? 
            (priceCents * quantity) / 100.0 : 0.0;
    }
    
    public Long getTotalPriceCents() {
        return (priceCents != null && quantity != null) ? 
            (long) priceCents * quantity : 0L;
    }
}