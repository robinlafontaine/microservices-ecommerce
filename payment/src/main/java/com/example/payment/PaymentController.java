package com.example.payment;

import com.example.payment.dto.PaymentRequestDTO;
import com.example.payment.dto.PaymentResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleDeserializationError(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest().body("Invalid request payload: " + ex.getMessage());
    }

    @PostMapping("/initiate")
    public ResponseEntity<PaymentResponseDTO> initiatePayment(@RequestBody PaymentRequestDTO paymentRequest) {
        PaymentResponseDTO response = paymentService.processPayment(paymentRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            paymentService.handleStripeWebhook(payload, sigHeader);
            return ResponseEntity.ok("Webhook handled successfully");
        } catch (PaymentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/{paymentId}/status")
    public ResponseEntity<String> getPaymentStatus(@PathVariable String paymentId) {
        String status = paymentService.getPaymentStatus(paymentId);
        return ResponseEntity.ok(status);
    }

    @PostMapping("/confirm")
    public ResponseEntity<String> confirmPayment(@RequestParam String paymentId) {
        String status = paymentService.confirmPayment(paymentId);
        return ResponseEntity.ok("Payment status: " + status);
        //TODO: on fail, free stock in inventory
    }
}

