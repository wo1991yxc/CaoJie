# SpringBoot 关键业务流程规范化日志解决方案

## 概述

本文档提供了一套完整的解决方案，用于为SpringBoot工程中涉及多个类和方法的关键业务流程添加规范化的流程日志。

## 解决方案架构

### 1. 核心组件

- **自定义注解** - 标记需要记录日志的方法
- **AOP切面** - 统一处理日志逻辑  
- **日志配置** - 规范化日志格式和输出
- **流程跟踪** - 为业务流程生成唯一标识

### 2. 技术栈

- SpringBoot 2.x/3.x
- Spring AOP
- Logback/Log4j2
- Jackson (JSON序列化)

## 具体实现

### 1. 添加依赖 (pom.xml)

```xml
<dependencies>
    <!-- SpringBoot Starter -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <!-- AOP支持 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-aop</artifactId>
    </dependency>
    
    <!-- JSON处理 -->
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
    </dependency>
</dependencies>
```

### 2. 自定义日志注解

```java
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BusinessLog {
    
    /**
     * 业务模块名称
     */
    String module() default "";
    
    /**
     * 操作描述
     */
    String operation() default "";
    
    /**
     * 是否记录方法参数
     */
    boolean logArgs() default true;
    
    /**
     * 是否记录返回值
     */
    boolean logResult() default true;
    
    /**
     * 是否记录执行时间
     */
    boolean logExecutionTime() default true;
}
```

### 3. 流程跟踪上下文

```java
public class ProcessTraceContext {
    
    private static final ThreadLocal<String> TRACE_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> PROCESS_NAME = new ThreadLocal<>();
    private static final ThreadLocal<Long> START_TIME = new ThreadLocal<>();
    
    public static void setTraceId(String traceId) {
        TRACE_ID.set(traceId);
    }
    
    public static String getTraceId() {
        return TRACE_ID.get();
    }
    
    public static void setProcessName(String processName) {
        PROCESS_NAME.set(processName);
    }
    
    public static String getProcessName() {
        return PROCESS_NAME.get();
    }
    
    public static void setStartTime(long startTime) {
        START_TIME.set(startTime);
    }
    
    public static Long getStartTime() {
        return START_TIME.get();
    }
    
    public static void clear() {
        TRACE_ID.remove();
        PROCESS_NAME.remove();
        START_TIME.remove();
    }
    
    public static String generateTraceId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
}
```

### 4. AOP切面实现

