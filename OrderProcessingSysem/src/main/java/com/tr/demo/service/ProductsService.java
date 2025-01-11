package com.tr.demo.service;

import com.tr.demo.advice.exception.ProductAlreadyExistException;
import com.tr.demo.entity.ProductsEntity;
import com.tr.demo.model.request.CreateProducRequest;
import com.tr.demo.model.response.ProductListResponse;
import com.tr.demo.model.response.ProductResponse;
import com.tr.demo.repository.ProductsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductsService {

    private final ProductsRepository productsRepository;

    public ProductListResponse getAllProducts() {
        List<ProductResponse> responseList = new ArrayList<>();
        productsRepository.findAll().forEach(productsEntity ->
                responseList.add(
                        ProductResponse.builder()
                .productName(productsEntity.getName()).price(productsEntity.getPrice()).stocks(productsEntity.getStocks()).build()));
        return ProductListResponse.builder().products(responseList).build();
    }

    public ProductResponse addProduct(CreateProducRequest request) {

        Optional<ProductsEntity> product = productsRepository.findByName(request.getProductName());
        if(product.isPresent()){
            throw new ProductAlreadyExistException();
        }

        ProductsEntity productsEntity = ProductsEntity.builder()
                .name(request.getProductName())
                .price(request.getPrice())
                .stocks(request.getStocks())
                .build();
        log.info("Product added: {}", productsEntity);
        productsRepository.save(productsEntity);



        return ProductResponse.builder()
                .productName(productsEntity.getName())
                .price(productsEntity.getPrice())
                .stocks(productsEntity.getStocks())
                .build();
    }

}
