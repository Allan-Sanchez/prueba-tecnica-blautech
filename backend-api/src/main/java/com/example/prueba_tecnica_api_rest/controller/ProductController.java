package com.example.prueba_tecnica_api_rest.controller;

import com.example.prueba_tecnica_api_rest.dto.ApiResponse;
import com.example.prueba_tecnica_api_rest.dto.CreateProductRequest;
import com.example.prueba_tecnica_api_rest.dto.ProductDto;
import com.example.prueba_tecnica_api_rest.dto.UpdateProductRequest;
import com.example.prueba_tecnica_api_rest.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
@Validated
@CrossOrigin(origins = "http://localhost:3000")
public class ProductController {
    
    private final ProductService productService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductDto>>> getProducts(
            @RequestParam(defaultValue = "0") @Min(0) Integer page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) Integer size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice) {
        
        log.info("Getting products - page: {}, size: {}, sortBy: {}, sortDir: {}, search: {}", 
                page, size, sortBy, sortDir, search);
        
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? 
            Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        Page<ProductDto> products;
        
        if (search != null && !search.trim().isEmpty()) {
            products = productService.searchActiveProducts(search.trim(), pageable);
        } else if (minPrice != null && maxPrice != null) {
            products = productService.getActiveProductsByPriceRange(minPrice, maxPrice, pageable);
        } else {
            products = productService.getActiveProducts(pageable);
        }
        
        ApiResponse<Page<ProductDto>> response = ApiResponse.<Page<ProductDto>>builder()
                .success(true)
                .message("Products retrieved successfully")
                .data(products)
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<ProductDto>>> getAllProducts() {
        log.info("Getting all active products");
        
        List<ProductDto> products = productService.getAllActiveProducts();
        
        ApiResponse<List<ProductDto>> response = ApiResponse.<List<ProductDto>>builder()
                .success(true)
                .message("Todos los productos activos se han recuperado con éxito")
                .data(products)
                .appCode("OK")
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDto>> getProductById(@PathVariable Long id) {
        log.info("Getting product by id: {}", id);
        
        Optional<ProductDto> product = productService.getActiveProductById(id);
        
        if (product.isPresent()) {
            ApiResponse<ProductDto> response = ApiResponse.<ProductDto>builder()
                    .success(true)
                    .message("Product found")
                    .data(product.get())
                    .build();
            return ResponseEntity.ok(response);
        } else {
            ApiResponse<ProductDto> response = ApiResponse.<ProductDto>builder()
                    .success(false)
                    .message("Product not found")
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<ProductDto>> createProduct(@Valid @RequestBody CreateProductRequest request) {
        log.info("Creating new product: {}", request.getName());
        
        ProductDto createdProduct = productService.createProduct(request);
        
        ApiResponse<ProductDto> response = ApiResponse.<ProductDto>builder()
                .success(true)
                .httpStatus(HttpStatus.CREATED.value())
                .appCode("PRODUCT_CREATED")
                .message("Producto creado exitosamente")
                .data(createdProduct)
                .build();
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDto>> updateProduct(
            @PathVariable Long id, 
            @Valid @RequestBody UpdateProductRequest request) {
        
        log.info("Updating product with id: {}", id);
        
        Optional<ProductDto> updatedProduct = productService.updateProduct(id, request);
        
        if (updatedProduct.isPresent()) {
            ApiResponse<ProductDto> response = ApiResponse.<ProductDto>builder()
                    .success(true)
                    .httpStatus(HttpStatus.OK.value())
                    .appCode("PRODUCT_UPDATED")
                    .message("Producto actualizado exitosamente")
                    .data(updatedProduct.get())
                    .build();
            return ResponseEntity.ok(response);
        } else {
            ApiResponse<ProductDto> response = ApiResponse.<ProductDto>builder()
                    .success(false)
                    .httpStatus(HttpStatus.NOT_FOUND.value())
                    .appCode("PRODUCT_NOT_FOUND")
                    .message("Producto no encontrado")
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deactivateProduct(@PathVariable Long id) {
        log.info("Deactivating product with id: {}", id);
        
        boolean deactivated = productService.deactivateProduct(id);
        
        if (deactivated) {
            ApiResponse<Void> response = ApiResponse.<Void>builder()
                    .success(true)
                    .httpStatus(HttpStatus.OK.value())
                    .appCode("PRODUCT_DEACTIVATED")
                    .message("Producto desactivado exitosamente")
                    .build();
            return ResponseEntity.ok(response);
        } else {
            ApiResponse<Void> response = ApiResponse.<Void>builder()
                    .success(false)
                    .httpStatus(HttpStatus.NOT_FOUND.value())
                    .appCode("PRODUCT_NOT_FOUND")
                    .message("Producto no encontrado")
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    
    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> getActiveProductsCount() {
        log.info("Getting active products count");
        
        Long count = productService.countActiveProducts();
        
        ApiResponse<Long> response = ApiResponse.<Long>builder()
                .success(true)
                .message("Total de productos activos recuperados con éxito")
                .data(count)
                .build();
        
        return ResponseEntity.ok(response);
    }
}