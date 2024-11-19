package com.example.payment;

import com.example.payment.dto.PaymentRequestDTO;
import com.example.payment.dto.PaymentResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.logging.Logger;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentGatewayClient paymentGatewayClient;

    Logger logger = Logger.getLogger(PaymentService.class.getName());

    public PaymentResponseDTO processPayment(PaymentRequestDTO paymentRequest) {

        logger.info("Processing payment for order: " + paymentRequest.getOrderId());
        PaymentResponseDTO response = paymentGatewayClient.processPayment(paymentRequest);

        Payment payment = new Payment();
        payment.setOrderId(paymentRequest.getOrderId());
        payment.setAmount(paymentRequest.getAmount());
        payment.setPaymentId(response.getPaymentId());
        payment.setStatus(response.getStatus());
        payment.setTimestamp(LocalDateTime.now());

        logger.info("Saving payment: " + payment);

        paymentRepository.save(payment);
        return response;
    }

    public String getPaymentStatus(String paymentId) {
        Payment payment = paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new PaymentException("Payment not found"));
        return payment.getStatus().name();
    }

}


