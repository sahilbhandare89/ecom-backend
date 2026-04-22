package com.example.Spring_ecom.controller;

import com.example.Spring_ecom.model.Category;
import com.example.Spring_ecom.model.Product;
import com.example.Spring_ecom.service.CategoryService;
import com.example.Spring_ecom.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final ProductService productService;

    @PostMapping
    public Category create(@RequestBody Category category) {
        return categoryService.createCategory(category.getName());
    }

    @GetMapping
    public List<Category> getAll() {
        return categoryService.getAllCategories();
    }


}
