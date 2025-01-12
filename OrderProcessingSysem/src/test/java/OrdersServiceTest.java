
import com.tr.demo.advice.exception.OrderLimitExceededException;
import com.tr.demo.entity.OrdersEntity;
import com.tr.demo.entity.OrdersProductEntity;
import com.tr.demo.entity.PaymentsEntity;
import com.tr.demo.entity.ProductsEntity;
import com.tr.demo.model.CustomerPrincipalModel;
import com.tr.demo.model.enums.PaymentsMethodEnum;
import com.tr.demo.model.request.CreateOrderRequest;
import com.tr.demo.model.request.OrderProductRequest;
import com.tr.demo.model.response.CreateOrderResponse;
import com.tr.demo.model.response.OrderListResponse;
import com.tr.demo.repository.OrdersProductRepository;
import com.tr.demo.repository.OrdersRepository;
import com.tr.demo.repository.PaymentsRepository;
import com.tr.demo.repository.ProductsRepository;
import com.tr.demo.service.OrdersService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.OffsetDateTime;
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
        OrderProductRequest request = OrderProductRequest.builder()
                .productName("Product1").quantity(2).build();

        CustomerPrincipalModel customer = getCustomerPrincipalModel(1L, 5, 0.0);

        ProductsEntity product = getProductEntity(1L, "Product1", 100.0, 10);

        when(productsRepository.findByName("Product1")).thenReturn(Optional.of(product));

        OrdersEntity savedOrder = OrdersEntity.builder()
                .id(1L)
                .customerId(Math.toIntExact(customer.getCustomerId()))
                .totalAmount(200.0)
                .build();

        when(ordersRepository.save(any(OrdersEntity.class))).thenReturn(savedOrder);

        PaymentsEntity savedPayment = PaymentsEntity.builder()
                .id(1L)
                .orderEntity(savedOrder)
                .paymentMethod(PaymentsMethodEnum.CREDIT_CARD.getValue())
                .paymentDate(OffsetDateTime.now())
                .amount(200.0)
                .build();

        when(paymentsRepository.save(any(PaymentsEntity.class))).thenReturn(savedPayment);

        OrdersProductEntity savedOrderProduct = OrdersProductEntity.builder()
                .id(1L)
                .ordersEntity(savedOrder)
                .productsEntity(product)
                .quantity(2)
                .price(100.0)
                .totalPrice(200.0)
                .discountRate(0.0)
                .build();

        when(ordersProductRepository.save(any(OrdersProductEntity.class))).thenReturn(savedOrderProduct);

        CreateOrderResponse response = ordersService.createOrder(request, PaymentsMethodEnum.CREDIT_CARD, customer);

        assertNotNull(response);
        assertEquals(200.0, response.getTotalAmount());
        assertEquals("Product1", request.getProductName());
        verify(productsRepository).save(any(ProductsEntity.class));
        verify(ordersRepository).save(any(OrdersEntity.class));
        verify(paymentsRepository).save(any(PaymentsEntity.class));
        verify(ordersProductRepository).save(any(OrdersProductEntity.class)); // Doğru çalıştığını kontrol etmek için
    }



    @Test
    void shouldCalculateTotalAmountWithDiscount() {
        OrderProductRequest request = OrderProductRequest.builder()
                .productName("Product1").quantity(2).build();

        CustomerPrincipalModel customer = getCustomerPrincipalModel(1L, 15, 10.0);

        ProductsEntity product = getProductEntity(1L, "Product1", 100.0, 10);

        when(productsRepository.findByName("Product1")).thenReturn(Optional.of(product));

        OrdersEntity savedOrder = OrdersEntity.builder()
                .id(1L)
                .customerId(Math.toIntExact(customer.getCustomerId()))
                .totalAmount(180.0) // 10% indirim uygulanmış tutar
                .build();
        when(ordersRepository.save(any(OrdersEntity.class))).thenReturn(savedOrder);

        PaymentsEntity savedPayment = PaymentsEntity.builder()
                .id(1L)
                .orderEntity(savedOrder)
                .paymentMethod("CASH")
                .paymentDate(OffsetDateTime.now())
                .amount(180.0)
                .build();
        when(paymentsRepository.save(any(PaymentsEntity.class))).thenReturn(savedPayment);

        OrdersProductEntity savedOrderProduct = OrdersProductEntity.builder()
                .id(1L)
                .ordersEntity(savedOrder)
                .productsEntity(product)
                .quantity(2)
                .price(100.0)
                .totalPrice(180.0)
                .discountRate(10.0) // İndirim oranı
                .build();
        when(ordersProductRepository.save(any(OrdersProductEntity.class))).thenReturn(savedOrderProduct);

        // Act
        CreateOrderResponse response = ordersService.createOrder(request, PaymentsMethodEnum.CASH, customer);

        // Assert
        assertNotNull(response); // Yanıt boş olmamalı
        assertEquals(180.0, response.getTotalAmount()); // İndirim uygulanmış toplam tutarı kontrol et
        assertEquals("Product1", request.getProductName()); // Ürün adını kontrol et
        verify(productsRepository).save(any(ProductsEntity.class)); // Ürün kaydedildi mi?
        verify(ordersRepository).save(any(OrdersEntity.class)); // Sipariş kaydedildi mi?
        verify(paymentsRepository).save(any(PaymentsEntity.class)); // Ödeme kaydedildi mi?
        verify(ordersProductRepository).save(any(OrdersProductEntity.class)); // Sipariş-ürün kaydedildi mi?
    }

    @Test
    void shouldApplyDiscountBasedOnOrderCount() {
        OrderProductRequest request = OrderProductRequest.builder()
                .productName("Product1").quantity(2).build();

        ProductsEntity product = getProductEntity(1L, "Product1", 100.0, 10);

        when(productsRepository.findByName("Product1")).thenReturn(Optional.of(product));

        CustomerPrincipalModel customerWithNoDiscount = getCustomerPrincipalModel(1L, 5, 0.0);
        CustomerPrincipalModel customerWith10PercentDiscount = getCustomerPrincipalModel(2L, 15, 10.0);
        CustomerPrincipalModel customerWith20PercentDiscount = getCustomerPrincipalModel(3L, 25, 20.0);

        OrdersEntity savedOrder = OrdersEntity.builder()
                .id(1L)
                .customerId(1)
                .totalAmount(200.0)
                .build();
        when(ordersRepository.save(any(OrdersEntity.class))).thenReturn(savedOrder);

        PaymentsEntity savedPayment = PaymentsEntity.builder()
                .id(1L)
                .orderEntity(savedOrder)
                .paymentMethod("CREDIT_CARD")
                .paymentDate(OffsetDateTime.now())
                .amount(200.0)
                .build();
        when(paymentsRepository.save(any(PaymentsEntity.class))).thenReturn(savedPayment);

        OrdersProductEntity savedOrderProduct = OrdersProductEntity.builder()
                .id(1L)
                .ordersEntity(savedOrder)
                .productsEntity(product)
                .quantity(2)
                .price(100.0)
                .totalPrice(200.0)
                .discountRate(10.0)
                .build();
        when(ordersProductRepository.save(any(OrdersProductEntity.class))).thenReturn(savedOrderProduct);

        // Act
        CreateOrderResponse responseNoDiscount = ordersService.createOrder(request, PaymentsMethodEnum.CREDIT_CARD, customerWithNoDiscount);
        CreateOrderResponse response10PercentDiscount = ordersService.createOrder(request, PaymentsMethodEnum.CREDIT_CARD, customerWith10PercentDiscount);
        CreateOrderResponse response20PercentDiscount = ordersService.createOrder(request, PaymentsMethodEnum.CREDIT_CARD, customerWith20PercentDiscount);

        // Assert
        assertEquals(200.0, responseNoDiscount.getTotalAmount()); // İndirim yok
        assertEquals(180.0, response10PercentDiscount.getTotalAmount()); // 10% indirim
        assertEquals(160.0, response20PercentDiscount.getTotalAmount()); // 20% indirim

        // Verify
        verify(productsRepository, times(3)).save(any(ProductsEntity.class)); // Ürün 3 kez kaydedildi mi?
        verify(ordersRepository, times(3)).save(any(OrdersEntity.class)); // Sipariş 3 kez kaydedildi mi?
        verify(paymentsRepository, times(3)).save(any(PaymentsEntity.class)); // Ödeme 3 kez kaydedildi mi?
        verify(ordersProductRepository, times(3)).save(any(OrdersProductEntity.class)); // Sipariş-ürün 3 kez kaydedildi mi?
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
