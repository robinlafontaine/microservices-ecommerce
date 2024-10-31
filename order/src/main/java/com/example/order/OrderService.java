package com.example.order;

import com.example.order.inventory.InventoryClient;
import com.example.order.inventory.InventoryException;
import com.example.order.payment.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final PaymentClient paymentClient;
    private final InventoryClient inventoryClient;
    private final OrderRepository orderRepository;

    public OrderService(PaymentClient paymentClient, InventoryClient inventoryClient, OrderRepository orderRepository) {
        this.paymentClient = paymentClient;
        this.inventoryClient = inventoryClient;
        this.orderRepository = orderRepository;
    }

    public Order createOrder(Order order) throws Exception {

        boolean isAvailable = inventoryClient.checkStock(order.getItems());
        if (!isAvailable) {
            throw new InventoryException("Insufficient stock for one or more items.");
        }

        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setOrderId(order.getId());
        paymentRequest.setAmount(order.getTotalAmount());
        paymentRequest.setPaymentMethodId("1");

        PaymentResponse paymentResponse = paymentClient.initiatePayment(paymentRequest);
        if (paymentResponse.getStatus() != PaymentStatus.PENDING) {
            throw new PaymentException("Payment initiation failed.");
        }

        order.setStatus(OrderStatus.PENDING);
        order.setPaymentId(paymentResponse.getPaymentId());
        return orderRepository.save(order);
    }

    public List<Order> getOrders() {
        return orderRepository.findAll();
    }

    public Order getOrder(Long orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }

    public void updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null) {
            order.setStatus(status);
            orderRepository.save(order);
        }
    }

}
