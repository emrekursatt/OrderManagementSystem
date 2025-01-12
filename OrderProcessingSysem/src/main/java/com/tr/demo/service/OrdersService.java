package com.tr.demo.service;

import com.tr.demo.advice.exception.OrderLimitExceededException;
import com.tr.demo.advice.exception.ResourceNotFoundException;
import com.tr.demo.entity.OrdersEntity;
import com.tr.demo.entity.OrdersProductEntity;
import com.tr.demo.entity.PaymentsEntity;
import com.tr.demo.entity.ProductsEntity;
import com.tr.demo.model.CustomerPrincipalModel;
import com.tr.demo.model.OrderRabbitMessage;
import com.tr.demo.model.enums.PaymentsMethodEnum;
import com.tr.demo.model.request.CreateOrderRequest;
import com.tr.demo.model.request.OrderProductRequest;
import com.tr.demo.model.response.CreateOrderResponse;
import com.tr.demo.model.response.OrderListResponse;
import com.tr.demo.repository.OrdersProductRepository;
import com.tr.demo.repository.OrdersRepository;
import com.tr.demo.repository.PaymentsRepository;
import com.tr.demo.repository.ProductsRepository;
import com.tr.demo.resolver.CustomerPrincipalArgumentResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrdersService {

    private final OrdersRepository ordersRepository;
    private final ProductsRepository productsRepository;
    private final PaymentsRepository paymentsRepository;
    private final RabbitTemplate rabbitTemplate;
    private final OrdersProductRepository ordersProductRepository;


    public CreateOrderResponse createOrder(OrderProductRequest product , PaymentsMethodEnum paymentsMethodEnum , CustomerPrincipalModel customerPrincipalModel) {

        // paymentsMethodEnum gönderilen enum yoksa hata verecek.
        PaymentsMethodEnum.valueOf(paymentsMethodEnum.name());

            ProductsEntity productEntity = productsRepository.findByName(product.getProductName())
                    .orElseThrow(ResourceNotFoundException::new);

            double totalAmount = productEntity.getPrice() * product.getQuantity();

            double discountRate = customerPrincipalModel.getDiscountRate();

            if(discountRate > 0)
                totalAmount = totalAmount - (totalAmount * discountRate / 100);


            productEntity.setStocks(productEntity.getStocks() - product.getQuantity());
            productsRepository.save(productEntity);


            OrdersEntity order = OrdersEntity.builder()
                    .customerId(Math.toIntExact(customerPrincipalModel.getCustomerId()))
                    .totalAmount(totalAmount)
                    .orderDate(OffsetDateTime.now())
                    .customerEmail(customerPrincipalModel.getEmail())
                    .build();
            ordersRepository.save(order);

            log.info("Order created with id: {}", order.getId());

            PaymentsEntity payments = paymentsRepository.save(PaymentsEntity.builder()
                    .orderEntity(order)
                    .paymentMethod(Objects.requireNonNull(PaymentsMethodEnum.fromValue(paymentsMethodEnum.getValue())).getValue())
                    .paymentDate(OffsetDateTime.now())
                    .amount(totalAmount)
                    .build());

            log.info("Payment created with id: {}", payments.getId());

            OrdersProductEntity orderProducts = ordersProductRepository.save(OrdersProductEntity.builder()
                    .ordersEntity(order)
                    .productsEntity(productEntity)
                    .quantity(product.getQuantity())
                    .price(productEntity.getPrice())
                    .totalPrice(totalAmount)
                    .discountRate(discountRate)
                    .build());

            log.info("Order product created with id: {}", orderProducts.getId());



            OrderRabbitMessage message = new OrderRabbitMessage(order.getId(), customerPrincipalModel.getCustomerId(), order.getTotalAmount());
            rabbitTemplate.convertAndSend("order.exchange", "order.created", message);
            log.info("Order message sent to RabbitMQ with order id: {}", order.getId());


        return  CreateOrderResponse.builder()
                .customerName(customerPrincipalModel.getCustomerName())
                .totalAmount(totalAmount)
                .paymentType(paymentsMethodEnum.getValue())
                .discountRate(discountRate)
                .build();
    }

}
