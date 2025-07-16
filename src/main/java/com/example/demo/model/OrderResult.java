package com.example.demo.model;

/**
 * 订单操作结果
 */
public class OrderResult {
    
    private String orderId;
    private String message;
    private boolean success;
    
    public OrderResult() {
    }
    
    public OrderResult(String orderId, String message) {
        this.orderId = orderId;
        this.message = message;
        this.success = true;
    }
    
    public OrderResult(String orderId, String message, boolean success) {
        this.orderId = orderId;
        this.message = message;
        this.success = success;
    }
    
    // Getters and Setters
    public String getOrderId() {
        return orderId;
    }
    
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    @Override
    public String toString() {
        return "OrderResult{" +
                "orderId='" + orderId + '\'' +
                ", message='" + message + '\'' +
                ", success=" + success +
                '}';
    }
}