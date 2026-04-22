package com.example.Spring_ecom.service;

import com.example.Spring_ecom.model.Category;
import com.example.Spring_ecom.model.Product;
import com.example.Spring_ecom.repo.CtegoryRepo;
import com.example.Spring_ecom.repo.ProductRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CtegoryRepo categoryRepository;
    private final ProductRepo productRepo;

    public Category createCategory(String name) {
        return categoryRepository.save(
                Category.builder().name(name).build()
        );
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }


}
