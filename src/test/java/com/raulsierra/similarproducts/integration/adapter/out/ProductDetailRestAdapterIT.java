package com.raulsierra.similarproducts.integration.adapter.out;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.raulsierra.similarproducts.domain.exception.ExternalServiceException;
import com.raulsierra.similarproducts.domain.exception.ProductDetailNotFoundException;
import com.raulsierra.similarproducts.domain.model.ProductDetail;
import com.raulsierra.similarproducts.domain.model.ProductId;
import com.raulsierra.similarproducts.infrastructure.adapter.out.ProductDetailRestAdapter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest
class ProductDetailRestAdapterIT {

    private WireMockServer wireMockServer;

    @Autowired
    private ProductDetailRestAdapter productDetailRestAdapter;

    @Autowired
    private WebClient webClient;

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer(3001);
        wireMockServer.start();
        configureFor("localhost", 3001);
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @Test
    void getProductDetail_shouldReturnProductDetail() {
        ProductId productId = new ProductId("1");
        ProductDetail expectedProduct = new ProductDetail("1", "Shirt", 9.99, true);

        Mono<ProductDetail> result = productDetailRestAdapter.getProductDetail(productId);

        StepVerifier.create(result)
                .expectNext(expectedProduct)
                .verifyComplete();

        verify(getRequestedFor(urlEqualTo("/product/1")));
    }

    @Test
    void getProductDetail_shouldReturnErrorWhenApiReturn404() {
        ProductId productId = new ProductId("5");

        Mono<ProductDetail> result = productDetailRestAdapter.getProductDetail(productId);

        // Verifies that the flux completes without emitting any elements.
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof ProductDetailNotFoundException)
                .verify();

        verify(getRequestedFor(urlEqualTo("/product/5")));
    }

    @Test
    void getProductDetail_shouldReturnErrorWhenApiReturn500() {
        ProductId productId = new ProductId("6");

        Mono<ProductDetail> result = productDetailRestAdapter.getProductDetail(productId);

        // Verifies that the flux completes without emitting any elements.
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof ExternalServiceException)
                .verify();

        verify(getRequestedFor(urlEqualTo("/product/6")));
    }
}