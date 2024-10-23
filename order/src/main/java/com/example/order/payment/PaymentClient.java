package com.example.order.payment;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@FeignClient(name = "payment-service", url = "${payment.service.url}")
public interface PaymentClient {

    @PostMapping("/api/payments/initiate")
    PaymentResponse initiatePayment(@RequestParam BigDecimal amount, @RequestParam Long orderId);

    @GetMapping("/api/payments/status")
    PaymentStatus getPaymentStatus(@RequestParam Long paymentId);
}
