package com.hyl.component.export.exception;

/**
 * @Description: 未找到对应的处理器异常
 */
public class NotFoundHandlerException extends RuntimeException {

    public NotFoundHandlerException(String message) {
        super(message);
    }
}
