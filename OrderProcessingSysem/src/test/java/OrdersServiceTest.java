import com.tr.demo.advice.exception.OrderLimitExceededException;
import com.tr.demo.entity.OrdersEntity;
import com.tr.demo.entity.PaymentsEntity;
import com.tr.demo.entity.ProductsEntity;
import com.tr.demo.model.CustomerPrincipalModel;
import com.tr.demo.model.enums.PaymentsMethodEnum;
import com.tr.demo.model.request.CreateOrderRequest;
import com.tr.demo.model.request.OrderProductRequest;
import com.tr.demo.model.response.OrderListResponse;
import com.tr.demo.repository.OrdersRepository;
import com.tr.demo.repository.PaymentsRepository;
import com.tr.demo.repository.ProductsRepository;
import com.tr.demo.service.OrdersService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
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

        CustomerPrincipalModel customer = CustomerPrincipalModel.builder()
                .customerId(1L)
                .customerName("John Doe")
                .username("johndoe")
                .email("john@example.com")
                .status("ACTIVE")
                .enabled(true)
                .orderCount(5)
                .discountRate(0.0)
                .build();

        ProductsEntity product1 = ProductsEntity.builder()
                .id(1L)
                .name("Product1")
                .price(100.0)
                .stocks(10)
                .build();

        ProductsEntity product2 = ProductsEntity.builder()
                .id(2L)
                .name("Product2")
                .price(200.0)
                .stocks(5)
                .build();

        when(productsRepository.findByName("Product1")).thenReturn(Optional.of(product1));
        when(productsRepository.findByName("Product2")).thenReturn(Optional.of(product2));

        // Act
        OrderListResponse response = ordersService.createOrder(request, PaymentsMethodEnum.CREDIT_CARD, customer);

        // Assert
        assertNotNull(response);
        assertEquals(2, response.getOrders().size());
        verify(ordersRepository, times(2)).save(any(OrdersEntity.class));
        verify(paymentsRepository, times(2)).save(any(PaymentsEntity.class));
        verify(productsRepository, times(2)).save(any(ProductsEntity.class));
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

        CustomerPrincipalModel customer = CustomerPrincipalModel.builder()
                .customerId(1L)
                .customerName("John Doe")
                .username("johndoe")
                .email("john@example.com")
                .status("ACTIVE")
                .enabled(true)
                .orderCount(5)
                .discountRate(0.0)
                .build();

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

        CustomerPrincipalModel customer = CustomerPrincipalModel.builder()
                .customerId(1L)
                .customerName("John Doe")
                .username("johndoe")
                .email("john@example.com")
                .status("ACTIVE")
                .enabled(true)
                .orderCount(15)
                .discountRate(10.0)
                .build();

        ProductsEntity product = ProductsEntity.builder()
                .id(1L)
                .name("Product1")
                .price(100.0)
                .stocks(10)
                .build();

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

        ProductsEntity product = ProductsEntity.builder()
                .id(1L)
                .name("Product1")
                .price(100.0)
                .stocks(10)
                .build();

        when(productsRepository.findByName("Product1")).thenReturn(Optional.of(product));

        CustomerPrincipalModel customerWithNoDiscount = CustomerPrincipalModel.builder()
                .customerId(1L)
                .customerName("John Doe")
                .username("johndoe")
                .email("john@example.com")
                .status("ACTIVE")
                .enabled(true)
                .orderCount(5)
                .discountRate(0.0)
                .build();

        CustomerPrincipalModel customerWith10PercentDiscount = CustomerPrincipalModel.builder()
                .customerId(2L)
                .customerName("Jane Doe")
                .username("janedoe")
                .email("jane@example.com")
                .status("ACTIVE")
                .enabled(true)
                .orderCount(15)
                .discountRate(10.0)
                .build();

        CustomerPrincipalModel customerWith20PercentDiscount = CustomerPrincipalModel.builder()
                .customerId(3L)
                .customerName("Jake Doe")
                .username("jakedoe")
                .email("jake@example.com")
                .status("ACTIVE")
                .enabled(true)
                .orderCount(25)
                .discountRate(20.0)
                .build();

        // Act
        OrderListResponse responseNoDiscount = ordersService.createOrder(request, PaymentsMethodEnum.CREDIT_CARD, customerWithNoDiscount);
        OrderListResponse response10PercentDiscount = ordersService.createOrder(request, PaymentsMethodEnum.CREDIT_CARD, customerWith10PercentDiscount);
        OrderListResponse response20PercentDiscount = ordersService.createOrder(request, PaymentsMethodEnum.CREDIT_CARD, customerWith20PercentDiscount);

        // Assert
        assertEquals(100.0, responseNoDiscount.getOrders().get(0).getTotalAmount());
        assertEquals(90.0, response10PercentDiscount.getOrders().get(0).getTotalAmount());
        assertEquals(80.0, response20PercentDiscount.getOrders().get(0).getTotalAmount());
    }
}
