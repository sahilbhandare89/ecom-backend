package com.example.Spring_ecom.controller;

import com.example.Spring_ecom.dto.PaginatedResponse;
import com.example.Spring_ecom.dto.ProductResponse;
import com.example.Spring_ecom.model.Product;
import com.example.Spring_ecom.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper; // ✅ correct
import io.jsonwebtoken.io.IOException;
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
    //for admin only
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
    //For admin only
    @PutMapping(value = "/admin/product/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProduct(
            @PathVariable Long id, // Changed to Long to match Entity
            @RequestPart("product") Product product,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        try {
            // Ensure the ID from the URL is the one being used
            Product updated = productService.updateProduct(id, product, image);
            return ResponseEntity.ok(updated);

        } catch (RuntimeException e) {
            // Check if it's a "Not Found" case
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.badRequest().body(e.getMessage());

        } catch (java.io.IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Image upload failed: " + e.getMessage());
        }catch (Exception e) {
            // This will catch the Hibernate, Jackson, and Logic errors
            Throwable rootCause = e;
            while (rootCause.getCause() != null) {
                rootCause = rootCause.getCause();
            }
            System.err.println("🔥 REAL ERROR: " + rootCause.getMessage());
            rootCause.printStackTrace();

            return ResponseEntity.status(500).body("Backend Error: " + rootCause.getMessage());
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

    @GetMapping("/category/{name}")
    public ResponseEntity<List<Product>> getByCategory(@PathVariable String name) {
        return ResponseEntity.ok(productService.getProductsByCategory(name));
    }

    @GetMapping("/price-range")
    public List<Product> getProductsByPriceRange(
            @RequestParam double min,
            @RequestParam double max) {

        return productService.getProductsByPriceRange(min, max);
    }
}