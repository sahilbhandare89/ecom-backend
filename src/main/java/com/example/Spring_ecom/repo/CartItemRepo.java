package com.example.Spring_ecom.repo;

import com.example.Spring_ecom.model.CartItem;
import com.example.Spring_ecom.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepo extends JpaRepository<CartItem,Long> {
    Optional<CartItem> findByCartIdAndProductId(Long id, Long productId);


}
