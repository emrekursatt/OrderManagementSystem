package com.tr.demo.service;

import com.tr.demo.repository.OrdersProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderProductService {

    private final OrdersProductRepository ordersProductRepository;


}
