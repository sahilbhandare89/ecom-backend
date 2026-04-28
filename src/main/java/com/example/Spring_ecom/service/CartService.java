package com.example.Spring_ecom.service;

import com.example.Spring_ecom.dto.AddToCartRequest;
import com.example.Spring_ecom.model.Cart;
import com.example.Spring_ecom.model.CartItem;
import com.example.Spring_ecom.model.Product;
import com.example.Spring_ecom.model.User;
import com.example.Spring_ecom.repo.CartItemRepo;
import com.example.Spring_ecom.repo.CartRepo;
import com.example.Spring_ecom.repo.ProductRepo;
import com.example.Spring_ecom.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@Transactional
public class CartService {

    @Autowired
    private CartRepo cartRepo;

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private CartItemRepo cartItemRepo;


    @Autowired
    private UserRepo userRepo;


    // -- find the cart by username -----------------------------------------
    public Cart getCartByUsername(String username) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));


        return cartRepo.findByUserId(user.getId())

                .orElseGet(() -> {

                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepo.save(newCart);
                });
    }


    // -- user can add product to the cart -------------------------------------------
    public Cart addToCart(String username, AddToCartRequest request) {

        if (request.getQuantity() <= 0) {
            throw new RuntimeException("Invalid quantity. You must add at least 1 item.");
        }


        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartRepo.findByUserId(user.getId())
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepo.save(newCart);
                });

        Product product = productRepo.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Check if product is even marked as available
        if (!product.isProductAvailable()) {
            throw new RuntimeException("This product is currently out of stock.");
        }

        // Check if we have enough stock for the request
        if (product.getAvailableQuantity() < request.getQuantity()) {
            throw new RuntimeException("Only " + product.getAvailableQuantity() + " items left in stock.");
        }

        // Check if product already exists in cart
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();

            // -- Update the TOTAL quantity
            int updatedQuantity = item.getQuantity() + request.getQuantity();
            item.setQuantity(updatedQuantity);

            // -- Multiply unit price by the NEW total quantity
            BigDecimal unitPrice = item.getProduct().getPrice();
            item.setPrice(unitPrice.multiply(BigDecimal.valueOf(updatedQuantity)));

        } else {
            CartItem item = new CartItem();
            item.setCart(cart);
            item.setProduct(product);
            item.setQuantity(request.getQuantity());

            BigDecimal newPrice = product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity()));
            item.setPrice(newPrice);

            cart.getItems().add(item);
        }



        return cartRepo.save(cart);
    }

    // -- user can remove prodyuct from the cart------------------------------------------------
    public Cart removeFromCart(String username, AddToCartRequest request) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartRepo.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        // Find the item in the cart
        CartItem itemToRemove = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(request.getProductId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Product not in cart"));


        // -- Logic: Decrement quantity or remove completely--
        if (itemToRemove.getQuantity() > request.getQuantity()) {
            // 1. Update Quantity
            int newQuantity = itemToRemove.getQuantity() - request.getQuantity();
            itemToRemove.setQuantity(newQuantity);

            // -- decrement price --

            BigDecimal unitPrice = itemToRemove.getProduct().getPrice();
            itemToRemove.setPrice(unitPrice.multiply(BigDecimal.valueOf(newQuantity)));

        }else {
            // If removing more than or equal to what is there, delete the item
            cart.getItems().remove(itemToRemove);
        }

        return cartRepo.save(cart);
    }
}