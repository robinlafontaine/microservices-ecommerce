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
        try {
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(paymentRequest.getAmount().multiply(BigDecimal.valueOf(100)).longValue()) // Amount in cents
                    .setCurrency("usd")
                    .setPaymentMethod(paymentRequest.getPaymentMethodId())
                    .setDescription("Payment for Order ID: " + paymentRequest.getOrderId())
                    //.setReturnUrl("https://your-site.com/payment-complete") //TODO : set return URL
                    .setAutomaticPaymentMethods(PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                            .setEnabled(true)
                            .setAllowRedirects(PaymentIntentCreateParams.AutomaticPaymentMethods.AllowRedirects.NEVER)
                            .build()) //TODO : remove this block to allow redirects
                    .build();
            PaymentIntent paymentIntent = PaymentIntent.create(params);

            PaymentResponseDTO response = new PaymentResponseDTO();
            response.setPaymentId(paymentIntent.getId());
            response.setStatus(PaymentStatus.PENDING);

            return response;
        } catch (StripeException e) {
            throw new PaymentException("Payment processing failed: " + e.getMessage());
        }
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

