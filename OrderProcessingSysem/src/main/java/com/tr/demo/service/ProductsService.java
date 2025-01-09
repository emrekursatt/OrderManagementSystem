package com.tr.demo.service;

import com.tr.demo.entity.ProductsEntity;
import com.tr.demo.model.request.CreateProducRequest;
import com.tr.demo.model.request.OrderProductRequest;
import com.tr.demo.model.response.CreateProductResponse;
import com.tr.demo.repository.ProductsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductsService {

    private final ProductsRepository productsRepository;

    public List<ProductsEntity> getAllProducts() {
        return productsRepository.findAll();
    }

    public CreateProductResponse addProduct(CreateProducRequest request) {

        ProductsEntity productsEntity = ProductsEntity.builder()
                .name(request.getProductName())
                .price(request.getPrice())
                .stocks(request.getStocks())
                .build();

        productsRepository.save(productsEntity);

        return CreateProductResponse.builder()
                .productName(productsEntity.getName())
                .price(productsEntity.getPrice())
                .stocks(productsEntity.getStocks())
                .build();
    }

}
