package com.example.order;

import com.example.order.auth.AuthClient;
import com.example.order.dto.OrderRequestDTO;
import com.example.order.orderitem.OrderItem;
import com.example.order.payment.PaymentResponse;
import feign.Headers;
import org.springframework.data.domain.jaxb.SpringDataJaxb;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderService orderService;
    private final AuthClient authClient;
    private final OrderRepository orderRepository;

    Logger logger = Logger.getLogger(OrderController.class.getName());

    public OrderController(OrderService orderService, AuthClient authClient, OrderRepository orderRepository) {
        this.orderService = orderService;
        this.authClient = authClient;
        this.orderRepository = orderRepository;
    }

    @PostMapping("/create")
    public ResponseEntity<PaymentResponse> createOrder(
            @RequestHeader("Authorization") String token,
            @RequestBody OrderRequestDTO orderRequest) throws Exception {

        Order order = new Order();
        order.setUserId(authClient.getUserId(token));
        order.setTotalAmount(orderRequest.getTotalAmount());
        logger.info("OrderItems: " + orderRequest.getItems());

        List<OrderItem> orderItems = orderRequest.getItems().stream().map(item -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(item.getProductId());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setOrder(order);
            return orderItem;
        }).toList();

        order.setItems(orderItems);

        orderRepository.save(order);

        logger.info("Order user id: " + order.getUserId());
        logger.info("Order total amount: " + order.getTotalAmount());
        logger.info("Order items: " + order.getItems());

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

