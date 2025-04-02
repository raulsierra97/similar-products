package com.raulsierra.similarproducts.infrastructure.adapter.out;


import com.raulsierra.similarproducts.domain.model.ProductDetail;
import com.raulsierra.similarproducts.domain.model.ProductId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductDetailRestAdapterTest {

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

    @InjectMocks
    private ProductDetailRestAdapter productDetailRestAdapter;

    @SuppressWarnings("unchecked")
    @Test
    void getProductDetail_shouldReturnProductDetail() {
        ProductId productId = new ProductId("1");
        ProductDetail productDetail = new ProductDetail("1", "Product 1", 7.5, true);

        when(webClientMock.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri("/product/{productId}", "1")).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(ProductDetail.class)).thenReturn(Mono.just(productDetail));

        Mono<ProductDetail> result = productDetailRestAdapter.getProductDetail(productId);

        StepVerifier.create(result)
                .expectNext(productDetail)
                .verifyComplete();
    }

    @SuppressWarnings("unchecked")
    @Test
    void getProductDetail_shouldReturnNullWhenBodyIsNull() {
        // Arrange
        ProductId productId = new ProductId("1");

        when(webClientMock.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri("/product/{productId}", "1")).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(ProductDetail.class)).thenReturn(Mono.empty());

        Mono<ProductDetail> result = productDetailRestAdapter.getProductDetail(productId);

        // Verifies that the mono completes without emitting any element.
        StepVerifier.create(result)
                .verifyComplete();
    }
}