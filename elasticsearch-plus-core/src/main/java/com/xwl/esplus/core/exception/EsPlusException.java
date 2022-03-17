package com.xwl.esplus.core.exception;

/**
 * EsPlus异常类
 * @author xwl
 * @since 2022/3/11 15:38
 */
public class EsPlusException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public EsPlusException(String message) {
        super(message);
    }

    public EsPlusException(Throwable throwable) {
        super(throwable);
    }

    public EsPlusException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
