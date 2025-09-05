package com.pruebatecnica.cartservice.dto;

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
public class AddToCartRequest {
    
    @NotNull(message = "Product ID es requerido")
    private Long productId;
    
    @NotNull(message = "Cantidad es requerida")
    @Min(value = 1, message = "Cantidad debe ser mayor a 0")
    private Integer quantity;
    
    private String sessionId;
}