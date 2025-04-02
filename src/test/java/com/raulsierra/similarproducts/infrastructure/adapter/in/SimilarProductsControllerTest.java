package com.raulsierra.similarproducts.infrastructure.adapter.in;

import com.raulsierra.similarproducts.application.port.in.GetSimilarProductsUseCase;
import com.raulsierra.similarproducts.domain.model.ProductDetail;
import com.raulsierra.similarproducts.domain.model.ProductId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SimilarProductsControllerTest {

    @Mock
    private GetSimilarProductsUseCase getSimilarProductsUseCase;

    @InjectMocks
    private SimilarProductsController similarProductsController;

    @Test
    void getSimilarProducts_shouldReturnOkWithProductDetails() {
        ProductId productId = new ProductId("1");
        List<ProductDetail> productDetails = Arrays.asList(new ProductDetail(), new ProductDetail());

        when(getSimilarProductsUseCase.getSimilarProducts(productId)).thenReturn(Flux.fromIterable(productDetails));

        ResponseEntity<Flux<ProductDetail>> response = similarProductsController.getSimilarProducts("1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        StepVerifier.create(response.getBody())
                .expectNextCount(2)
                .expectComplete()
                .verify();
    }
}