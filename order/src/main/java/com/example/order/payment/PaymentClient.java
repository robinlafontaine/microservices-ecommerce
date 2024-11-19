package com.example.order.payment;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@FeignClient(name = "payment-service", url = "${payment.service.url}")
public interface PaymentClient {

    @PostMapping("/payments/initiate")
    PaymentResponse initiatePayment(@RequestBody PaymentRequest paymentRequest);

    @GetMapping("/payments/{paymentId}/status")
    PaymentStatus getPaymentStatus(@PathVariable Long paymentId);
}
