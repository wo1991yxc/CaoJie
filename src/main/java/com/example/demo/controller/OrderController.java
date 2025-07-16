package com.example.demo.controller;

import com.example.demo.annotation.BusinessLog;
import com.example.demo.context.ProcessTraceContext;
import com.example.demo.model.Order;
import com.example.demo.model.OrderRequest;
import com.example.demo.model.OrderResult;
import com.example.demo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 订单控制器
 * 提供订单相关的REST API接口
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    @PostMapping
    @BusinessLog(
        module = "订单API", 
        operation = "接收创建订单请求"
    )
    public ResponseEntity<OrderResult> createOrder(@RequestBody OrderRequest request) {
        // 为整个流程生成追踪ID
        String traceId = ProcessTraceContext.generateTraceId();
        ProcessTraceContext.setTraceId(traceId);
        ProcessTraceContext.setProcessName("订单创建业务流程");
        ProcessTraceContext.setStartTime(System.currentTimeMillis());
        
        try {
            OrderResult result = orderService.createOrder(request);
            return ResponseEntity.ok(result);
        } finally {
            ProcessTraceContext.clear();
        }
    }
    
    @GetMapping("/{orderId}")
    @BusinessLog(
        module = "订单API", 
        operation = "接收查询订单请求"
    )
    public ResponseEntity<Order> getOrder(@PathVariable String orderId) {
        // 为查询流程生成追踪ID
        String traceId = ProcessTraceContext.generateTraceId();
        ProcessTraceContext.setTraceId(traceId);
        ProcessTraceContext.setProcessName("订单查询业务流程");
        ProcessTraceContext.setStartTime(System.currentTimeMillis());
        
        try {
            Order order = orderService.getOrderById(orderId);
            return ResponseEntity.ok(order);
        } finally {
            ProcessTraceContext.clear();
        }
    }
    
    @DeleteMapping("/{orderId}")
    @BusinessLog(
        module = "订单API", 
        operation = "接收取消订单请求"
    )
    public ResponseEntity<OrderResult> cancelOrder(@PathVariable String orderId) {
        // 为取消流程生成追踪ID
        String traceId = ProcessTraceContext.generateTraceId();
        ProcessTraceContext.setTraceId(traceId);
        ProcessTraceContext.setProcessName("订单取消业务流程");
        ProcessTraceContext.setStartTime(System.currentTimeMillis());
        
        try {
            OrderResult result = orderService.cancelOrder(orderId);
            return ResponseEntity.ok(result);
        } finally {
            ProcessTraceContext.clear();
        }
    }
    
    @GetMapping("/health")
    @BusinessLog(
        module = "系统监控", 
        operation = "健康检查",
        logArgs = false,
        logResult = false
    )
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("OK");
    }
}