package com.example.Spring_ecom.dto;

import lombok.Data;

import java.util.List;

// dto/OrderRequest.java
@Data
public class OrderRequest {
    private Long addressId;
    private String paymentMethod;         // COD, UPI, CARD

    @Data
    public static class OrderItemRequest {
        private Long productId;
        private int quantity;
    }
}