package com.raulsierra.similarproducts.application.service;


import com.raulsierra.similarproducts.application.port.out.ProductDetailPort;
import com.raulsierra.similarproducts.application.port.out.SimilarProductIdsPort;
import com.raulsierra.similarproducts.domain.model.ProductDetail;
import com.raulsierra.similarproducts.domain.model.ProductId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetSimilarProductsServiceTest {

    @Mock
    private SimilarProductIdsPort similarProductsIdsPort;

    @Mock
    private ProductDetailPort productDetailPort;

    @InjectMocks
    private GetSimilarProductsService getSimilarProductsService;

    @Test
    void getSimilarProducts_shouldReturnProductDetails() {
        // Arrange
        ProductId productId = new ProductId("1");
        List<ProductId> similarIds = List.of(new ProductId("2"), new ProductId("3"));
        ProductDetail productDetail1 = new ProductDetail("2", "Product 2", 15.0, true);
        ProductDetail productDetail2 = new ProductDetail("3", "Product 3", 30.0, false);

        when(similarProductsIdsPort.getSimilarProductIds(productId)).thenReturn(Flux.fromIterable(similarIds));
        when(productDetailPort.getProductDetail(new ProductId("2"))).thenReturn(Mono.just(productDetail1));
        when(productDetailPort.getProductDetail(new ProductId("3"))).thenReturn(Mono.just(productDetail2));

        // Act
        Flux<ProductDetail> result = getSimilarProductsService.getSimilarProducts(productId);

        // Assert
        StepVerifier.create(result)
                .expectNext(productDetail1) // Primer producto
                .expectNext(productDetail2)
                .expectComplete() // Verifica que el Flux ha terminado correctamente
                .verify();
    }
}