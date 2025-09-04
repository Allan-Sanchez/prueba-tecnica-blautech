package com.example.prueba_tecnica_api_rest.service;

import com.example.prueba_tecnica_api_rest.dto.CreateProductRequest;
import com.example.prueba_tecnica_api_rest.dto.ProductDto;
import com.example.prueba_tecnica_api_rest.dto.UpdateProductRequest;
import com.example.prueba_tecnica_api_rest.entity.Product;
import com.example.prueba_tecnica_api_rest.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductService {
    
    private final ProductRepository productRepository;
    
    @Transactional(readOnly = true)
    public List<ProductDto> getAllActiveProducts() {
        log.debug("Getting all active products");
        return productRepository.findByIsActiveTrue()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public Page<ProductDto> getActiveProducts(Pageable pageable) {
        log.debug("Getting active products with pagination: {}", pageable);
        return productRepository.findByIsActiveTrue(pageable)
                .map(this::convertToDto);
    }
    
    @Transactional(readOnly = true)
    public Optional<ProductDto> getActiveProductById(Long id) {
        log.debug("Getting active product by id: {}", id);
        return productRepository.findByIdAndIsActiveTrue(id)
                .map(this::convertToDto);
    }
    
    @Transactional(readOnly = true)
    public Page<ProductDto> searchActiveProducts(String search, Pageable pageable) {
        log.debug("Searching active products with term: {}", search);
        return productRepository.findActiveProductsBySearch(search, pageable)
                .map(this::convertToDto);
    }
    
    @Transactional(readOnly = true)
    public Page<ProductDto> getActiveProductsByPriceRange(Double minPrice, Double maxPrice, Pageable pageable) {
        log.debug("Getting active products by price range: {} - {}", minPrice, maxPrice);
        Integer minPriceCents = (int) Math.round(minPrice * 100);
        Integer maxPriceCents = (int) Math.round(maxPrice * 100);
        return productRepository.findActiveProductsByPriceRange(minPriceCents, maxPriceCents, pageable)
                .map(this::convertToDto);
    }
    
    public ProductDto createProduct(CreateProductRequest request) {
        log.info("Creating new product: {}", request.getName());
        
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .priceCents((int) Math.round(request.getPrice() * 100))
                .currency(request.getCurrency() != null ? request.getCurrency() : "GTQ")
                .isActive(true)
                .build();
        
        Product savedProduct = productRepository.save(product);
        log.info("Product created with id: {}", savedProduct.getId());
        
        return convertToDto(savedProduct);
    }
    
    public Optional<ProductDto> updateProduct(Long id, UpdateProductRequest request) {
        log.info("Updating product with id: {}", id);
        
        return productRepository.findById(id)
                .map(product -> {
                    if (request.getName() != null) {
                        product.setName(request.getName());
                    }
                    if (request.getDescription() != null) {
                        product.setDescription(request.getDescription());
                    }
                    if (request.getImageUrl() != null) {
                        product.setImageUrl(request.getImageUrl());
                    }
                    if (request.getPrice() != null) {
                        product.setPriceCents((int) Math.round(request.getPrice() * 100));
                    }
                    if (request.getCurrency() != null) {
                        product.setCurrency(request.getCurrency());
                    }
                    if (request.getIsActive() != null) {
                        product.setIsActive(request.getIsActive());
                    }
                    
                    Product updatedProduct = productRepository.save(product);
                    log.info("Product updated with id: {}", updatedProduct.getId());
                    
                    return convertToDto(updatedProduct);
                });
    }
    
    public boolean deactivateProduct(Long id) {
        log.info("Deactivating product with id: {}", id);
        
        return productRepository.findById(id)
                .map(product -> {
                    product.setIsActive(false);
                    productRepository.save(product);
                    log.info("Product deactivated with id: {}", id);
                    return true;
                })
                .orElse(false);
    }
    
    @Transactional(readOnly = true)
    public Long countActiveProducts() {
        return productRepository.countActiveProducts();
    }
    
    private ProductDto convertToDto(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .imageUrl(product.getImageUrl())
                .price(product.getPriceInCurrency())
                .currency(product.getCurrency())
                .isActive(product.getIsActive())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}