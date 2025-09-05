package com.pruebatecnica.productservice.repository;

import com.pruebatecnica.productservice.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    List<Product> findByIsActiveTrueOrderByCreatedAtDesc();
    
    @Query("SELECT p FROM Product p WHERE p.isActive = true AND " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "ORDER BY p.createdAt DESC")
    List<Product> findActiveProductsBySearchTerm(String searchTerm);
    
    List<Product> findByIsActiveTrueAndPriceCentsBetweenOrderByPriceCentsAsc(Integer minPrice, Integer maxPrice);
}