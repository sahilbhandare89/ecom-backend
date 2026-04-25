package com.example.Spring_ecom.repo;

import com.example.Spring_ecom.model.Order;
import com.example.Spring_ecom.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepo extends JpaRepository<Order,Long> {
    Optional<Order> findById(Long id);

    List<Order> findByUser(User user);

    Optional<Order> findByIdAndUser(Long orderId, User user);
}
