package com.raulsierra.similarproducts.domain.exception;

public class ProductDetailNotFoundException extends RuntimeException{
    public ProductDetailNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
