package com.example.prueba_tecnica_api_rest.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductRequest {
    
    @Size(max = 150, message = "Product name cannot exceed 150 characters")
    private String name;
    
    private String description;
    
    @Size(max = 500, message = "Image URL cannot exceed 500 characters")
    private String imageUrl;
    
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private Double price;
    
    @Size(max = 3, message = "Currency code cannot exceed 3 characters")
    private String currency;
    
    private Boolean isActive;
}