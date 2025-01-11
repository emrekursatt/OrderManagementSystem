package com.tr.demo.service;

import com.tr.demo.entity.ProductsEntity;
import com.tr.demo.model.request.CreateProducRequest;
import com.tr.demo.model.response.ProductListResponse;
import com.tr.demo.model.response.ProductResponse;
import com.tr.demo.repository.ProductsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductsService {

    private final ProductsRepository productsRepository;

    public ProductListResponse getAllProducts() {
        List<ProductsEntity> all = productsRepository.findAll();
        List<ProductResponse> responseList = new ArrayList<>();
        all.forEach(productsEntity ->
                responseList.add(
                        ProductResponse.builder()
                .productName(productsEntity.getName()).price(productsEntity.getPrice()).stocks(productsEntity.getStocks()).build()));
        return ProductListResponse.builder().products(responseList).build();
    }

    public ProductResponse addProduct(CreateProducRequest request) {

        ProductsEntity productsEntity = ProductsEntity.builder()
                .name(request.getProductName())
                .price(request.getPrice())
                .stocks(request.getStocks())
                .build();

        productsRepository.save(productsEntity);

        return ProductResponse.builder()
                .productName(productsEntity.getName())
                .price(productsEntity.getPrice())
                .stocks(productsEntity.getStocks())
                .build();
    }

}
