package com.pruebatecnica.cartservice.entity;

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
@Table(name = "carts", indexes = {
    @Index(name = "idx_carts_session_id", columnList = "sessionId", unique = true),
    @Index(name = "idx_carts_user_id", columnList = "userId"),
    @Index(name = "idx_carts_status_updated", columnList = "status, updatedAt")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "session_id", length = 100, unique = true, nullable = false)
    private String sessionId;
    
    @Column(name = "user_id")
    private Long userId;
    
    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "status", length = 20, nullable = false)
    private CartStatus status = CartStatus.ACTIVE;
    
    @Builder.Default
    @Column(name = "total_items", nullable = false)
    private Integer totalItems = 0;
    
    @Builder.Default
    @Column(name = "total_price_cents", nullable = false)
    private Long totalPriceCents = 0L;
    
    @Builder.Default
    @Column(name = "currency", length = 3, nullable = false, columnDefinition = "CHAR(3) DEFAULT 'GTQ'")
    private String currency = "GTQ";
    
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private List<CartItem> items = new ArrayList<>();
    
    public enum CartStatus {
        ACTIVE,
        CHECKOUT,
        COMPLETED,
        EXPIRED,
        ABANDONED
    }
    
    public Double getTotalPriceInCurrency() {
        return totalPriceCents != null ? totalPriceCents / 100.0 : 0.0;
    }
    
    public void setTotalPriceInCurrency(Double price) {
        this.totalPriceCents = price != null ? Math.round(price * 100) : 0L;
    }
    
    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }
    
    public boolean isAnonymous() {
        return userId == null;
    }
    
    public void updateTotals() {
        this.totalItems = items.stream()
            .mapToInt(CartItem::getQuantity)
            .sum();
        
        this.totalPriceCents = items.stream()
            .mapToLong(item -> (long) item.getQuantity() * item.getPriceCents())
            .sum();
    }
}