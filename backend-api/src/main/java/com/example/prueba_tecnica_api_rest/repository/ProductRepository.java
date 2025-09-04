package com.example.prueba_tecnica_api_rest.repository;

import com.example.prueba_tecnica_api_rest.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    List<Product> findByIsActiveTrue();
    
    Page<Product> findByIsActiveTrue(Pageable pageable);
    
    Optional<Product> findByIdAndIsActiveTrue(Long id);
    
    @Query("SELECT p FROM Product p WHERE p.isActive = true AND " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Product> findActiveProductsBySearch(@Param("search") String search, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.isActive = true AND p.priceCents BETWEEN :minPrice AND :maxPrice")
    Page<Product> findActiveProductsByPriceRange(@Param("minPrice") Integer minPrice, 
                                                 @Param("maxPrice") Integer maxPrice, 
                                                 Pageable pageable);
    
    @Query("SELECT COUNT(p) FROM Product p WHERE p.isActive = true")
    Long countActiveProducts();
}