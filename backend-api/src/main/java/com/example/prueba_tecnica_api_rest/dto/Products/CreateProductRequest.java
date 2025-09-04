package com.example.prueba_tecnica_api_rest.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductRequest {
    
    @NotBlank(message = "Nombre de producto es requerido")
    @Size(max = 150, message = "El nombre del producto no puede exceder 150 caracteres")
    private String name;
    
    @NotBlank(message = "La descripción es requerida")
    private String description;
    
    @NotBlank(message = "La URL de la imagen es requerida")
    @Size(max = 500, message = "La URL de la imagen no puede exceder 500 caracteres")
    private String imageUrl;
    
    @NotNull(message = "El precio es requerido")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor que 0")
    private Double price;
    
    @Builder.Default
    @Size(max = 3, message = "El código de moneda no puede exceder 3 caracteres")
    private String currency = "GTQ";
}