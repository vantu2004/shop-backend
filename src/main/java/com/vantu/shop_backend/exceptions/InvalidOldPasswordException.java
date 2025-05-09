package com.vantu.shop_backend.exceptions;

public class InvalidOldPasswordException extends RuntimeException {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public InvalidOldPasswordException(String message) {
        super(message);
    }
}
