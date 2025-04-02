package com.raulsierra.similarproducts.integration.adapter.out;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.raulsierra.similarproducts.domain.exception.SimilarProductsNotFoundException;
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
        wireMockServer = new WireMockServer(3001);
        wireMockServer.start();
        configureFor("localhost", 3001);
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
    void getSimilarProductIds_shouldReturnErrorWhenApiReturn404() {
        ProductId productId = new ProductId("99");

        Flux<ProductId> result = similarProductsIdsRestAdapter.getSimilarProductIds(productId);

        // Verifies that the flux completes without emitting any elements.
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof SimilarProductsNotFoundException)
                .verify();

        verify(getRequestedFor(urlEqualTo("/product/99/similarids")));
    }


}