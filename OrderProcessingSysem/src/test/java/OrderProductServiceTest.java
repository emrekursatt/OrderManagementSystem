import com.tr.demo.entity.OrdersEntity;
import com.tr.demo.entity.OrdersProductEntity;
import com.tr.demo.entity.ProductsEntity;
import com.tr.demo.model.response.OrderProductListResponse;
import com.tr.demo.repository.OrdersProductRepository;
import com.tr.demo.service.OrderProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class OrderProductServiceTest {
    @InjectMocks
    private OrderProductService orderProductService;

    @Mock
    private OrdersProductRepository ordersProductRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnAllOrderProductsSuccessfully() {
        OrdersEntity mockOrder = OrdersEntity.builder()
                .customerEmail("customer@example.com")
                .build();

        ProductsEntity mockProduct = ProductsEntity.builder()
                .name("Product1")
                .price(100.0)
                .build();

        OrdersProductEntity product1 = OrdersProductEntity.builder()
                .ordersEntity(mockOrder)
                .productsEntity(mockProduct)
                .quantity(2)
                .price(100.0)
                .discountRate(10.0)
                .build();

        OrdersProductEntity product2 = OrdersProductEntity.builder()
                .ordersEntity(mockOrder)
                .productsEntity(mockProduct)
                .quantity(1)
                .price(200.0)
                .discountRate(0.0)
                .build();

        when(ordersProductRepository.findAll()).thenReturn(List.of(product1, product2));

        OrderProductListResponse response = orderProductService.getAllOrderProducts();

        assertNotNull(response);
        assertEquals(2, response.getOrderProductList().size());
        assertEquals("Product1", response.getOrderProductList().get(0).getProductName());
        verify(ordersProductRepository, times(1)).findAll();
    }

    @Test
    void shouldReturnOrderProductsByCustomerEmailSuccessfully() {
        OrdersEntity mockOrder = OrdersEntity.builder()
                .customerEmail("test@example.com")
                .build();

        ProductsEntity mockProduct = ProductsEntity.builder()
                .name("Product1")
                .price(100.0)
                .build();

        OrdersProductEntity product1 = OrdersProductEntity.builder()
                .ordersEntity(mockOrder)
                .productsEntity(mockProduct)
                .quantity(2)
                .price(100.0)
                .discountRate(10.0)
                .build();

        OrdersProductEntity product2 = OrdersProductEntity.builder()
                .ordersEntity(mockOrder)
                .productsEntity(mockProduct)
                .quantity(1)
                .price(200.0)
                .discountRate(0.0)
                .build();

        when(ordersProductRepository.getOrderProductsByCustomerEmail("test@example.com"))
                .thenReturn(List.of(product1, product2));

        OrderProductListResponse response = orderProductService.getOrderProductsByCustomerEmail("test@example.com");

        assertNotNull(response);
        assertEquals(2, response.getOrderProductList().size());
        assertEquals("Product1", response.getOrderProductList().get(0).getProductName());
        verify(ordersProductRepository, times(1)).getOrderProductsByCustomerEmail("test@example.com");
    }
}
