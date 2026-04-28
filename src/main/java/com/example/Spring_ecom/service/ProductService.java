package com.example.Spring_ecom.service;

import com.example.Spring_ecom.dto.PaginatedResponse;
import com.example.Spring_ecom.dto.ProductResponse;
import com.example.Spring_ecom.model.Category;
import com.example.Spring_ecom.model.Product;
import com.example.Spring_ecom.repo.CtegoryRepo;
import com.example.Spring_ecom.repo.ProductRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    @Autowired
    private ProductRepo productRepo;
    @Autowired
    private CtegoryRepo ctegoryRepo;

    private final CtegoryRepo categoryRepo;

    public PaginatedResponse<ProductResponse> getAllProducts(Pageable pageable) {

        Page<Product> pageProduct = productRepo.findAll(pageable);

        List<ProductResponse> content = pageProduct.getContent()
                .stream()
                .map(this::mapToResponse)
                .toList();

        return PaginatedResponse.<ProductResponse>builder()
                .content(content)
                .pageNumber(pageProduct.getNumber())
                .pageSize(pageProduct.getSize())
                .totalElements(pageProduct.getTotalElements())
                .totalPages(pageProduct.getTotalPages())
                .first(pageProduct.isFirst())
                .last(pageProduct.isLast())
                .numberOfElements(pageProduct.getNumberOfElements())
                .build();
    }

    public Product getProductById(int id) {
        return productRepo.findById((long) id).get();
    }

//    public Product addProduct(Product product, MultipartFile image) throws IOException {
//
//        product.setImagename(image.getOriginalFilename());
//        product.setImagetype(image.getContentType());
//        product.setImagepath(image.getBytes());
//
//        return productRepo.save(product);
//    }

//    public Product addProduct(Product product, MultipartFile image) throws IOException {
//
//        product.setImagename(image.getOriginalFilename());
//        product.setImagetype(image.getContentType());
//        product.setImagepath(image.getBytes());
//
//        return productRepo.save(product);
//    }

    public Product addProduct(Product product, MultipartFile image) throws IOException {

        // 1.Set image fields
        product.setImagename(image.getOriginalFilename());
        product.setImagetype(image.getContentType());
        product.setImagepath(image.getBytes());


        if (product.getAvailableQuantity() < 0) {
            throw new RuntimeException("Initial stock cannot be negative");
        }

        // 2.Fetch category from DB using incoming category id
        if (product.getCategory() != null && product.getCategory().getId() != null) {
            Long categoryId = product.getCategory().getId();

            Category category = ctegoryRepo.findById(categoryId)
                    .orElseThrow(() -> new RuntimeException("Category not found"));

            product.setCategory(category); // ✅ VERY IMPORTANT

        }
        // 3. Set Availability Status automatically based on quantity
        product.setProductAvailable(product.getAvailableQuantity() > 0);


        return productRepo.save(product);
    }

    @Transactional
    public Product updateProduct(Long id, Product product, MultipartFile image) throws IOException {

        // 1. Fetch existing product
        Product existingProduct = productRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        // 2. Map Basic Fields (Check for nulls if necessary)
        existingProduct.setName(product.getName());
        existingProduct.setBrand(product.getBrand());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setReleaseDate(product.getReleaseDate());
        existingProduct.setAvailableQuantity(product.getAvailableQuantity());

        // Auto-calculate availability
        existingProduct.setProductAvailable(product.getAvailableQuantity() > 0);

        // 3. Robust Category Update
        // Check product.getCategory() != null first to avoid NullPointerException
        if (product.getCategory() != null && product.getCategory().getId() != null) {
            Category category = categoryRepo.findById(product.getCategory().getId())
                    .orElseThrow(() -> new RuntimeException("Category not found with ID: " + product.getCategory().getId()));
            existingProduct.setCategory(category);
        }

        // 4. Update Image only if a new one is provided
        if (image != null && !image.isEmpty()) {
            existingProduct.setImagename(image.getOriginalFilename());
            existingProduct.setImagetype(image.getContentType());
            existingProduct.setImagepath(image.getBytes());
        }

        // 5. Final Save
        return productRepo.save(existingProduct);
    }

    public Product getImage(int id) {
        return productRepo.findById((long) id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public List<Product> getProductsByCategory(String categoryName) {
        return productRepo.findByCategory_NameIgnoreCase(categoryName);
    }

    public List<Product> getProductsByPriceRange(double min, double max) {
        return productRepo.findByPriceBetweenOrderByPriceAsc(min, max);
    }


    private ProductResponse mapToResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .brand(product.getBrand())
                .description(product.getDescription())
                .price(product.getPrice())
                .releaseDate(product.getReleaseDate())
                .productAvailable(product.isProductAvailable())
                .availableQuantity(product.getAvailableQuantity())
                .imagename(product.getImagename())
                .imagetype(product.getImagetype())
                .imageUrl("/product/" + product.getId() + "/image")
                .build();
    }
}
