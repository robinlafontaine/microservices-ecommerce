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
            // Log the exception details for easier debugging
            System.err.println("Stripe error occurred: " + e.getMessage());
            throw new PaymentException("Payment processing failed: " + e.getMessage());
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
                // Uncomment for return URL
                //.setReturnUrl(System.getenv("PAYMENT_RETURN_URL"))
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

    public HashMap<Integer, String> confirmPayment(String paymentId) {
        try {
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentId);
            PaymentIntent confirmedPaymentIntent = paymentIntent.confirm();
            HashMap<Integer, String> response = new HashMap<>();

            if ("succeeded".equals(confirmedPaymentIntent.getStatus())) {
                response.put(200, "Payment status: " + confirmedPaymentIntent.getStatus());
            } else {
                response.put(400, "Payment status: " + confirmedPaymentIntent.getStatus());
            }
            return response;
        } catch (StripeException e) {
            throw new RuntimeException("Error confirming payment: " + e.getMessage());
        }
    }

    public void handleWebhook(String payload, String sigHeader) {
        String endpointSecret = "your_webhook_secret"; // Retrieve from Stripe

        try {
            Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
            if ("payment_intent.succeeded".equals(event.getType())) {
                PaymentIntent intent = (PaymentIntent) event.getDataObjectDeserializer()
                        .getObject().orElseThrow();
                // Process the successful payment intent here
            }
        } catch (StripeException e) {
            throw new PaymentException("Webhook error: " + e.getMessage());
        }
    }

}

