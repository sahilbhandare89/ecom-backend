package com.example.Spring_ecom.model.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class OrderHistoryResponse {

    private Long orderId;
    private Double totalAmount;
    private LocalDate orderDate;
    private String status;

    private List<OrderItemResponse> items; // ✅ ADD THIS
}