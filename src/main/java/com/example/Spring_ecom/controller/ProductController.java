package com.example.Spring_ecom.controller;

import com.example.Spring_ecom.dto.PaginatedResponse;
import com.example.Spring_ecom.dto.ProductResponse;
import com.example.Spring_ecom.model.Product;
import com.example.Spring_ecom.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper; // ✅ correct
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    // ✅ GET all products
    @GetMapping("/product")
    public ResponseEntity<PaginatedResponse<ProductResponse>> getAllProducts(
            @PageableDefault(size = 10,sort = "id",direction = Sort.Direction.ASC)Pageable pageable
            ) {
        PaginatedResponse<ProductResponse> page=productService.getAllProducts(pageable);
        return new ResponseEntity<>(page,HttpStatus.OK);
    }

    // ✅ GET product by ID
    @GetMapping("/product/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable int id) {
        Product product = productService.getProductById(id);
        return product != null
                ? ResponseEntity.ok(product)
                : ResponseEntity.notFound().build();
    }

    // ✅ ADD product with image
    @PostMapping(value = "/admin/product", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addProduct(
            @RequestPart("product") String productJson,
            @RequestPart("image") MultipartFile image) {
        try {
            Product product = objectMapper.readValue(productJson, Product.class);
            Product saved = productService.addProduct(product, image);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    // ✅ UPDATE product
    @PutMapping(value = "/admin/product/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProduct(
            @PathVariable int id,
            @RequestPart("product") Product product,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        try {
            Product updated = productService.updateProduct(id, product, image);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    // ✅ GET product image
    @GetMapping("/product/{id}/image")
    public ResponseEntity<byte[]> getImage(@PathVariable int id) {
        Product product = productService.getImage(id);

        if (product == null || product.getImagepath() == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(product.getImagetype()))
                .body(product.getImagepath());
    }
}