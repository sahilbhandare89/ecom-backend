package com.example.Spring_ecom.controller;

import com.example.Spring_ecom.dto.OrderRequest;
import com.example.Spring_ecom.dto.OrderResponse;
import com.example.Spring_ecom.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/orders")
@CrossOrigin("*")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // -- get username form jwt token---------
    private String getUsername(){
       return SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
    }

    // -- place order------------------------------
    @PostMapping("/place")
    public ResponseEntity<OrderResponse> placeOrder(@RequestBody OrderRequest request){
        String username=getUsername();
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.placeOrder(username,request));
    }

    // -- Get My Orders----------------------------
    @GetMapping("/my-orders")
    public ResponseEntity<List<OrderResponse>> getMyOrders() {
        String username = getUsername();
        return ResponseEntity.ok(orderService.getOrdersByUser(username));
    }

    // -- Get Order By ID --------------------------------
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long orderId) {
        String username = getUsername();
        return ResponseEntity.ok(orderService.getOrderById(username, orderId));
    }

    // -- Cancel Order -----------------------------------
    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable Long orderId) {
        String username = getUsername();
        return ResponseEntity.ok(orderService.cancelOrder(username, orderId));
    }



}