package com.raulsierra.similarproducts.integration.adapter.in;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.raulsierra.similarproducts.domain.model.ProductDetail;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureWebTestClient
class SimilarProductsControllerIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ObjectMapper objectMapper;

    private WireMockServer wireMockServer;

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
    void getSimilarProducts_shouldReturnOkWithProductDetails() throws Exception {

        List<ProductDetail> expectedProducts = List.of(
                new ProductDetail("2", "Dress", 19.99, true),
                new ProductDetail("3", "Blazer", 29.99, false),
                new ProductDetail("4", "Boots", 39.99, true)
        );

        // Product 1 has similar products, so the endpoint return the list of product details correctly
        webTestClient.get()
                .uri("/product/1/similar")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .returnResult(ProductDetail.class)
                .getResponseBody()
                .as(StepVerifier::create)
                .expectNextMatches(expectedProducts::contains)
                .expectNextMatches(expectedProducts::contains)
                .expectNextMatches(expectedProducts::contains)
                .verifyComplete();
    }

    @Test
    void getSimilarProducts_shouldReturnErrorWhenSimilarIdsNotFound() {
        // Product 99 returns 404 because has no similar products
        webTestClient.get()
                .uri("/product/99/similar")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectHeader().contentType(MediaType.APPLICATION_JSON);
    }

}