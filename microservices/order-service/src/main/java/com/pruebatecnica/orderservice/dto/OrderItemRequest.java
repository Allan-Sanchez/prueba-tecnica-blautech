package com.pruebatecnica.orderservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemRequest {
    
    @NotNull(message = "Product ID es requerido")
    private Long productId;
    
    @NotNull(message = "Cantidad es requerida")
    @Min(value = 1, message = "Cantidad debe ser mayor a 0")
    private Integer quantity;
    
    // Optional fields that will be populated from product service if not provided
    private String productName;
    private String productDescription;
    private String productImageUrl;
    private Double price;
    private String currency;
}