package com.example.order;

import com.example.order.payment.PaymentResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderService orderService;

    Logger logger = Logger.getLogger(OrderController.class.getName());

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/create")
    public ResponseEntity<PaymentResponse> createOrder(@RequestBody Order order) throws Exception {
        logger.info("Creating order: " + order);
        PaymentResponse orderPayment = orderService.createOrder(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderPayment);
    }

    @GetMapping
    public ResponseEntity<List<Order>> getOrders() {
        List<Order> orders = orderService.getOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrder(@PathVariable Long orderId) {
        Order order = orderService.getOrder(orderId);
        return order != null ? ResponseEntity.ok(order) : ResponseEntity.notFound().build();
    }

}

