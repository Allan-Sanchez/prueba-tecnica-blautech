package com.pruebatecnica.orderservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {
    
    @NotEmpty(message = "Los items de la orden son requeridos")
    @Valid
    private List<OrderItemRequest> items;
    
    @Size(max = 500, message = "Las notas no pueden exceder 500 caracteres")
    private String notes;
    
    @Size(max = 1000, message = "La dirección de envío no puede exceder 1000 caracteres")
    private String shippingAddress;
    
    @Size(max = 1000, message = "La dirección de facturación no puede exceder 1000 caracteres")
    private String billingAddress;
    
    @Size(max = 50, message = "El método de pago no puede exceder 50 caracteres")
    private String paymentMethod;
    
    @Size(max = 100, message = "La referencia de pago no puede exceder 100 caracteres")
    private String paymentReference;
}