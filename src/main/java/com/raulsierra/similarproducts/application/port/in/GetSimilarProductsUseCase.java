package com.raulsierra.similarproducts.application.port.in;


import com.raulsierra.similarproducts.domain.model.ProductDetail;
import com.raulsierra.similarproducts.domain.model.ProductId;
import reactor.core.publisher.Flux;

public interface GetSimilarProductsUseCase {

    Flux<ProductDetail> getSimilarProducts(ProductId productId);
}
