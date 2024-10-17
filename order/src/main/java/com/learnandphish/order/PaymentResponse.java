package com.learnandphish.order;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class PaymentResponse {
    private Long paymentId;
    private BigDecimal amount;
    private PaymentStatus status;

}

