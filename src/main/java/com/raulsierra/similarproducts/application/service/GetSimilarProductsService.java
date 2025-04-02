package com.raulsierra.similarproducts.application.service;


import com.raulsierra.similarproducts.application.port.in.GetSimilarProductsUseCase;
import com.raulsierra.similarproducts.application.port.out.ProductDetailPort;
import com.raulsierra.similarproducts.application.port.out.SimilarProductIdsPort;
import com.raulsierra.similarproducts.domain.model.ProductDetail;
import com.raulsierra.similarproducts.domain.model.ProductId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetSimilarProductsService implements GetSimilarProductsUseCase {

    private final SimilarProductIdsPort similarProductsIdsPort;
    private final ProductDetailPort productDetailPort;

    @Override
    public Flux<ProductDetail> getSimilarProducts(ProductId productId) {
        return similarProductsIdsPort.getSimilarProductIds(productId)
                .flatMap(this::safeGetProductDetail);
    }

    private Mono<ProductDetail> safeGetProductDetail(ProductId id) {
        return productDetailPort.getProductDetail(id)
                .onErrorResume(error -> {
                    log.error("Error getting product detail for {}: {}", id, error.getMessage());
                    return Mono.empty();
                    // Ignores the exception not controlled by circuit break and continues with the rest of products
                });
    }
}
