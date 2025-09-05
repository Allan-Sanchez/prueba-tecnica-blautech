package com.pruebatecnica.orderservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    
    private Long id;
    private String orderNumber;
    private Long userId;
    private String userEmail;
    private String status;
    private Integer totalItems;
    private Double subtotal;
    private Double tax;
    private Double total;
    private String currency;
    private String notes;
    private String shippingAddress;
    private String billingAddress;
    private String paymentMethod;
    private String paymentReference;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime shippedAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deliveredAt;
    
    private List<OrderItemDto> items;
    
    // Helper fields for UI
    private boolean canBeCancelled;
    private boolean canBeShipped;
    private boolean canBeDelivered;
}