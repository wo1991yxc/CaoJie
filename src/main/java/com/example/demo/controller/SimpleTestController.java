package com.example.demo.controller;

import com.example.demo.model.OrderRequest;
import com.example.demo.model.OrderResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 简单测试控制器
 * 用于验证基本Spring Boot功能
 */
@RestController
@RequestMapping("/api/test")
public class SimpleTestController {
    
    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }
    
    @GetMapping("/health")
    public String health() {
        return "OK";
    }
    
    @PostMapping("/simple-order")
    public OrderResult createSimpleOrder(@RequestBody OrderRequest request) {
        // 简单测试，不使用AOP
        return new OrderResult("TEST-001", "测试订单创建成功", true);
    }
    
    @PostMapping("/test-json")
    public String testJson(@RequestBody OrderRequest request) {
        return "{\"orderId\":\"TEST-002\",\"message\":\"JSON测试成功\",\"success\":true}";
    }
    
    @PostMapping("/test-map")
    public Map<String, Object> testMap(@RequestBody OrderRequest request) {
        Map<String, Object> result = new HashMap<>();
        result.put("orderId", "TEST-003");
        result.put("message", "Map对象测试成功");
        result.put("success", true);
        return result;
    }
}