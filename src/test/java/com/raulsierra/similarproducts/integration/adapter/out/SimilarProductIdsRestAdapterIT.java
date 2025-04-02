package com.raulsierra.similarproducts.integration.adapter.out;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.raulsierra.similarproducts.domain.model.ProductId;
import com.raulsierra.similarproducts.infrastructure.adapter.out.SimilarProductIdsRestAdapter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest
class SimilarProductIdsRestAdapterIT {

    private WireMockServer wireMockServer;

    @Autowired
    private SimilarProductIdsRestAdapter similarProductsIdsRestAdapter;

    @Autowired
    private WebClient webClient;

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer(8089);
        wireMockServer.start();
        configureFor("localhost", 8089);
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @Test
    void getSimilarProductIds_shouldReturnProductIds() {
        ProductId productId = new ProductId("1");

        Flux<ProductId> result = similarProductsIdsRestAdapter.getSimilarProductIds(productId);

        StepVerifier.create(result)
                .expectNext(new ProductId("2"))
                .expectNext(new ProductId("3"))
                .expectNext(new ProductId("4"))
                .verifyComplete();

        verify(getRequestedFor(urlEqualTo("/product/1/similarids")));
    }

    @Test
    void getSimilarProductIds_shouldReturnEmptyListWhenApiReturn404() {
        ProductId productId = new ProductId("5");

        Flux<ProductId> result = similarProductsIdsRestAdapter.getSimilarProductIds(productId);

        // Verifies that the flux completes without emitting any elements.
        StepVerifier.create(result).verifyComplete();

        verify(getRequestedFor(urlEqualTo("/product/5/similarids")));
    }

    @Test
    void getSimilarProductIds_shouldReturnEmptyListWhenApiReturn500() {
        ProductId productId = new ProductId("6");

        Flux<ProductId> result = similarProductsIdsRestAdapter.getSimilarProductIds(productId);

        // Verifies that the flux completes without emitting any elements.
        StepVerifier.create(result).verifyComplete();

        verify(getRequestedFor(urlEqualTo("/product/6/similarids")));
    }

    @Test
    void getSimilarProductIds_shouldReturnEmptyListWhenBodyIsInvalid() {
        ProductId productId = new ProductId("7");

        Flux<ProductId> result = similarProductsIdsRestAdapter.getSimilarProductIds(productId);

        // Verifies that the flux completes without emitting any elements.
        StepVerifier.create(result).verifyComplete();

        verify(getRequestedFor(urlEqualTo("/product/7/similarids")));
    }
}