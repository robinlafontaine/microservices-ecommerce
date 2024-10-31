package com.example.order.payment;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class PaymentResponse {
    private String paymentId;
    private BigDecimal amount;
    private PaymentStatus status;

}

