package com.example.Spring_ecom.service;

import com.example.Spring_ecom.model.Order;
import com.example.Spring_ecom.model.OrderItem;
import com.example.Spring_ecom.model.Product;
import com.example.Spring_ecom.model.dto.*;
import com.example.Spring_ecom.repo.OrderRepo;
import com.example.Spring_ecom.repo.ProductRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private ProductRepo productRepo;

    @Transactional
    public OrderResponse placeOrder(OrderRequest request) {

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new RuntimeException("Order items cannot be empty");
        }

        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemRequest itemReq : request.getItems()) {

            Product product = productRepo.findById(Long.valueOf(itemReq.getProductId()))
                    .orElseThrow(() -> new RuntimeException(
                            "Product not found with ID: " + itemReq.getProductId()));

            OrderItem item = new OrderItem();
            item.setProduct(product);
            item.setQuantity(itemReq.getQuantity());

            BigDecimal price = product.getPrice()
                    .multiply(BigDecimal.valueOf(itemReq.getQuantity()));

//            item.setPrice(price);
            item.setOrder(order);

            totalAmount = totalAmount.add(price);
            orderItems.add(item);
        }

        order.setOrderItems(orderItems);
        order.setTotalAmount(totalAmount);

        orderRepo.save(order);

        OrderResponse response = new OrderResponse();
        response.setOrderId(order.getId());
        response.setTotalAmount(totalAmount);
        response.setMessage("Order placed successfully");

        return response;
    }

    public List<OrderHistoryResponse> getAllOrders() {

        List<Order> orders = orderRepo.findAll();

        return orders.stream().map(order -> {

            OrderHistoryResponse res = new OrderHistoryResponse();
            res.setOrderId(order.getId());
            res.setTotalAmount(order.getTotalAmount().doubleValue());
            res.setOrderDate(order.getOrderDate().toLocalDate());
            res.setStatus("PLACED");

            // ✅ MAP ORDER ITEMS
            List<OrderItemResponse> items = order.getOrderItems().stream()
                    .map(item -> new OrderItemResponse(
                            item.getProduct().getName(),
                            item.getQuantity(),
                            item.getPrice()
                    ))
                    .toList();

            res.setItems(items);

            return res;

        }).toList();
    }
}