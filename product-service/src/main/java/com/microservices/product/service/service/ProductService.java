package com.microservices.product.service.service;

import com.microservices.product.service.model.ProductRequest;
import com.microservices.product.service.model.ProductResponse;

public interface ProductService {
    ProductResponse addProduct(ProductRequest productRequest);
    ProductResponse getProductById(Long productId);
    void reduceQuantity(Long productId, Long quantity);
}
