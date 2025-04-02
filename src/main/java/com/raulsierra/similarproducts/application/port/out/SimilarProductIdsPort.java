package com.raulsierra.similarproducts.application.port.out;

import com.raulsierra.similarproducts.domain.model.ProductId;
import reactor.core.publisher.Flux;


public interface SimilarProductIdsPort {

    Flux<ProductId> getSimilarProductIds(ProductId productId);
}