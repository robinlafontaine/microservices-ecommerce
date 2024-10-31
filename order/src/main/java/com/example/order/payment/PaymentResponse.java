package com.example.order.payment;

import lombok.Data;

@Data
public class PaymentResponse {

    private String paymentId;

    private String clientSecret;

    private PaymentStatus status;

}

