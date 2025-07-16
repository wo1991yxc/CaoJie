# SpringBoot 业务流程日志解决方案 Demo

这是一个完整的SpringBoot项目demo，演示了如何为关键业务流程添加规范化的流程日志。项目采用AOP（面向切面编程）方式，通过自定义注解来统一管理多个类和方法的日志记录。

## 项目特性

- ✅ **自定义注解** - `@BusinessLog`注解标记需要记录日志的方法
- ✅ **AOP切面** - 统一拦截处理，避免重复编写日志代码
- ✅ **流程追踪** - 使用TraceId将整个业务流程的日志串联
- ✅ **规范化格式** - 统一的日志输出格式，包含模块、操作、参数、返回值、执行时间等
- ✅ **非侵入性** - 通过注解方式，不影响原有业务代码
- ✅ **灵活配置** - 可选择记录参数、返回值、执行时间等

## 项目结构

```
src/main/java/com/example/demo/
├── annotation/           # 自定义注解
│   └── BusinessLog.java
├── aspect/              # AOP切面
│   └── BusinessLogAspect.java
├── context/             # 上下文管理
│   └── ProcessTraceContext.java
├── controller/          # REST控制器
│   └── OrderController.java
├── service/             # 业务服务
│   └── OrderService.java
├── model/               # 数据模型
│   ├── Order.java
│   ├── OrderRequest.java
│   └── OrderResult.java
├── exception/           # 异常定义
│   └── BusinessException.java
└── DemoApplication.java # 主启动类
```

## 快速开始

### 1. 环境要求

- Java 8+
- Maven 3.6+

### 2. 运行项目

```bash
# 编译项目
mvn clean compile

# 运行项目
mvn spring-boot:run
```

项目启动后，访问 http://localhost:8080

### 3. 测试API接口

#### 创建订单
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "productId": "PROD-001",
    "productName": "测试商品",
    "quantity": 2,
    "price": 99.99,
    "userId": "USER-001"
  }'
```

#### 查询订单
```bash
curl -X GET http://localhost:8080/api/orders/ORD-12345678
```

#### 取消订单
```bash
curl -X DELETE http://localhost:8080/api/orders/ORD-12345678
```

#### 健康检查
```bash
curl -X GET http://localhost:8080/api/orders/health
```

## 日志输出示例

当调用创建订单API时，您将看到类似以下的日志输出：

```
2024-01-15 10:30:15.123 [http-nio-8080-exec-1] INFO  [a1b2c3d4e5f6g7h8] c.e.d.aspect.BusinessLogAspect - ========== 业务流程开始 ==========
流程追踪ID: a1b2c3d4e5f6g7h8
业务模块: 订单API
操作描述: 接收创建订单请求
执行类: OrderController
执行方法: createOrder
方法参数: [{"productId":"PROD-001","productName":"测试商品","quantity":2,"price":99.99,"userId":"USER-001"}]
开始时间: Mon Jan 15 10:30:15 CST 2024
=====================================

2024-01-15 10:30:15.145 [http-nio-8080-exec-1] INFO  [a1b2c3d4e5f6g7h8] c.e.d.aspect.BusinessLogAspect - ========== 业务流程开始 ==========
流程追踪ID: a1b2c3d4e5f6g7h8
业务模块: 订单管理
操作描述: 创建订单
执行类: OrderService
执行方法: createOrder
方法参数: [{"productId":"PROD-001","productName":"测试商品","quantity":2,"price":99.99,"userId":"USER-001"}]
开始时间: Mon Jan 15 10:30:15 CST 2024
=====================================

... (其他方法的日志)

2024-01-15 10:30:15.289 [http-nio-8080-exec-1] INFO  [a1b2c3d4e5f6g7h8] c.e.d.aspect.BusinessLogAspect - ========== 业务流程结束 ==========
流程追踪ID: a1b2c3d4e5f6g7h8
业务模块: 订单管理
操作描述: 创建订单
执行类: OrderService
执行方法: createOrder
返回结果: {"orderId":"ORD-A1B2C3D4","message":"订单创建成功","success":true}
执行耗时: 144ms
结束时间: Mon Jan 15 10:30:15 CST 2024
状态: 成功
=====================================
```

## 核心组件说明

### 1. @BusinessLog 注解
```java
@BusinessLog(
    module = "订单管理",      // 业务模块名称
    operation = "创建订单",    // 操作描述
    logArgs = true,          // 是否记录参数
    logResult = true,        // 是否记录返回值
    logExecutionTime = true  // 是否记录执行时间
)
```

### 2. 流程追踪
- 每个业务流程都有唯一的TraceId
- 通过TraceId可以串联整个流程的所有日志
- 支持跨类、跨方法的流程追踪

### 3. 日志格式
- 统一的日志输出格式
- 包含流程追踪ID、业务模块、操作描述等信息
- 记录方法参数、返回值、执行时间
- 异常情况的详细记录

## 日志文件
项目运行后，日志文件将保存在以下位置：
- `logs/business.log` - 业务流程日志
- `logs/application.log` - 应用程序日志

## 使用指南

### 1. 为方法添加日志
只需要在需要记录日志的方法上添加`@BusinessLog`注解：

```java
@BusinessLog(
    module = "用户管理", 
    operation = "用户注册"
)
public UserResult registerUser(UserRequest request) {
    // 业务逻辑
}
```

### 2. 管理流程追踪
在业务流程入口处设置TraceId：

```java
String traceId = ProcessTraceContext.generateTraceId();
ProcessTraceContext.setTraceId(traceId);
ProcessTraceContext.setProcessName("用户注册流程");

try {
    // 业务处理
} finally {
    ProcessTraceContext.clear();
}
```

### 3. 自定义日志格式
可以通过修改`BusinessLogAspect`类来自定义日志格式和内容。

## 扩展功能

该解决方案支持以下扩展：
- 集成ELK栈进行日志分析
- 添加链路追踪支持（如Zipkin、Jaeger）
- 集成业务监控指标
- 支持自定义日志格式化器
- 添加日志脱敏功能

## 最佳实践

1. **性能考虑** - 大对象序列化可能影响性能，建议对返回值大小进行限制
2. **敏感信息** - 避免在日志中记录密码、身份证号等敏感信息
3. **日志分级** - 根据业务重要性设置不同的日志级别
4. **存储策略** - 重要业务日志建议同时存储到文件和数据库
5. **监控告警** - 对异常日志设置监控告警机制

## 许可证

MIT License