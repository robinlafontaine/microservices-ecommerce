package com.example.payment;

import com.example.payment.dto.PaymentRequestDTO;
import com.example.payment.dto.PaymentResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentGatewayClient paymentGatewayClient;

    public PaymentResponseDTO processPayment(PaymentRequestDTO paymentRequest) {
        PaymentResponseDTO response = paymentGatewayClient.processPayment(paymentRequest);

        Payment payment = new Payment();
        payment.setOrderId(paymentRequest.getOrderId());
        payment.setAmount(paymentRequest.getAmount());
        payment.setPaymentId(response.getPaymentId());
        payment.setStatus(response.getStatus());
        payment.setTimestamp(LocalDateTime.now());

        paymentRepository.save(payment);
        return response;
    }

    public void handleStripeWebhook(String payload, String sigHeader) {
        paymentGatewayClient.handleWebhook(payload, sigHeader);
    }

    public String getPaymentStatus(String paymentId) {
        Payment payment = paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new PaymentException("Payment not found"));
        return payment.getStatus().name();
    }

    public String confirmPayment(String paymentId) {
        HashMap<Integer, String> status = paymentGatewayClient.confirmPayment(paymentId);
        if (status.containsKey(200)) {
            updatePaymentStatus(paymentId, PaymentStatus.COMPLETED);
            return status.get(200);
        } else {
            updatePaymentStatus(paymentId, PaymentStatus.FAILED);
            return status.get(400);
        }
    }

    private void updatePaymentStatus(String paymentId, PaymentStatus status) {
        Payment payment = paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new PaymentException("Payment not found"));
        payment.setStatus(status);
        paymentRepository.save(payment);
    }
}


