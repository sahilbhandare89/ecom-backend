package com.example.Spring_ecom.repo;

import com.example.Spring_ecom.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepo extends JpaRepository<Product,Long>, JpaSpecificationExecutor<Product> {
    List<Product> findByCategory_NameIgnoreCase(String name);


    List<Product> findByPriceBetweenOrderByPriceAsc(Double minPrice,Double maxPrice);

//
//    @Query("SELECT p FROM Product p JOIN p.category c WHERE " +
//            "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
//            "LOWER(p.brand) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
//            "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
//            "LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
//    Page<Product> searchEntireDB(@Param("keyword") String keyword, Pageable pageable);


    @Query("""
        SELECT p
        FROM Product p
        LEFT JOIN p.category c
        WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(p.brand) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
       """)
    Page<Product> searchEntireDB(@Param("keyword") String keyword, Pageable pageable);

//    @Modifying
//    @Query("UPDATE Product p SET p.availableQuantity = p.availableQuantity - :qty " +
//            "WHERE p.id = :id AND p.availableQuantity >= :qty")
//    int decreaseStock(@Param("id") Long id, @Param("qty") int qty);
}
