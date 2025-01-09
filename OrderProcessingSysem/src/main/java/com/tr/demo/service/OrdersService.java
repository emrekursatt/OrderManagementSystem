package com.tr.demo.service;

import com.tr.demo.advice.exception.ResourceNotFoundException;
import com.tr.demo.entity.OrdersEntity;
import com.tr.demo.entity.PaymentsEntity;
import com.tr.demo.entity.ProductsEntity;
import com.tr.demo.model.CustomerPrincipalModel;
import com.tr.demo.model.OrderRabbitMessage;
import com.tr.demo.model.enums.PaymentsMethodEnum;
import com.tr.demo.model.request.CreateOrderRequest;
import com.tr.demo.model.request.OrderProductRequest;
import com.tr.demo.model.response.CreateOrderResponse;
import com.tr.demo.repository.OrdersRepository;
import com.tr.demo.repository.PaymentsRepository;
import com.tr.demo.repository.ProductsRepository;
import lombok.RequiredArgsConstructor;
import org.joda.time.DateTime;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrdersService {

    private final OrdersRepository ordersRepository;
    private final ProductsRepository productsRepository;
    private final PaymentsRepository paymentsRepository;
    private final RabbitTemplate rabbitTemplate;


    public CreateOrderResponse createOrder(CreateOrderRequest request , PaymentsMethodEnum paymentsMethodEnum , CustomerPrincipalModel customerPrincipalModel) {
        Double totalAmount = calculateTotalAmount(request.getProducts());

        double discountRate = customerPrincipalModel.getDiscountRate();

        //İndirim oranı varsa hesapla
        if(discountRate > 0)
            totalAmount = totalAmount - (totalAmount * discountRate / 100);

        request.getProducts().forEach(product -> {
            ProductsEntity productEntity = productsRepository.findByName(product.getProductName())
                    .orElseThrow(ResourceNotFoundException::new);
            productEntity.setStocks(productEntity.getStocks() - product.getQuantity());
            productsRepository.save(productEntity);
        });

        OrdersEntity order = OrdersEntity.builder()
                .customerId(Math.toIntExact(customerPrincipalModel.getCustomerId()))
                .totalAmount(totalAmount)
                .orderDate(new DateTime())
                .build();
        ordersRepository.save(order);

        paymentsRepository.save(PaymentsEntity.builder()
                .orderEntity(order)
                .paymentMethod(paymentsMethodEnum.getValue())
                .paymentDate(new DateTime())
                .amount(totalAmount)
                .build());

        OrderRabbitMessage message = new OrderRabbitMessage(order.getId(), customerPrincipalModel.getCustomerId(), order.getTotalAmount());
        rabbitTemplate.convertAndSend("order.exchange", "order.created", message);

        return CreateOrderResponse.builder()
                .customerName(customerPrincipalModel.getCustomerName())
                .totalAmount(totalAmount)
                .paymentType(paymentsMethodEnum.getValue())
                .build();
    }


    private Double calculateTotalAmount(List<OrderProductRequest> products) {
        return products.stream()
                .map(product -> {
                    ProductsEntity productEntity = productsRepository.findByName(product.getProductName())
                            .orElseThrow(ResourceNotFoundException::new);
                    return productEntity.getPrice() * product.getQuantity();
                })
                .reduce(0.0, Double::sum);
    }

}
