package com.tr.demo.controller;

import com.tr.demo.annotations.CustomerPrincipal;
import com.tr.demo.model.CustomerPrincipalModel;
import com.tr.demo.model.enums.PaymentsMethodEnum;
import com.tr.demo.model.request.CreateOrderRequest;
import com.tr.demo.model.request.OrderProductRequest;
import com.tr.demo.model.response.BaseResponse;
import com.tr.demo.model.response.CreateOrderResponse;
import com.tr.demo.model.response.OrderListResponse;
import com.tr.demo.service.OrdersService;
import com.tr.demo.util.ResponseEntityWrapper;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static com.tr.demo.advice.constans.RestAPIConstants.API_ORDERS;

@RestController
@RequestMapping(API_ORDERS)
@RequiredArgsConstructor
public class OrdersController {

    private final OrdersService ordersService;


    @PostMapping("/create-order")
    public ResponseEntityWrapper<CreateOrderResponse> createOrder(
            @Parameter(hidden = true)  @CustomerPrincipal CustomerPrincipalModel customer,
            @RequestBody OrderProductRequest request ,
            @RequestParam PaymentsMethodEnum paymentMethod) {
        BaseResponse<CreateOrderResponse> response = new BaseResponse<>();
        response.setData(ordersService.createOrder(request , paymentMethod ,customer));
        response.setMessage("Order created successfully");
        return new ResponseEntityWrapper<>(response, HttpStatus.CREATED);
    }
}
