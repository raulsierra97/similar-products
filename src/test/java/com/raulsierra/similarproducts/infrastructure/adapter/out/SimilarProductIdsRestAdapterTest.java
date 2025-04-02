package com.raulsierra.similarproducts.infrastructure.adapter.out;

import com.raulsierra.similarproducts.domain.model.ProductId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SimilarProductIdsRestAdapterTest {

    @Mock
    private WebClient webClient;

    @SuppressWarnings("rawtypes")
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @SuppressWarnings("rawtypes")
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private SimilarProductIdsRestAdapter similarProductsIdsRestAdapter;

    @SuppressWarnings("unchecked")
    @Test
    void getSimilarProductIds_shouldReturnProductIds() {
        ProductId productId = new ProductId("1");
        List<String> ids = Arrays.asList("2", "3");

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/product/{productId}/similarids", "1")).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToFlux(String.class).collectList()).thenReturn(Mono.just(ids));

        Flux<ProductId> result = similarProductsIdsRestAdapter.getSimilarProductIds(productId);

        StepVerifier.create(result)
                .expectNext(new ProductId("2"))
                .expectNext(new ProductId("3"))
                .verifyComplete();
    }

    @SuppressWarnings("unchecked")
    @Test
    void getSimilarProductIds_shouldReturnEmptyListWhenBodyIsNull() {
        // Arrange
        ProductId productId = new ProductId("1");

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/product/{productId}/similarids", "1")).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToFlux(String.class).collectList()).thenReturn(Mono.empty());

        // Act
        Flux<ProductId> result = similarProductsIdsRestAdapter.getSimilarProductIds(productId);

        // Verifies that the flux completes without emitting any elements.
        StepVerifier.create(result)
                .verifyComplete();
    }
}