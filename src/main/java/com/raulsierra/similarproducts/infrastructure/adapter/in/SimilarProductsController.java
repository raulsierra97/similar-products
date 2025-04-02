package com.raulsierra.similarproducts.infrastructure.adapter.in;

import com.raulsierra.similarproducts.application.port.in.GetSimilarProductsUseCase;
import com.raulsierra.similarproducts.domain.model.ProductDetail;
import com.raulsierra.similarproducts.domain.model.ProductId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@Slf4j
@RestController
@RequestMapping("/product/{productId}/similar")
@RequiredArgsConstructor
public class SimilarProductsController {

    private final GetSimilarProductsUseCase getSimilarProductsUseCase;

    /**
     * Retrieves similar product recommendations for a given product ID.
     * Uses Flux to stream product recommendations asynchronously, allowing:
     * - Non-blocking processing of multiple results
     * - Immediate delivery of each recommendation as it's available
     */
    @GetMapping
    public ResponseEntity<Flux<ProductDetail>> getSimilarProducts(@PathVariable String productId) {
        Flux<ProductDetail> similarProducts = getSimilarProductsUseCase.getSimilarProducts(new ProductId(productId));
        return ResponseEntity.ok(similarProducts);
    }
}
