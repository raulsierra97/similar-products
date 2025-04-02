package com.raulsierra.similarproducts.infrastructure.adapter.out;

import com.raulsierra.similarproducts.application.port.out.ProductDetailPort;
import com.raulsierra.similarproducts.domain.exception.ExternalServiceException;
import com.raulsierra.similarproducts.domain.exception.ProductDetailNotFoundException;
import com.raulsierra.similarproducts.domain.model.ProductDetail;
import com.raulsierra.similarproducts.domain.model.ProductId;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductDetailRestAdapter implements ProductDetailPort {

    private final WebClient webClient;

    @CircuitBreaker(name = "productApi", fallbackMethod = "getProductDetailFallback")
    @Retry(name = "productApi")
    @Override
    public Mono<ProductDetail> getProductDetail(ProductId productId) {
        return webClient.get()
                .uri("/product/{productId}", productId.getId())
                .retrieve()
                .bodyToMono(ProductDetail.class)
                .onErrorResume(WebClientResponseException.NotFound.class, e -> {
                    log.error("Product detail not found for productId: {}", productId.getId(), e);
                    throw new ProductDetailNotFoundException(
                            "Product detail not found for product ID: " + productId.getId(), e);
                })
                .onErrorResume(WebClientResponseException.class, e -> {
                    log.error("Error retrieving product detail for productId: {}", productId.getId(), e);
                    throw new ExternalServiceException("Error calling product API", e);
                });
    }

    public Mono<ProductDetail> getProductDetailFallback(ProductId productId, Throwable t) {
        return Mono.empty();
    }
}

