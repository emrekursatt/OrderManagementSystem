package com.tr.demo.controller;

import com.tr.demo.model.request.CreateProducRequest;
import com.tr.demo.model.request.OrderProductRequest;
import com.tr.demo.model.response.BaseResponse;
import com.tr.demo.model.response.CreateProductResponse;
import com.tr.demo.service.ProductsService;
import com.tr.demo.util.ResponseEntityWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.tr.demo.advice.constans.RestAPIConstants.API_ORDERS;
import static com.tr.demo.advice.constans.RestAPIConstants.API_PRODUCT;

@RestController
@RequestMapping(API_PRODUCT)
@RequiredArgsConstructor
public class ProductController {

    private final ProductsService productsService;

    @PostMapping
    public ResponseEntityWrapper<CreateProductResponse> addProduct(@RequestBody CreateProducRequest request) {
        BaseResponse<CreateProductResponse> response = new BaseResponse<>();
        response.setData(productsService.addProduct(request));
        response.setMessage("Product added successfully");
        return new ResponseEntityWrapper<>(response , HttpStatus.CREATED);
    }
}
