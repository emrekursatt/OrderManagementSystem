package com.tr.demo.service;

import com.tr.demo.model.response.OrderProductListResponse;
import com.tr.demo.model.response.OrderProductResponse;
import com.tr.demo.repository.OrdersProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderProductService {

    private final OrdersProductRepository ordersProductRepository;


    public OrderProductListResponse getAllOrderProducts() {

        List<OrderProductResponse> orderProductList = new ArrayList<>();

        ordersProductRepository.findAll().forEach(item -> {
            orderProductList.add(OrderProductResponse.builder()
                    .customerEmail(item.getOrdersEntity().getCustomerEmail())
                    .productName(item.getProductsEntity().getName())
                    .price(item.getProductsEntity().getPrice())
                    .quantity(item.getQuantity())
                    .discountRate(item.getDiscountRate())
                    .build());
        });
        return OrderProductListResponse.builder()
                .orderProductList(orderProductList)
                .build();
    }

    public OrderProductListResponse getOrderProductsByCustomerEmail(String customerEmail) {
        List<OrderProductResponse> orderProductList = new ArrayList<>();
        ordersProductRepository.getOrderProductsByCustomerEmail(customerEmail).forEach(item -> {
            orderProductList.add(OrderProductResponse.builder()
                    .customerEmail(item.getOrdersEntity().getCustomerEmail())
                    .productName(item.getProductsEntity().getName())
                    .price(item.getProductsEntity().getPrice())
                    .quantity(item.getQuantity())
                    .discountRate(item.getDiscountRate())
                    .build());
        });
        return OrderProductListResponse.builder()
                .orderProductList(orderProductList)
                .build();
    }

}
