package com.microservices.payment.service.service;


import com.microservices.payment.service.model.PaymentRequest;

public interface PaymentService {
    Long doPayment(PaymentRequest paymentRequest);
}
