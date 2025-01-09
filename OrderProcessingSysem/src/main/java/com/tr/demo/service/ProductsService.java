package com.tr.demo.service;

import com.tr.demo.entity.ProductsEntity;
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

    public ProductsEntity addProduct(ProductsEntity productsEntity) {
        return productsRepository.save(productsEntity);
    }

}
