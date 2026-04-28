package com.example.Spring_ecom.controller;

import com.example.Spring_ecom.dto.AddToCartRequest;
import com.example.Spring_ecom.model.Cart;
import com.example.Spring_ecom.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    // ✅ Get logged-in user's cart using JWT
    @GetMapping
    public ResponseEntity<Cart> getUserCart() {

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();


        Cart cart = cartService.getCartByUsername(username);

        return ResponseEntity.ok(cart);
    }


    @PostMapping("/add")
    public ResponseEntity<Cart> addToCart(@RequestBody AddToCartRequest request) {

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Cart cart = cartService.addToCart(username, request);

        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/remove")
    public ResponseEntity<Cart> removeFromCart(@RequestBody AddToCartRequest request) {

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Cart cart = cartService.removeFromCart(username, request);

        return ResponseEntity.ok(cart);
    }
}