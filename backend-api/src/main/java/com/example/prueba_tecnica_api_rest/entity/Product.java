package com.example.prueba_tecnica_api_rest.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "products", indexes = {
    @Index(name = "idx_products_active_created", columnList = "isActive, createdAt")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", length = 150, nullable = false)
    private String name;
    
    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;
    
    @Column(name = "image_url", length = 500, nullable = false)
    private String imageUrl;
    
    @Column(name = "price_cents", nullable = false)
    private Integer priceCents;
    
    @Builder.Default
    @Column(name = "currency", length = 3, nullable = false, columnDefinition = "CHAR(3) DEFAULT 'GTQ'")
    private String currency = "GTQ";
    
    @Builder.Default
    @Column(name = "is_active", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 1")
    private Boolean isActive = true;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // Helper methods for price conversion
    public Double getPriceInCurrency() {
        return priceCents != null ? priceCents / 100.0 : 0.0;
    }
    
    public void setPriceInCurrency(Double price) {
        this.priceCents = price != null ? (int) Math.round(price * 100) : 0;
    }
}