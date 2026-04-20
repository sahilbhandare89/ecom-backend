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

//    public Cart addItemToCart(String username, Long productId, int quantity) {
//
//        User user = userRepo.findByUsername(username)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        Cart cart = cartRepo.findByUserId(user.getId())
//                .orElseGet(() -> {
//                    Cart newCart = new Cart();
//                    newCart.setUser(user);
//                    return cartRepo.save(newCart);
//                });
//
//        Product product = productRepo.findById(productId)
//                .orElseThrow(() -> new RuntimeException("Product not found"));
//
//        Optional<CartItem> existingItem =
//                cartItemRepo.findByCartIdAndProductId(cart.getId(), productId);
//
//        if (existingItem.isPresent()) {
//            CartItem item = existingItem.get();
//            item.setQuantity(item.getQuantity() + quantity);
//            cartItemRepo.save(item);
//        } else {
//            CartItem newItem = new CartItem();
//            newItem.setCart(cart);
//            newItem.setProduct(product);
//            newItem.setQuantity(quantity);
//            newItem.setPrice(product.getPrice());
//
//            cartItemRepo.save(newItem);
//        }
//
//        return cart;
//    }

    public Cart addToCart(String username, AddToCartRequest request) {

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

        // Check if product already exists in cart
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
        } else {
            CartItem item = new CartItem();
            item.setCart(cart);
            item.setProduct(product);
            item.setQuantity(request.getQuantity());
            item.setPrice(product.getPrice());

            cart.getItems().add(item);
        }

        return cartRepo.save(cart);
    }

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

        // Logic: Decrement quantity or remove completely
        if (itemToRemove.getQuantity() > request.getQuantity()) {
            itemToRemove.setQuantity(itemToRemove.getQuantity() - request.getQuantity());
        } else {
            // If removing more than or equal to what is there, delete the item
            cart.getItems().remove(itemToRemove);
        }

        return cartRepo.save(cart);
    }
}