package com.raulsierra.similarproducts.infrastructure.adapter.out;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.raulsierra.similarproducts.application.port.out.SimilarProductIdsPort;
import com.raulsierra.similarproducts.domain.exception.ExternalServiceException;
import com.raulsierra.similarproducts.domain.exception.SimilarProductsNotFoundException;
import com.raulsierra.similarproducts.domain.model.ProductId;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SimilarProductIdsRestAdapter implements SimilarProductIdsPort {

    private final WebClient webClient;

    private final ObjectMapper objectMapper;


    //this circuit breaker doesn't have fallback method because we want the error go up
    @CircuitBreaker(name = "productApi")
    @Retry(name = "productApi")
    @Override
    public Flux<ProductId> getSimilarProductIds(ProductId productId) {
        // Logic for calling the external API for similar product IDs.
        return webClient.get()
                .uri("/product/{productId}/similarids", productId.getId())
                .retrieve()
                .bodyToMono(String.class)
                // bodyToFlux(String) returns all strings concatenated in one string, so we do a workaround
                .flatMapMany(this::mapJsonToProductIdFlux)
                // we don't retry nor circuit break when similar products are not found
                .onErrorResume(WebClientResponseException.NotFound.class, e -> {
                    throw new SimilarProductsNotFoundException(
                            "Similar products not found for product ID: " + productId.getId(), e);
                })
                // we retry and circuit break when there is another problem with the api call
                .onErrorResume(WebClientResponseException.class, e -> {
                    throw new ExternalServiceException("Error calling product API", e);
                });
    }

    private Flux<ProductId> mapJsonToProductIdFlux(String jsonArrayString) {
        try {
            List<String> ids = objectMapper.readValue(jsonArrayString, new TypeReference<List<String>>() {});
            return Flux.fromIterable(ids).map(ProductId::new);
        } catch (IOException e) {
            return Flux.error(e);
        }
    }
}

