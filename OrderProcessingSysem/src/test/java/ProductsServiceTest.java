
import com.tr.demo.advice.exception.ProductAlreadyExistException;
import com.tr.demo.entity.ProductsEntity;
import com.tr.demo.model.request.CreateProducRequest;
import com.tr.demo.model.response.ProductListResponse;
import com.tr.demo.model.response.ProductResponse;
import com.tr.demo.repository.ProductsRepository;
import com.tr.demo.service.ProductsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductsServiceTest {

    @InjectMocks
    private ProductsService productsService;

    @Mock
    private ProductsRepository productsRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnAllProductsSuccessfully() {
        ProductsEntity product1 = ProductsEntity.builder()
                .name("Product1")
                .price(100.0)
                .stocks(10)
                .build();

        ProductsEntity product2 = ProductsEntity.builder()
                .name("Product2")
                .price(200.0)
                .stocks(20)
                .build();

        when(productsRepository.findAll()).thenReturn(List.of(product1, product2));

        ProductListResponse response = productsService.getAllProducts();

        assertNotNull(response);
        assertEquals(2, response.getProducts().size());
        assertEquals("Product1", response.getProducts().get(0).getProductName());
        assertEquals(100.0, response.getProducts().get(0).getPrice());
        assertEquals(10, response.getProducts().get(0).getStocks());
        verify(productsRepository, times(1)).findAll();
    }

    @Test
    void shouldAddProductSuccessfully() {
        CreateProducRequest request = CreateProducRequest.builder()
                .productName("Product1")
                .price(100.0)
                .stocks(10)
                .build();

        when(productsRepository.findByName("Product1")).thenReturn(Optional.empty());

        ProductsEntity savedProduct = ProductsEntity.builder()
                .name("Product1")
                .price(100.0)
                .stocks(10)
                .build();

        when(productsRepository.save(any(ProductsEntity.class))).thenReturn(savedProduct);

        ProductResponse response = productsService.addProduct(request);

        assertNotNull(response);
        assertEquals("Product1", response.getProductName());
        assertEquals(100.0, response.getPrice());
        assertEquals(10, response.getStocks());
        verify(productsRepository, times(1)).findByName("Product1");
        verify(productsRepository, times(1)).save(any(ProductsEntity.class));
    }

    @Test
    void shouldThrowExceptionWhenProductAlreadyExists() {
        CreateProducRequest request = CreateProducRequest.builder()
                .productName("Product1")
                .price(100.0)
                .stocks(10)
                .build();

        ProductsEntity existingProduct = ProductsEntity.builder()
                .name("Product1")
                .price(100.0)
                .stocks(10)
                .build();

        when(productsRepository.findByName("Product1")).thenReturn(Optional.of(existingProduct));

        assertThrows(ProductAlreadyExistException.class, () -> productsService.addProduct(request));
        verify(productsRepository, times(1)).findByName("Product1");
        verify(productsRepository, never()).save(any(ProductsEntity.class));
    }
}