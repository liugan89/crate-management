package com.tk.cratemanagement.exception;

/**
 * 业务验证异常
 * 当业务规则验证失败时抛出
 */
public class BusinessValidationException extends RuntimeException {
    
    public BusinessValidationException(String message) {
        super(message);
    }
    
    public BusinessValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
