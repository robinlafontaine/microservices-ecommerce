package com.example.order;

import com.example.order.auth.AuthClient;
import com.example.order.inventory.InventoryClient;
import com.example.order.inventory.InventoryException;
import com.example.order.payment.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;

@Service
public class OrderService {

    private final PaymentClient paymentClient;
    private final InventoryClient inventoryClient;
    private final OrderRepository orderRepository;

    Logger logger = Logger.getLogger(OrderService.class.getName());

    public OrderService(PaymentClient paymentClient, InventoryClient inventoryClient, OrderRepository orderRepository) {
        this.paymentClient = paymentClient;
        this.inventoryClient = inventoryClient;
        this.orderRepository = orderRepository;
    }

    public PaymentResponse createOrder(Order order) throws Exception {

        boolean isAvailable = inventoryClient.checkStock(order.getItems());
        if (!isAvailable) {
            throw new InventoryException("Insufficient stock for one or more items.");
        }

        boolean isReserved = inventoryClient.reserveStock(order.getItems());
        if (!isReserved) {
            throw new InventoryException("Failed to reserve stock for one or more items.");
        }

        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setOrderId(order.getId());
        paymentRequest.setAmount(order.getTotalAmount());
        paymentRequest.setCurrency("eur");

        try {
            PaymentResponse paymentResponse = paymentClient.initiatePayment(paymentRequest);
            if (paymentResponse.getStatus() != PaymentStatus.PENDING) {
                inventoryClient.freeStock(order.getItems());
                throw new PaymentException("Payment initiation failed.");
            }

            order.setStatus(OrderStatus.PENDING);
            order.setPaymentId(paymentResponse.getPaymentId());
            orderRepository.save(order);

            return paymentResponse;
        } catch (Exception e) {
            inventoryClient.freeStock(order.getItems());
            throw new PaymentException("Order creation failed: " + e.getMessage(), e);
        }
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