```java
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

@Aspect
@Component
public class BusinessLogAspect {
    
    private static final Logger logger = LoggerFactory.getLogger(BusinessLogAspect.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Pointcut("@annotation(com.example.annotation.BusinessLog)")
    public void businessLogPointcut() {}
    
    @Around("businessLogPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String traceId = ProcessTraceContext.getTraceId();
        
        // 如果没有traceId，生成一个新的
        if (traceId == null) {
            traceId = ProcessTraceContext.generateTraceId();
            ProcessTraceContext.setTraceId(traceId);
        }
        
        // 设置MDC，用于日志输出
        MDC.put("traceId", traceId);
        
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        BusinessLog businessLog = method.getAnnotation(BusinessLog.class);
        
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = method.getName();
        
        try {
            // 记录方法开始日志
            logMethodStart(businessLog, className, methodName, joinPoint.getArgs(), traceId);
            
            // 执行目标方法
            Object result = joinPoint.proceed();
            
            // 记录方法结束日志
            long endTime = System.currentTimeMillis();
            logMethodEnd(businessLog, className, methodName, result, traceId, endTime - startTime);
            
            return result;
            
        } catch (Exception e) {
            // 记录异常日志
            logMethodError(businessLog, className, methodName, e, traceId, System.currentTimeMillis() - startTime);
            throw e;
        } finally {
            MDC.clear();
        }
    }
    
    private void logMethodStart(BusinessLog businessLog, String className, String methodName, 
                               Object[] args, String traceId) {
        try {
            StringBuilder logMsg = new StringBuilder();
            logMsg.append("========== 业务流程开始 ==========\n");
            logMsg.append("流程追踪ID: ").append(traceId).append("\n");
            logMsg.append("业务模块: ").append(businessLog.module()).append("\n");
            logMsg.append("操作描述: ").append(businessLog.operation()).append("\n");
            logMsg.append("执行类: ").append(className).append("\n");
            logMsg.append("执行方法: ").append(methodName).append("\n");
            
            if (businessLog.logArgs() && args != null && args.length > 0) {
                logMsg.append("方法参数: ").append(formatArgs(args)).append("\n");
            }
            
            logMsg.append("开始时间: ").append(new Date()).append("\n");
            logMsg.append("=====================================");
            
            logger.info(logMsg.toString());
            
        } catch (Exception e) {
            logger.warn("记录方法开始日志失败", e);
        }
    }
    
    private void logMethodEnd(BusinessLog businessLog, String className, String methodName, 
                             Object result, String traceId, long executionTime) {
        try {
            StringBuilder logMsg = new StringBuilder();
            logMsg.append("========== 业务流程结束 ==========\n");
            logMsg.append("流程追踪ID: ").append(traceId).append("\n");
            logMsg.append("业务模块: ").append(businessLog.module()).append("\n");
            logMsg.append("操作描述: ").append(businessLog.operation()).append("\n");
            logMsg.append("执行类: ").append(className).append("\n");
            logMsg.append("执行方法: ").append(methodName).append("\n");
            
            if (businessLog.logResult() && result != null) {
                logMsg.append("返回结果: ").append(formatObject(result)).append("\n");
            }
            
            if (businessLog.logExecutionTime()) {
                logMsg.append("执行耗时: ").append(executionTime).append("ms").append("\n");
            }
            
            logMsg.append("结束时间: ").append(new Date()).append("\n");
            logMsg.append("状态: 成功").append("\n");
            logMsg.append("=====================================");
            
            logger.info(logMsg.toString());
            
        } catch (Exception e) {
            logger.warn("记录方法结束日志失败", e);
        }
    }
    
    private void logMethodError(BusinessLog businessLog, String className, String methodName, 
                               Exception exception, String traceId, long executionTime) {
        try {
            StringBuilder logMsg = new StringBuilder();
            logMsg.append("========== 业务流程异常 ==========\n");
            logMsg.append("流程追踪ID: ").append(traceId).append("\n");
            logMsg.append("业务模块: ").append(businessLog.module()).append("\n");
            logMsg.append("操作描述: ").append(businessLog.operation()).append("\n");
            logMsg.append("执行类: ").append(className).append("\n");
            logMsg.append("执行方法: ").append(methodName).append("\n");
            logMsg.append("异常信息: ").append(exception.getMessage()).append("\n");
            logMsg.append("执行耗时: ").append(executionTime).append("ms").append("\n");
            logMsg.append("异常时间: ").append(new Date()).append("\n");
            logMsg.append("状态: 失败").append("\n");
            logMsg.append("=====================================");
            
            logger.error(logMsg.toString(), exception);
            
        } catch (Exception e) {
            logger.warn("记录方法异常日志失败", e);
        }
    }
    
    private String formatArgs(Object[] args) {
        try {
            return objectMapper.writeValueAsString(Arrays.asList(args));
        } catch (Exception e) {
            return Arrays.toString(args);
        }
    }
    
    private String formatObject(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return obj.toString();
        }
    }
}
```

### 5. 日志配置 (logback-spring.xml)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration scan="true" scanPeriod="60 seconds" debug="false">
    
    <!-- 控制台输出 -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level [%X{traceId}] %logger{50} - %msg%n</pattern>
            <charset>UTF-8</charset>
        </encoder>
    </appender>
    
    <!-- 业务日志文件输出 -->
    <appender name="BUSINESS_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/business.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/business.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <maxFileSize>100MB</maxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level [%X{traceId}] %logger{50} - %msg%n</pattern>
            <charset>UTF-8</charset>
        </encoder>
    </appender>
    
    <!-- 专门的业务流程日志记录器 -->
    <logger name="com.example.aspect.BusinessLogAspect" level="INFO" additivity="false">
        <appender-ref ref="BUSINESS_FILE"/>
        <appender-ref ref="CONSOLE"/>
    </logger>
    
    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
    </root>
    
