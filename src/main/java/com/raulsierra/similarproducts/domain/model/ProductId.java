package com.raulsierra.similarproducts.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@NoArgsConstructor
public class ProductId {

    public ProductId(String id) {
        if (Objects.isNull(id) || id.isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null or empty");
        }
        this.id = id;
    }

    private String id;
}