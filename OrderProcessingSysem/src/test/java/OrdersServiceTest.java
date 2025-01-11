package com.tr.demo.service;

import com.tr.demo.advice.exception.OrderLimitExceededException;
import com.tr.demo.entity.OrdersEntity;
import com.tr.demo.entity.PaymentsEntity;
import com.tr.demo.entity.ProductsEntity;
import com.tr.demo.model.CustomerPrincipalModel;
import com.tr.demo.model.enums.PaymentsMethodEnum;
import com.tr.demo.model.request.CreateOrderRequest;
import com.tr.demo.model.request.OrderProductRequest;
import com.tr.demo.model.response.OrderListResponse;
import com.tr.demo.repository.OrdersProductRepository;
import com.tr.demo.repository.OrdersRepository;
import com.tr.demo.repository.PaymentsRepository;
import com.tr.demo.repository.ProductsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrdersServiceTest {

    @InjectMocks
    private OrdersService ordersService;

    @Mock
    private OrdersRepository ordersRepository;

    @Mock
    private ProductsRepository productsRepository;

    @Mock
    private PaymentsRepository paymentsRepository;

    @Mock
    private OrdersProductRepository ordersProductRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        doNothing().when(rabbitTemplate).convertAndSend(anyString(), anyString(), Optional.ofNullable(any()));
    }

    @Test
    void shouldCreateOrderSuccessfully() {
        // Arrange
        CreateOrderRequest request = CreateOrderRequest.builder()
                .products(List.of(
                        OrderProductRequest.builder().productName("Product1").quantity(2).build(),
                        OrderProductRequest.builder().productName("Product2").quantity(1).build()
                ))
                .build();

        CustomerPrincipalModel customer = getCustomerPrincipalModel(1L, 5, 0.0);

        ProductsEntity product1 = getProductEntity(1L, "Product1", 100.0, 10);
        ProductsEntity product2 = getProductEntity(2L, "Product2", 200.0, 5);

        when(productsRepository.findByName("Product1")).thenReturn(Optional.of(product1));
        when(productsRepository.findByName("Product2")).thenReturn(Optional.of(product2));

        // Act
        OrderListResponse response = ordersService.createOrder(request, PaymentsMethodEnum.CREDIT_CARD, customer);

        // Assert
        assertNotNull(response);
        assertEquals(2, response.getOrders().size());
        verify(productsRepository, times(2)).save(any(ProductsEntity.class));
        verify(ordersRepository, times(2)).save(any(OrdersEntity.class));
        verify(paymentsRepository, times(2)).save(any(PaymentsEntity.class));
        verify(ordersProductRepository, times(2)).save(any());
    }

    @Test
    void shouldThrowExceptionWhenOrderLimitExceeded() {
        // Arrange
        CreateOrderRequest request = CreateOrderRequest.builder()
                .products(List.of(
                        OrderProductRequest.builder().productName("Product1").quantity(1).build(),
                        OrderProductRequest.builder().productName("Product2").quantity(1).build(),
                        OrderProductRequest.builder().productName("Product3").quantity(1).build(),
                        OrderProductRequest.builder().productName("Product4").quantity(1).build(),
                        OrderProductRequest.builder().productName("Product5").quantity(1).build(),
                        OrderProductRequest.builder().productName("Product6").quantity(1).build(),
                        OrderProductRequest.builder().productName("Product7").quantity(1).build(),
                        OrderProductRequest.builder().productName("Product8").quantity(1).build(),
                        OrderProductRequest.builder().productName("Product9").quantity(1).build(),
                        OrderProductRequest.builder().productName("Product10").quantity(1).build()
                ))
                .build();

        CustomerPrincipalModel customer = getCustomerPrincipalModel(1L, 5, 0.0);

        // Act & Assert
        assertThrows(OrderLimitExceededException.class, () ->
                ordersService.createOrder(request, PaymentsMethodEnum.CREDIT_CARD, customer));
    }

    @Test
    void shouldCalculateTotalAmountWithDiscount() {
        // Arrange
        CreateOrderRequest request = CreateOrderRequest.builder()
                .products(List.of(
                        OrderProductRequest.builder().productName("Product1").quantity(2).build()
                ))
                .build();

        CustomerPrincipalModel customer = getCustomerPrincipalModel(1L, 15, 10.0);

        ProductsEntity product = getProductEntity(1L, "Product1", 100.0, 10);

        when(productsRepository.findByName("Product1")).thenReturn(Optional.of(product));

        // Act
        OrderListResponse response = ordersService.createOrder(request, PaymentsMethodEnum.CASH, customer);

        // Assert
        assertNotNull(response);
        assertEquals(180.0, response.getOrders().get(0).getTotalAmount());
        verify(productsRepository).save(any(ProductsEntity.class));
    }

    @Test
    void shouldApplyDiscountBasedOnOrderCount() {
        // Arrange
        CreateOrderRequest request = CreateOrderRequest.builder()
                .products(List.of(
                        OrderProductRequest.builder().productName("Product1").quantity(1).build()
                ))
                .build();

        ProductsEntity product = getProductEntity(1L, "Product1", 100.0, 10);

        when(productsRepository.findByName("Product1")).thenReturn(Optional.of(product));

        CustomerPrincipalModel customerWithNoDiscount = getCustomerPrincipalModel(1L, 5, 0.0);
        CustomerPrincipalModel customerWith10PercentDiscount = getCustomerPrincipalModel(2L, 15, 10.0);
        CustomerPrincipalModel customerWith20PercentDiscount = getCustomerPrincipalModel(3L, 25, 20.0);

        OrderListResponse responseNoDiscount = ordersService.createOrder(request, PaymentsMethodEnum.CREDIT_CARD, customerWithNoDiscount);
        OrderListResponse response10PercentDiscount = ordersService.createOrder(request, PaymentsMethodEnum.CREDIT_CARD, customerWith10PercentDiscount);
        OrderListResponse response20PercentDiscount = ordersService.createOrder(request, PaymentsMethodEnum.CREDIT_CARD, customerWith20PercentDiscount);

        assertEquals(100.0, responseNoDiscount.getOrders().get(0).getTotalAmount());
        assertEquals(90.0, response10PercentDiscount.getOrders().get(0).getTotalAmount());
        assertEquals(80.0, response20PercentDiscount.getOrders().get(0).getTotalAmount());
    }

    // Helper methods to create mock data
    private CustomerPrincipalModel getCustomerPrincipalModel(Long customerId, int orderCount, double discountRate) {
        return CustomerPrincipalModel.builder()
                .customerId(customerId)
                .customerName("KRST")
                .username("krst")
                .email("krst@example.com")
                .status("ACTIVE")
                .enabled(true)
                .orderCount(orderCount)
                .discountRate(discountRate)
                .build();
    }

    private ProductsEntity getProductEntity(Long id, String name, double price, int stocks) {
        return ProductsEntity.builder()
                .id(id)
                .name(name)
                .price(price)
                .stocks(stocks)
                .build();
    }
}
