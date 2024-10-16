package com.example.payment;

import com.example.payment.dto.PaymentRequestDTO;
import com.example.payment.dto.PaymentResponseDTO;

public interface PaymentProcessor {

    PaymentResponseDTO processPayment(PaymentRequestDTO paymentRequest);
}
