package com.microservices.product.service.model;

import lombok.Data;

@Data
public class ProductRequest {
    private String productName;
    private Long price;
    private Long quantity;
}
