package com.example.Spring_ecom.service;

import com.example.Spring_ecom.dto.PaginatedResponse;
import com.example.Spring_ecom.dto.ProductResponse;
import com.example.Spring_ecom.model.Category;
import com.example.Spring_ecom.model.Product;
import com.example.Spring_ecom.repo.CtegoryRepo;
import com.example.Spring_ecom.repo.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepo productRepo;
    @Autowired
    private CtegoryRepo ctegoryRepo;

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

        // Set image fields
        product.setImagename(image.getOriginalFilename());
        product.setImagetype(image.getContentType());
        product.setImagepath(image.getBytes());

        // Fetch category from DB using incoming category id
        if (product.getCategory() != null && product.getCategory().getId() != null) {
            Long categoryId = product.getCategory().getId();

            Category category = ctegoryRepo.findById(categoryId)
                    .orElseThrow(() -> new RuntimeException("Category not found"));

            product.setCategory(category); // ✅ VERY IMPORTANT
        }

        return productRepo.save(product);
    }

    public Product updateProduct(int id, Product product, MultipartFile image) throws IOException {

        Product existingProduct = productRepo.findById((long) id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // 🔥 Update normal fields
        existingProduct.setName(product.getName());
        existingProduct.setBrand(product.getBrand());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
//        existingProduct.setStockQuantity(product.getStockQuantity());
        existingProduct.setReleaseDate(product.getReleaseDate());

        // 🔥 Update image only if new image is sent
        if (image != null && !image.isEmpty()) {
            existingProduct.setImagename(image.getOriginalFilename());
            existingProduct.setImagetype(image.getContentType());
            existingProduct.setImagepath(image.getBytes());
        }

        return productRepo.save(existingProduct);
    }

    public Product getImage(int id) {
        return productRepo.findById((long) id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public List<Product> getProductsByCategory(String categoryName) {
        return productRepo.findByCategory_NameIgnoreCase(categoryName);
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
