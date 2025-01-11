package com.tr.demo.controller;

import com.tr.demo.model.response.BaseResponse;
import com.tr.demo.model.response.OrderProductListResponse;
import com.tr.demo.service.OrderProductService;
import com.tr.demo.util.ResponseEntityWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static com.tr.demo.advice.constans.RestAPIConstants.API_ORDERS;

@RestController
@RequestMapping(API_ORDERS)
@RequiredArgsConstructor
public class OrderProductsController {

    private final OrderProductService orderProductService;

    @GetMapping("/all-order-products")
    public ResponseEntityWrapper<OrderProductListResponse> getAllOrderProducts() {
        BaseResponse<OrderProductListResponse> response = new BaseResponse<>();
        response.setData(orderProductService.getAllOrderProducts());
        response.setMessage("All order products listed successfully");
        return new ResponseEntityWrapper<>(response, HttpStatus.OK);
    }

    @GetMapping("/all-order-products/{customerEmail}")
    public ResponseEntityWrapper<OrderProductListResponse> getOrderProductsByCustomerEmail(@RequestParam String customerEmail) {
        BaseResponse<OrderProductListResponse> response = new BaseResponse<>();
        response.setData(orderProductService.getOrderProductsByCustomerEmail(customerEmail));
        response.setMessage("Order products listed successfully");
        return new ResponseEntityWrapper<>(response, HttpStatus.OK);
    }
}
