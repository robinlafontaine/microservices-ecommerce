package com.example.payment;

import com.example.payment.dto.PaymentRequestDTO;
import com.example.payment.dto.PaymentResponseDTO;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentMethodCreateParams;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Component
public class PaymentGatewayClient {

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    public PaymentResponseDTO processPayment(PaymentRequestDTO paymentRequest) {
        validatePaymentRequest(paymentRequest);

        try {
            PaymentIntentCreateParams params = createPaymentIntentParams(paymentRequest);
            PaymentIntent paymentIntent = PaymentIntent.create(params);

            return buildPaymentResponse(paymentIntent);
        } catch (StripeException e) {
            throw new PaymentException("Stripe error occurred: " + e.getMessage());
        }
    }

    private void validatePaymentRequest(PaymentRequestDTO paymentRequest) {
        if (paymentRequest.getAmount() == null || paymentRequest.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }
        if (paymentRequest.getCurrency() == null || paymentRequest.getCurrency().isEmpty()) {
            paymentRequest.setCurrency("eur");
        }
    }

    private PaymentIntentCreateParams createPaymentIntentParams(PaymentRequestDTO paymentRequest) {
        return PaymentIntentCreateParams.builder()
                .setAmount(paymentRequest.getAmount().multiply(BigDecimal.valueOf(100)).longValue())
                .setCurrency(paymentRequest.getCurrency())
                .setDescription("Payment for Order ID: " + paymentRequest.getOrderId())
                .setAutomaticPaymentMethods(PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                        .setEnabled(true)
                        .setAllowRedirects(PaymentIntentCreateParams.AutomaticPaymentMethods.AllowRedirects.NEVER)
                        .build())
                .build();
    }

    private PaymentResponseDTO buildPaymentResponse(PaymentIntent paymentIntent) {
        PaymentResponseDTO response = new PaymentResponseDTO();
        response.setPaymentId(paymentIntent.getId());
        response.setClientSecret(paymentIntent.getClientSecret());
        response.setStatus(PaymentStatus.PENDING);
        return response;
    }

}

