package com.pruebatecnica.productservice.service;

import com.pruebatecnica.productservice.entity.Product;
import com.pruebatecnica.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductService {
    
    private final ProductRepository productRepository;
    
    @Transactional(readOnly = true)
    public List<Product> getAllActiveProducts() {
        log.info("Fetching all active products from database");
        List<Product> products = productRepository.findByIsActiveTrueOrderByCreatedAtDesc();
        log.info("Found {} active products", products.size());
        return products;
    }
    
    @Transactional(readOnly = true)
    public Optional<Product> getProductById(Long id) {
        log.info("Fetching product with ID: {}", id);
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent() && product.get().getIsActive()) {
            log.info("Found active product with ID: {}", id);
            return product;
        }
        log.warn("Product with ID {} not found or inactive", id);
        return Optional.empty();
    }
    
    @Transactional(readOnly = true)
    public List<Product> searchProducts(String searchTerm) {
        log.info("Searching products with term: {}", searchTerm);
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllActiveProducts();
        }
        
        List<Product> products = productRepository.findActiveProductsBySearchTerm(searchTerm.trim());
        log.info("Found {} products matching search term: {}", products.size(), searchTerm);
        return products;
    }
    
    @Transactional(readOnly = true)
    public List<Product> getProductsByPriceRange(Double minPrice, Double maxPrice) {
        log.info("Fetching products with price range: {} - {}", minPrice, maxPrice);
        
        Integer minPriceCents = minPrice != null ? (int) Math.round(minPrice * 100) : 0;
        Integer maxPriceCents = maxPrice != null ? (int) Math.round(maxPrice * 100) : Integer.MAX_VALUE;
        
        List<Product> products = productRepository.findByIsActiveTrueAndPriceCentsBetweenOrderByPriceCentsAsc(minPriceCents, maxPriceCents);
        log.info("Found {} products in price range", products.size());
        return products;
    }
    
    public Product createProduct(Product product) {
        log.info("Creating new product: {}", product.getName());
        
        if (product.getIsActive() == null) {
            product.setIsActive(true);
        }
        
        if (product.getCurrency() == null || product.getCurrency().trim().isEmpty()) {
            product.setCurrency("GTQ");
        }
        
        Product savedProduct = productRepository.save(product);
        log.info("Product created with ID: {}", savedProduct.getId());
        return savedProduct;
    }
    
    public Product updateProduct(Long id, Product productUpdate) {
        log.info("Updating product with ID: {}", id);
        
        Optional<Product> existingProductOpt = productRepository.findById(id);
        if (existingProductOpt.isEmpty()) {
            throw new IllegalArgumentException("Producto no encontrado con ID: " + id);
        }
        
        Product existingProduct = existingProductOpt.get();
        
        if (productUpdate.getName() != null) {
            existingProduct.setName(productUpdate.getName());
        }
        if (productUpdate.getDescription() != null) {
            existingProduct.setDescription(productUpdate.getDescription());
        }
        if (productUpdate.getImageUrl() != null) {
            existingProduct.setImageUrl(productUpdate.getImageUrl());
        }
        if (productUpdate.getPriceCents() != null) {
            existingProduct.setPriceCents(productUpdate.getPriceCents());
        }
        if (productUpdate.getCurrency() != null) {
            existingProduct.setCurrency(productUpdate.getCurrency());
        }
        if (productUpdate.getIsActive() != null) {
            existingProduct.setIsActive(productUpdate.getIsActive());
        }
        
        Product savedProduct = productRepository.save(existingProduct);
        log.info("Product updated with ID: {}", savedProduct.getId());
        return savedProduct;
    }
    
    public void deleteProduct(Long id) {
        log.info("Soft deleting product with ID: {}", id);
        
        Optional<Product> productOpt = productRepository.findById(id);
        if (productOpt.isEmpty()) {
            throw new IllegalArgumentException("Producto no encontrado con ID: " + id);
        }
        
        Product product = productOpt.get();
        product.setIsActive(false);
        productRepository.save(product);
        
        log.info("Product with ID {} marked as inactive", id);
    }
}