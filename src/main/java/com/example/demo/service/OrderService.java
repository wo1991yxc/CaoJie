package com.example.demo.service;

import com.example.demo.annotation.BusinessLog;
import com.example.demo.context.ProcessTraceContext;
import com.example.demo.exception.BusinessException;
import com.example.demo.model.Order;
import com.example.demo.model.OrderRequest;
import com.example.demo.model.OrderResult;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * 订单业务服务
 * 演示涉及多个类和方法的业务流程日志记录
 */
@Service
public class OrderService {
    
    @BusinessLog(
        module = "订单管理", 
        operation = "创建订单", 
        logArgs = true, 
        logResult = true
    )
    public OrderResult createOrder(OrderRequest request) {
        // 设置流程名称
        ProcessTraceContext.setProcessName("订单创建流程");
        
        // 业务逻辑流程
        validateOrder(request);
        Order order = buildOrder(request);
        saveOrder(order);
        notifyStockService(order);
        
        return new OrderResult(order.getId(), "订单创建成功");
    }
    
    @BusinessLog(
        module = "订单管理", 
        operation = "验证订单信息"
    )
    private void validateOrder(OrderRequest request) {
        // 验证逻辑
        if (request.getProductId() == null || request.getProductId().trim().isEmpty()) {
            throw new BusinessException("PRODUCT_ID_EMPTY", "商品ID不能为空");
        }
        
        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new BusinessException("INVALID_QUANTITY", "商品数量必须大于0");
        }
        
        if (request.getPrice() == null || request.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("INVALID_PRICE", "商品价格必须大于0");
        }
        
        if (request.getUserId() == null || request.getUserId().trim().isEmpty()) {
            throw new BusinessException("USER_ID_EMPTY", "用户ID不能为空");
        }
        
        // 模拟一些业务验证耗时
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    @BusinessLog(
        module = "订单管理", 
        operation = "构建订单对象"
    )
    private Order buildOrder(OrderRequest request) {
        // 生成订单ID
        String orderId = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        // 构建订单对象
        Order order = new Order(
            orderId,
            request.getProductId(),
            request.getProductName(),
            request.getQuantity(),
            request.getPrice(),
            request.getUserId()
        );
        
        // 模拟一些业务处理耗时
        try {
            Thread.sleep(30);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        return order;
    }
    
    @BusinessLog(
        module = "订单管理", 
        operation = "保存订单到数据库"
    )
    private void saveOrder(Order order) {
        // 模拟数据库保存操作
        System.out.println("正在保存订单到数据库: " + order.getId());
        
        // 模拟数据库操作耗时
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // 设置订单状态
        order.setStatus("SAVED");
        System.out.println("订单保存成功: " + order.getId());
    }
    
    @BusinessLog(
        module = "订单管理", 
        operation = "通知库存服务"
    )
    private void notifyStockService(Order order) {
        // 模拟调用库存服务
        System.out.println("正在通知库存服务，订单ID: " + order.getId() + ", 商品ID: " + order.getProductId() + ", 数量: " + order.getQuantity());
        
        // 模拟服务调用耗时
        try {
            Thread.sleep(80);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println("库存服务通知成功");
    }
    
    @BusinessLog(
        module = "订单管理", 
        operation = "查询订单信息"
    )
    public Order getOrderById(String orderId) {
        // 验证参数
        if (orderId == null || orderId.trim().isEmpty()) {
            throw new BusinessException("ORDER_ID_EMPTY", "订单ID不能为空");
        }
        
        // 模拟从数据库查询
        System.out.println("正在查询订单: " + orderId);
        
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // 模拟返回订单数据
        return new Order(
            orderId,
            "PROD-001",
            "示例商品",
            2,
            new BigDecimal("99.99"),
            "USER-001"
        );
    }
    
    @BusinessLog(
        module = "订单管理", 
        operation = "取消订单"
    )
    public OrderResult cancelOrder(String orderId) {
        // 验证参数
        if (orderId == null || orderId.trim().isEmpty()) {
            throw new BusinessException("ORDER_ID_EMPTY", "订单ID不能为空");
        }
        
        // 查询订单
        Order order = getOrderById(orderId);
        
        // 更新订单状态
        updateOrderStatus(orderId, "CANCELLED");
        
        // 恢复库存
        restoreStock(order);
        
        return new OrderResult(orderId, "订单取消成功");
    }
    
    @BusinessLog(
        module = "订单管理", 
        operation = "更新订单状态"
    )
    private void updateOrderStatus(String orderId, String status) {
        System.out.println("正在更新订单状态，订单ID: " + orderId + ", 新状态: " + status);
        
        try {
            Thread.sleep(30);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println("订单状态更新成功");
    }
    
    @BusinessLog(
        module = "订单管理", 
        operation = "恢复库存"
    )
    private void restoreStock(Order order) {
        System.out.println("正在恢复库存，商品ID: " + order.getProductId() + ", 数量: " + order.getQuantity());
        
        try {
            Thread.sleep(60);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println("库存恢复成功");
    }
}