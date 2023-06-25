package com.microservices.order.service.service;

import com.microservices.order.service.model.OrderRequest;
import com.microservices.order.service.model.OrderResponse;

public interface OrderService {
    Long placeOrder(OrderRequest orderRequest);
    OrderResponse getOrderDetails(Long orderId);
}
