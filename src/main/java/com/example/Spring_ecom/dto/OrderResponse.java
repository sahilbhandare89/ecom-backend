package com.example.Spring_ecom.dto;

import com.example.Spring_ecom.model.OrderStatus;
import com.example.Spring_ecom.model.PaymentStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// dto/OrderResponse.java
@Data
@Builder
public class OrderResponse {
    private Long id;
    private LocalDateTime orderDate;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private PaymentStatus paymentStatus;
    private String paymentMethod;
    private List<OrderItemResponse> items;

    @Data
    @Builder
    public static class OrderItemResponse {
        private Long productId;
        private String productName;
        private int quantity;
        private BigDecimal priceAtPurchase;
        private BigDecimal subtotal;
    }
}