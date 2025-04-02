package com.raulsierra.similarproducts.domain.exception;

public class SimilarProductsNotFoundException extends RuntimeException{
    public SimilarProductsNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
