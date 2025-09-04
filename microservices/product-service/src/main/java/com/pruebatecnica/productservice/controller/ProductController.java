package com.pruebatecnica.productservice.controller;

import com.pruebatecnica.productservice.dto.ApiResponse;
import com.pruebatecnica.productservice.entity.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/products")
@Slf4j
public class ProductController {

    @GetMapping
    public ResponseEntity<ApiResponse<List<Product>>> getAllProducts() {
        log.info("Getting all products");
        
        // Datos de ejemplo hasta implementar el service completo
        List<Product> products = Arrays.asList(
            Product.builder()
                .id(1L)
                .name("Producto de Ejemplo")
                .description("Descripción del producto")
                .imageUrl("https://example.com/image.jpg")
                .priceCents(2500) // $25.00
                .currency("GTQ")
                .isActive(true)
                .build()
        );
        
        ApiResponse<List<Product>> response = ApiResponse.<List<Product>>builder()
                .success(true)
                .httpStatus(HttpStatus.OK.value())
                .appCode("PRODUCTS_FETCHED")
                .message("Productos obtenidos exitosamente")
                .data(products)
                .build();
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .httpStatus(HttpStatus.OK.value())
                .appCode("SERVICE_HEALTHY")
                .message("Product Service is running")
                .data("OK")
                .build();
        
        return ResponseEntity.ok(response);
    }
}