</configuration>
```

### 6. 使用示例

#### 业务服务类示例

```java
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
        
        // 业务逻辑...
        validateOrder(request);
        Order order = buildOrder(request);
        saveOrder(order);
        
        return new OrderResult(order.getId(), "订单创建成功");
    }
    
    @BusinessLog(
        module = "订单管理", 
        operation = "验证订单信息"
    )
    private void validateOrder(OrderRequest request) {
        // 验证逻辑...
        if (request.getProductId() == null) {
            throw new BusinessException("商品ID不能为空");
        }
    }
    
    @BusinessLog(
        module = "订单管理", 
        operation = "构建订单对象"
    )
    private Order buildOrder(OrderRequest request) {
        // 构建订单对象...
        return new Order();
    }
    
    @BusinessLog(
        module = "订单管理", 
        operation = "保存订单到数据库"
    )
    private void saveOrder(Order order) {
        // 保存逻辑...
        orderRepository.save(order);
    }
}
```

#### 控制器类示例

```java
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
}
```

## 日志输出示例

使用上述配置后，日志输出将如下所示：

```
2024-01-15 10:30:15.123 [http-nio-8080-exec-1] INFO  [a1b2c3d4e5f6g7h8] c.e.aspect.BusinessLogAspect - ========== 业务流程开始 ==========
流程追踪ID: a1b2c3d4e5f6g7h8
业务模块: 订单API
操作描述: 接收创建订单请求
执行类: OrderController
执行方法: createOrder
方法参数: [{"productId":"12345","quantity":2,"userId":"user001"}]
开始时间: Mon Jan 15 10:30:15 CST 2024
=====================================

2024-01-15 10:30:15.145 [http-nio-8080-exec-1] INFO  [a1b2c3d4e5f6g7h8] c.e.aspect.BusinessLogAspect - ========== 业务流程开始 ==========
流程追踪ID: a1b2c3d4e5f6g7h8
业务模块: 订单管理
操作描述: 创建订单
执行类: OrderService
执行方法: createOrder
方法参数: [{"productId":"12345","quantity":2,"userId":"user001"}]
开始时间: Mon Jan 15 10:30:15 CST 2024
=====================================

... (其他方法的日志)

2024-01-15 10:30:15.289 [http-nio-8080-exec-1] INFO  [a1b2c3d4e5f6g7h8] c.e.aspect.BusinessLogAspect - ========== 业务流程结束 ==========
流程追踪ID: a1b2c3d4e5f6g7h8
业务模块: 订单管理
操作描述: 创建订单
执行类: OrderService
执行方法: createOrder
返回结果: {"orderId":"ORD001","message":"订单创建成功"}
执行耗时: 144ms
结束时间: Mon Jan 15 10:30:15 CST 2024
状态: 成功
=====================================
```

## 高级特性

### 1. 流程步骤跟踪

```java
@Component
public class ProcessStepTracker {
    
    private static final ThreadLocal<List<ProcessStep>> STEPS = new ThreadLocal<>();
    
    public static void addStep(String stepName, String description) {
        List<ProcessStep> steps = STEPS.get();
        if (steps == null) {
            steps = new ArrayList<>();
            STEPS.set(steps);
        }
        steps.add(new ProcessStep(stepName, description, System.currentTimeMillis()));
    }
    
    public static List<ProcessStep> getSteps() {
        return STEPS.get();
    }
    
    public static void clear() {
        STEPS.remove();
    }
    
    public static class ProcessStep {
        private String stepName;
        private String description;
        private long timestamp;
        
        // 构造函数、getter、setter
    }
}
```

### 2. 异步日志处理

```java
@Component
public class AsyncBusinessLogProcessor {
    
    @Async
    public void processLog(LogEvent logEvent) {
        // 异步处理日志，如发送到消息队列、写入数据库等
        // 避免影响主业务流程性能
    }
}
```

### 3. 条件日志记录

```java
@BusinessLog(
    module = "用户管理",
    operation = "用户登录",
    condition = "#{args[0].isVip()}"  // 只有VIP用户才记录详细日志
)
public LoginResult login(User user) {
    // 登录逻辑
}
```

## 最佳实践

1. **性能考虑**: 大对象序列化可能影响性能，建议对返回值大小进行限制
2. **敏感信息**: 避免在日志中记录密码、身份证号等敏感信息
3. **日志分级**: 根据业务重要性设置不同的日志级别
4. **存储策略**: 重要业务日志建议同时存储到文件和数据库
5. **监控告警**: 对异常日志设置监控告警机制

## 扩展功能

- 集成ELK栈进行日志分析
- 添加链路追踪支持（如Zipkin、Jaeger）
- 集成业务监控指标
- 支持自定义日志格式化器
- 添加日志脱敏功能

这个解决方案提供了完整的业务流程日志记录能力，可以帮助您更好地监控和调试SpringBoot应用中的关键业务流程。