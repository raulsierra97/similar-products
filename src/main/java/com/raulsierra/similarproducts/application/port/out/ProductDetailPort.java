package com.raulsierra.similarproducts.application.port.out;

import com.raulsierra.similarproducts.domain.model.ProductDetail;
import com.raulsierra.similarproducts.domain.model.ProductId;
import reactor.core.publisher.Mono;

public interface ProductDetailPort {

    Mono<ProductDetail> getProductDetail(ProductId productId);
}
