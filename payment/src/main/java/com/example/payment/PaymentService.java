package com.example.payment;

import com.example.payment.dto.PaymentRequest;
import com.example.payment.dto.PaymentResponse;

public interface PaymentService {
    PaymentResponse createPayment(PaymentRequest paymentRequestDTO);

    PaymentResponse getPayment(Long id);

    PaymentResponse updatePayment(Long id, PaymentRequest paymentRequestDTO);

    void deletePayment(Long id);
}