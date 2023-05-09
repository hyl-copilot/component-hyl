package com.hyl.component.api.exception;

/**
 * @author hyl
 */
public class AuthenticationException extends RuntimeException{
    public AuthenticationException(String message) {
        super(message);
    }
}
