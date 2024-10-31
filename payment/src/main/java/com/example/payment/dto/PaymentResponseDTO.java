package com.example.payment.dto;
import com.example.payment.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Data;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PaymentResponseDTO {

    private String paymentId;

    private String clientSecret;

    private PaymentStatus status;
}

