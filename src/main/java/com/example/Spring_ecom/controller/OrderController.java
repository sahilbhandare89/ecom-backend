package com.example.Spring_ecom.controller;

import com.example.Spring_ecom.model.dto.OrderHistoryResponse;
import com.example.Spring_ecom.model.dto.OrderRequest;
import com.example.Spring_ecom.model.dto.OrderResponse;
import com.example.Spring_ecom.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/orders")
    public ResponseEntity<OrderResponse> placeOrder(@RequestBody OrderRequest request) {
        return new ResponseEntity<>(orderService.placeOrder(request), HttpStatus.CREATED);
    }

    // ✅ 2. GET ALL ORDERS (for your Orders page)
    @GetMapping("/orders")
    public ResponseEntity<List<OrderHistoryResponse>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }
}