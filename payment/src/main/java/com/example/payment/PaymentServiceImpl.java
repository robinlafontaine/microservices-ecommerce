package com.example.payment;


import com.example.payment.dto.PaymentRequest;
import com.example.payment.dto.PaymentResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    @Autowired
    public PaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public PaymentResponse createPayment(PaymentRequest paymentRequestDTO) {
        Payment payment = convertToEntity(paymentRequestDTO);
        Payment savedPayment = paymentRepository.save(payment);
        return convertToResponseDTO(savedPayment);
    }

    @Override
    public PaymentResponse getPayment(Long id) {
        return null;
    }

    @Override
    public PaymentResponse updatePayment(Long id, PaymentRequest paymentRequestDTO) {
        return null;
    }

    @Override
    public void deletePayment(Long id) {

    }

    private Payment convertToEntity(PaymentRequest paymentRequestDTO) {
        Payment payment = new Payment();
        BeanUtils.copyProperties(paymentRequestDTO, payment);
        return payment;
    }

    private PaymentResponse convertToResponseDTO(Payment payment) {
        PaymentResponse responseDTO = new PaymentResponse();
        BeanUtils.copyProperties(payment, responseDTO);
        return responseDTO;
    }
}