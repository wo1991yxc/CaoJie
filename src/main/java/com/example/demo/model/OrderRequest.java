package com.example.demo.model;

import java.math.BigDecimal;

/**
 * 订单创建请求
 */
public class OrderRequest {
    
    private String productId;
    private String productName;
    private Integer quantity;
    private BigDecimal price;
    private String userId;
    
    public OrderRequest() {
    }
    
    public OrderRequest(String productId, String productName, Integer quantity, 
                       BigDecimal price, String userId) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.userId = userId;
    }
    
    // Getters and Setters
    public String getProductId() {
        return productId;
    }
    
    public void setProductId(String productId) {
        this.productId = productId;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    @Override
    public String toString() {
        return "OrderRequest{" +
                "productId='" + productId + '\'' +
                ", productName='" + productName + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", userId='" + userId + '\'' +
                '}';
    }
}