package com.pruebatecnica.productservice.controller;

import com.pruebatecnica.productservice.dto.ApiResponse;
import com.pruebatecnica.productservice.entity.Product;
import com.pruebatecnica.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {
    
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Product>>> getAllProducts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice) {
        log.info("Getting products with filters - search: {}, minPrice: {}, maxPrice: {}", search, minPrice, maxPrice);
        
        try {
            List<Product> products;
            
            if (search != null && !search.trim().isEmpty()) {
                products = productService.searchProducts(search);
            } else if (minPrice != null || maxPrice != null) {
                products = productService.getProductsByPriceRange(minPrice, maxPrice);
            } else {
                products = productService.getAllActiveProducts();
            }
            
            ApiResponse<List<Product>> response = ApiResponse.<List<Product>>builder()
                    .success(true)
                    .httpStatus(HttpStatus.OK.value())
                    .appCode("PRODUCTS_FETCHED")
                    .message("Productos obtenidos exitosamente")
                    .data(products)
                    .build();
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error fetching products: {}", e.getMessage());
            
            ApiResponse<List<Product>> response = ApiResponse.<List<Product>>builder()
                    .success(false)
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .appCode("PRODUCTS_FETCH_ERROR")
                    .message("Error al obtener los productos")
                    .build();
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> getProductById(@PathVariable Long id) {
        log.info("Getting product with ID: {}", id);
        
        try {
            Optional<Product> product = productService.getProductById(id);
            
            if (product.isPresent()) {
                ApiResponse<Product> response = ApiResponse.<Product>builder()
                        .success(true)
                        .httpStatus(HttpStatus.OK.value())
                        .appCode("PRODUCT_FOUND")
                        .message("Producto encontrado")
                        .data(product.get())
                        .build();
                
                return ResponseEntity.ok(response);
            } else {
                ApiResponse<Product> response = ApiResponse.<Product>builder()
                        .success(false)
                        .httpStatus(HttpStatus.NOT_FOUND.value())
                        .appCode("PRODUCT_NOT_FOUND")
                        .message("Producto no encontrado")
                        .build();
                
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
        } catch (Exception e) {
            log.error("Error fetching product with ID {}: {}", id, e.getMessage());
            
            ApiResponse<Product> response = ApiResponse.<Product>builder()
                    .success(false)
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .appCode("PRODUCT_FETCH_ERROR")
                    .message("Error al obtener el producto")
                    .build();
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
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