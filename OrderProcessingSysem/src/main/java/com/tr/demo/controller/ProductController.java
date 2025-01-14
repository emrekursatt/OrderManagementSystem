package com.tr.demo.controller;

import com.tr.demo.model.request.CreateProducRequest;
import com.tr.demo.model.response.BaseResponse;
import com.tr.demo.model.response.ProductListResponse;
import com.tr.demo.model.response.ProductResponse;
import com.tr.demo.service.ProductsService;
import com.tr.demo.util.ResponseEntityWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static com.tr.demo.advice.constans.RestAPIConstants.API_PRODUCT;

@RestController
@RequestMapping(API_PRODUCT)
@RequiredArgsConstructor
public class ProductController {

    private final ProductsService productsService;

    @PostMapping("add-product")
    public ResponseEntityWrapper<ProductResponse> addProduct(@RequestBody CreateProducRequest request) {
        BaseResponse<ProductResponse> response = new BaseResponse<>();
        response.setData(productsService.addProduct(request));
        response.setMessage("Product added successfully");
        return new ResponseEntityWrapper<>(response, HttpStatus.CREATED);
    }

    @GetMapping("all-product")
    public  ResponseEntityWrapper<ProductListResponse> getAllProducts(){
        BaseResponse<ProductListResponse> response = new BaseResponse<>();
        response.setData(productsService.getAllProducts());
        response.setMessage("Product listed successfully");
        return new ResponseEntityWrapper<>(response, HttpStatus.OK);
    }

}
