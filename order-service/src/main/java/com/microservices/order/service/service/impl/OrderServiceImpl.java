package com.microservices.order.service.service.impl;

import com.microservices.order.service.external.client.PaymentService;
import com.microservices.order.service.external.client.ProductService;
import com.microservices.order.service.model.OrderRequest;
import com.microservices.order.service.model.OrderResponse;
import com.microservices.order.service.model.PaymentRequest;
import com.microservices.order.service.entity.Order;
import com.microservices.order.service.exception.CustomException;
import com.microservices.order.service.repository.OrderRepository;
import com.microservices.order.service.service.OrderService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

@Log4j2
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductService productService;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Long placeOrder(OrderRequest orderRequest) {
        // Order entry -> save the data with status order CREATED
        // product service -> block product (reduce the quantity)
        // payment service -> payments -> success -> complete else
        // cancelled

        log.info("placing order request: {}", orderRequest);

        // check the product inventory and reduce the quantity
        productService.reduceQuantity(orderRequest.getProductId(), orderRequest.getQuantity());

        log.info("creating order with status CREATED");

        Order order = Order.builder()
                .amount(orderRequest.getTotalAmount())
                .orderStatus("CREATED")
                .productId(orderRequest.getProductId())
                .orderDate(Instant.now())
                .quantity(orderRequest.getQuantity())
                .build();
        order = orderRepository.save(order);
        log.info("order placed successfully with order Id: {}", order.getOrderId());

        log.info("calling payment service to complete the payment");
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .orderId(order.getOrderId())
                .paymentMode(orderRequest.getPaymentMode())
                .amount(orderRequest.getTotalAmount())
                .build();

        String orderStatus = null;
        try {
            paymentService.doPayment(paymentRequest);
            log.info("payment done successfully");
            orderStatus = "PLACED";
        } catch (Exception ex) {
            log.error("error occurred in payment, changing order status: PAYMENT-FAILED");
            orderStatus = "PAYMENT_FAILED";
        }
        order.setOrderStatus(orderStatus);
        orderRepository.save(order);
        log.info("order placed successfully with Order Id: {}", order.getOrderId());

        return order.getOrderId();
    }

    @Override
    public OrderResponse getOrderDetails(Long orderId) {
        log.info("get order details for order Id: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException("order not found for order id: {}" + orderId,
                        "NOT_FOUND", 404));

        OrderResponse.ProductDetail productDetail = restTemplate.getForObject("http://PRODUCT-SERVICE/api/v1/products/" +
                order.getProductId(), OrderResponse.ProductDetail.class);

        OrderResponse orderResponse = OrderResponse.builder()
                .orderId(order.getOrderId())
                .orderStatus(order.getOrderStatus())
                .amount(order.getAmount())
                .orderDate(order.getOrderDate())
                .productDetail(productDetail)
                .build();
        return orderResponse;
    }
}
