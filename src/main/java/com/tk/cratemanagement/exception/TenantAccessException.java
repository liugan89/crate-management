package com.tk.cratemanagement.exception;

/**
 * 租户访问异常
 * 当用户尝试访问不属于自己租户的资源时抛出
 */
public class TenantAccessException extends RuntimeException {
    
    public TenantAccessException(String message) {
        super(message);
    }
    
    public TenantAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
