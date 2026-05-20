package com.example.Spring_ecom.service;

import com.example.Spring_ecom.model.Category;
import com.example.Spring_ecom.model.Product;
import com.example.Spring_ecom.repo.CtegoryRepo;
import com.example.Spring_ecom.repo.ProductRepo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Slf4j
class ProductServiceTest {

    @Mock
    private ProductRepo productRepo;

    @Mock
    private CtegoryRepo ctegoryRepo;

    @Mock
    private MultipartFile image;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private Category category;

    // ─── Common Setup ────────────────────────────────────────────────────────────

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        product = new Product();
        product.setName("Laptop");
        product.setBrand("Dell");
        product.setDescription("Gaming Laptop");
//        product.setPrice(75000);
        product.setAvailableQuantity(10);
        product.setCategory(category);
    }

    // ─── TC1: Happy Path ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC1: Should save product successfully with valid inputs")
    void addProduct_validInputs_shouldSaveAndReturn() throws IOException {

        log.info(" Strted");
        // Arrange
        when(image.getOriginalFilename()).thenReturn("laptop.jpg");
        when(image.getContentType()).thenReturn("image/jpeg");
        when(image.getBytes()).thenReturn(new byte[]{1, 2, 3});
        when(ctegoryRepo.findById(1l)).thenReturn(Optional.of(category));
        when(productRepo.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        log.info("category object",category);
        // Act
        Product result = productService.addProduct(product, image);

        log.info("add product is executed",result);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Laptop");
        assertThat(result.isProductAvailable()).isTrue();
        assertThat(result.getCategory().getName()).isEqualTo("Electronics");

        verify(productRepo, times(1)).save(product);
    }




}