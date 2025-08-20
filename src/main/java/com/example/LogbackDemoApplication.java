package com.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableScheduling
@RestController
public class LogbackDemoApplication {

    private static final Logger logger = LoggerFactory.getLogger(LogbackDemoApplication.class);

    public static void main(String[] args) {
        logger.info("应用程序启动中...");
        SpringApplication.run(LogbackDemoApplication.class, args);
        logger.info("应用程序启动完成");
    }

    @GetMapping("/")
    public String home() {
        logger.info("收到首页请求");
        return "Hello! 这是一个演示logback配置的Spring Boot应用";
    }

    @GetMapping("/test-logs")
    public String testLogs() {
        logger.trace("这是TRACE级别的日志");
        logger.debug("这是DEBUG级别的日志");
        logger.info("这是INFO级别的日志");
        logger.warn("这是WARN级别的日志");
        logger.error("这是ERROR级别的日志");
        
        return "已生成各级别日志，请查看日志文件";
    }

    @GetMapping("/simulate-error")
    public String simulateError() {
        try {
            throw new RuntimeException("模拟异常情况");
        } catch (Exception e) {
            logger.error("捕获到异常", e);
            return "异常已记录到错误日志文件";
        }
    }

    // 定时任务：每分钟生成一些日志，用于测试日志轮转
    @Scheduled(fixedRate = 60000)
    public void scheduledLogging() {
        logger.info("定时任务执行 - 当前时间: {}", System.currentTimeMillis());
        
        // 生成一些不同级别的日志
        if (System.currentTimeMillis() % 3 == 0) {
            logger.warn("定时任务警告日志");
        }
        
        if (System.currentTimeMillis() % 5 == 0) {
            logger.error("定时任务错误日志");
        }
    }
}