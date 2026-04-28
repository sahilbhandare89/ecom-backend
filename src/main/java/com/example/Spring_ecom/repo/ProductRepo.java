package com.example.Spring_ecom.repo;

import com.example.Spring_ecom.model.Product;
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

//    @Modifying
//    @Query("UPDATE Product p SET p.availableQuantity = p.availableQuantity - :qty " +
//            "WHERE p.id = :id AND p.availableQuantity >= :qty")
//    int decreaseStock(@Param("id") Long id, @Param("qty") int qty);
}
