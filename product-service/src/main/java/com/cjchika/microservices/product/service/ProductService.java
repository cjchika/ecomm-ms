package com.cjchika.microservices.product.service;

import com.cjchika.microservices.product.dto.ProductRequest;
import com.cjchika.microservices.product.model.Product;
import com.cjchika.microservices.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    public Product createProduct(ProductRequest productRequest){
        Product product = Product.builder()
                .name(productRequest.name())
                .description(productRequest.description())
                .price(productRequest.price())
                .build();
        productRepository.save(product);
        log.info("Product created successfully.");

        return product;
    }


    public List<Product> getAllProducts() {
        List<Product> products =  productRepository.findAll();
        log.info("Products retrieved successfully.");
        return products;
    }
}
