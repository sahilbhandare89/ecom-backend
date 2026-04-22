package com.example.Spring_ecom.repo;

import com.example.Spring_ecom.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CtegoryRepo extends JpaRepository<Category,Long> {

    Optional<Category> findByName(String name);
}
