package com.raulsierra.similarproducts.infrastructure.adapter.out;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.raulsierra.similarproducts.domain.exception.SimilarProductsNotFoundException;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SimilarProductIdsRestAdapterTest {

    @Mock
    private WebClient webClientMock;

    @SuppressWarnings("rawtypes")
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpecMock;

    @SuppressWarnings("rawtypes")
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpecMock;

    @Mock
    private WebClient.ResponseSpec responseSpecMock;

    @Mock
    private ObjectMapper objectMapperMock;

    @InjectMocks
    private SimilarProductIdsRestAdapter similarProductsIdsRestAdapter;

    @SuppressWarnings("unchecked")
    @Test
    void getSimilarProductIds_shouldReturnProductIds() throws JsonProcessingException {

        ProductId productId = new ProductId("1");
        List<String> ids = Arrays.asList("2", "3");

        when(webClientMock.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri("/product/{productId}/similarids", "1")).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(String.class)).thenReturn(Mono.just(ids.toString()));
        when(objectMapperMock.readValue(any(String.class), any(TypeReference.class))).thenReturn(ids);

        Flux<ProductId> result = similarProductsIdsRestAdapter.getSimilarProductIds(productId);

        StepVerifier.create(result)
                .expectNextSequence(Arrays.asList(new ProductId("2"),new ProductId("3")))
                .verifyComplete();
    }

    @SuppressWarnings("unchecked")
    @Test
    void getSimilarProductIds_shouldReturnErrorWhenNotFound() {
        ProductId productId = new ProductId("1");

        when(webClientMock.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri("/product/{productId}/similarids", "1"))
                .thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(String.class))
                .thenReturn(Mono.error(new SimilarProductsNotFoundException("Similar products not found", null)));

        Flux<ProductId> result = similarProductsIdsRestAdapter.getSimilarProductIds(productId);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof SimilarProductsNotFoundException)
                .verify();
    }
}