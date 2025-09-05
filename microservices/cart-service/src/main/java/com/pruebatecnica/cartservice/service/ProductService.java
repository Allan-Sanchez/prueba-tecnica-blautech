package com.pruebatecnica.cartservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    
    private final DiscoveryClient discoveryClient;
    private final RestTemplate restTemplate = new RestTemplate();
    
    public ProductInfo getProductInfo(Long productId) {
        try {
            String productServiceUrl = getProductServiceUrl();
            if (productServiceUrl == null) {
                log.error("Product service not available");
                return null;
            }
            
            String url = productServiceUrl + "/api/products/" + productId;
            log.info("Fetching product info from: {}", url);
            
            // Call product service
            ApiResponse<ProductInfo> response = restTemplate.getForObject(url, ApiResponse.class);
            
            if (response != null && response.isSuccess() && response.getData() != null) {
                // Parse the product data manually since we're getting a generic response
                @SuppressWarnings("unchecked")
                var productData = (java.util.Map<String, Object>) response.getData();
                
                return ProductInfo.builder()
                        .id(Long.valueOf(productData.get("id").toString()))
                        .name((String) productData.get("name"))
                        .imageUrl((String) productData.get("imageUrl"))
                        .priceCents((Integer) productData.get("priceCents"))
                        .currency((String) productData.get("currency"))
                        .isActive((Boolean) productData.get("isActive"))
                        .build();
            }
            
            log.warn("Product with ID {} not found or inactive", productId);
            return null;
            
        } catch (RestClientException e) {
            log.error("Error fetching product info for ID {}: {}", productId, e.getMessage());
            return null;
        } catch (Exception e) {
            log.error("Unexpected error fetching product info for ID {}: {}", productId, e.getMessage());
            return null;
        }
    }
    
    private String getProductServiceUrl() {
        try {
            List<ServiceInstance> instances = discoveryClient.getInstances("product-service");
            if (instances != null && !instances.isEmpty()) {
                ServiceInstance instance = instances.get(0);
                return instance.getUri().toString();
            }
            
            log.warn("No instances of product-service found");
            return null;
            
        } catch (Exception e) {
            log.error("Error getting product service URL: {}", e.getMessage());
            return null;
        }
    }
    
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class ProductInfo {
        private Long id;
        private String name;
        private String imageUrl;
        private Integer priceCents;
        private String currency;
        private Boolean isActive;
    }
    
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class ApiResponse<T> {
        private boolean success;
        private int httpStatus;
        private String appCode;
        private String message;
        private T data;
    }
}