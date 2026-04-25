package com.example.Spring_ecom.service;

import com.example.Spring_ecom.dto.OrderRequest;
import com.example.Spring_ecom.dto.OrderResponse;
import com.example.Spring_ecom.exception.ResourceNotFoundException;
import com.example.Spring_ecom.model.*;
import com.example.Spring_ecom.repo.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepo orderRepo;
    private final UserRepo userRepo;
    private final AddressRepo addressRepo;
    private final CartRepo cartRepo;

    // -- Place Order --------------------------------------------
    @Transactional
    public OrderResponse placeOrder(String username, OrderRequest request) {

        // 1. Get user
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // 2. Get cart
        Cart cart = cartRepo.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        List<CartItem> cartItems = cart.getItems();
        if (cartItems.isEmpty())
            throw new ResourceNotFoundException("Cart is empty. Add items before placing order.");

        // 3. Get shipping address
        Address address = addressRepo.findById(request.getAddressId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        // 4. Build order items from cart
        List<OrderItem> orderItems = cartItems.stream()
                .map(cartItem -> OrderItem.builder()
                        .product(cartItem.getProduct())
                        .quantity(cartItem.getQuantity())
                        .priceAtPurchase(cartItem.getPrice())  // ✅ use CartItem.price
                        .build())
                .toList();

        // 5. Calculate total
        BigDecimal total = orderItems.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 6. Build order
        Order order = Order.builder()
                .user(user)
                .shippingAddress(address)
                .orderItems(orderItems)
                .totalAmount(total)
                .status(OrderStatus.PENDING)
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        // 7. Link each item back to the order
        orderItems.forEach(item -> item.setOrder(order));

        // 8. Save order (cascades to order items)
        Order saved = orderRepo.save(order);

        // 9. Clear cart
        cart.getItems().clear();
        cartRepo.save(cart);


        return toResponse(saved);
    }

    // -- Get My Orders -----------------------------------------
    @Transactional
    public List<OrderResponse> getOrdersByUser(String username) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return orderRepo.findByUser(user)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // -- Get Order By ID -----------------------------------------
    @Transactional
    public OrderResponse getOrderById(String username, Long orderId) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Order order = orderRepo.findByIdAndUser(orderId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        return toResponse(order);
    }

    // -- Cancel Order -------------------------------------------
    @Transactional
    public OrderResponse cancelOrder(String username, Long orderId) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Order order = orderRepo.findByIdAndUser(orderId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (order.getStatus() == OrderStatus.DELIVERED)
            throw new RuntimeException("Delivered orders cannot be cancelled");
        if (order.getStatus() == OrderStatus.CANCELLED)
            throw new RuntimeException("Order is already cancelled");

        order.setStatus(OrderStatus.CANCELLED);
        order.setPaymentStatus(PaymentStatus.REFUNDED);
        return toResponse(orderRepo.save(order));
    }

    // -- toResponse mapper ----------------------------------------
    private OrderResponse toResponse(Order order) {
        List<OrderResponse.OrderItemResponse> itemResponses = order.getOrderItems()
                .stream()
                .map(item -> OrderResponse.OrderItemResponse.builder()
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .quantity(item.getQuantity())
                        .priceAtPurchase(item.getPriceAtPurchase())
                        .subtotal(item.getSubtotal())
                        .build())
                .toList();

        return OrderResponse.builder()
                .id(order.getId())
                .orderDate(order.getOrderDate())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .paymentStatus(order.getPaymentStatus())
                .paymentMethod(order.getPaymentMethod())
                .items(itemResponses)
                .build();
    }
}