package com.pruebatecnica.orderservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderStatusRequest {
    
    @NotNull(message = "El estado es requerido")
    @Pattern(regexp = "PENDING|CONFIRMED|PROCESSING|SHIPPED|DELIVERED|CANCELLED|REFUNDED", 
             message = "Estado inválido")
    private String status;
    
    @Size(max = 500, message = "Las notas no pueden exceder 500 caracteres")
    private String notes;
}