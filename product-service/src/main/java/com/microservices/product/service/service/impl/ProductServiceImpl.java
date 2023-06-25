package com.microservices.product.service.service.impl;

import com.microservices.product.service.entity.Product;
import com.microservices.product.service.exception.ProductServiceException;
import com.microservices.product.service.service.ProductService;
import com.microservices.product.service.model.ProductRequest;
import com.microservices.product.service.model.ProductResponse;
import com.microservices.product.service.repository.ProductRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.springframework.beans.BeanUtils.copyProperties;

@Log4j2
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Override
    public ProductResponse addProduct(ProductRequest productRequest) {
        log.info("adding new product...");

        Product product = Product.builder()
                .productName(productRequest.getProductName())
                .quantity(productRequest.getQuantity())
                .price(productRequest.getPrice())
                .build();

        productRepository.save(product);
        ProductResponse response = new ProductResponse();
        copyProperties(product, response);
        return response;
    }
    @Override
    public ProductResponse getProductById(Long productId) {
        log.info("fetching product by Id: " + productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductServiceException("product does not exist by id: " + productId,
                        "PRODUCT_NOT_FOUND"));
        ProductResponse productResponse = new ProductResponse();
        copyProperties(product, productResponse);
        return productResponse;
    }

    @Override
    public void reduceQuantity(Long productId, Long quantity) {
        log.info("reduce quantity {} for id: {}", productId, quantity);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductServiceException("product does not exists by id: "+ productId,
                        "PRODUCT_NOT_FOUND"));

        if(product.getQuantity() < quantity){
            throw new ProductServiceException("product does not have sufficient quantity",
                    "INSUFFICIENT_QUANTITY");
        }

        product.setQuantity(product.getQuantity() - quantity);
        productRepository.save(product);
        log.info("product quantity updated successfully");

    }
}
