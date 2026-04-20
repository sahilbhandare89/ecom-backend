package com.example.Spring_ecom.model.dto;


public record OrderItemResponse(
        String productName,
        int quantity,
        double totalPrice
) {}
