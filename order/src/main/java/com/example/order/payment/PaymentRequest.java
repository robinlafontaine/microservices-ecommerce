package com.example.order.payment;

import lombok.*;
import java.math.BigDecimal;

@Data
public class PaymentRequest {

    private Long orderId;

    private BigDecimal amount;

    private String paymentMethodId;
}
