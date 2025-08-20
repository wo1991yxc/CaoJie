# Spring Boot Logback 临时文件问题解决方案

## 问题描述

Spring Boot 应用在服务器上偶尔出现临时文件（通常在 `/tmp` 目录下），而且不会自动删除，导致磁盘空间被占用。

## 问题原因分析

### 1. 默认日志路径问题
- Spring Boot 默认可能将日志文件生成在 `/tmp` 目录
- 未明确指定日志文件路径时，系统会使用临时目录
- `/tmp` 目录下的文件清理策略不可控

### 2. 日志滚动策略不当
- 缺少 `cleanHistoryOnStart` 配置
- 没有设置 `maxHistory` 和 `totalSizeCap`
- 日志滚动时产生的临时文件未及时清理

### 3. 异步日志缓存问题
- 日志缓存未及时刷新到磁盘
- 应用异常关闭时缓存数据丢失，留下临时文件

## 解决方案

### 1. 明确指定日志文件路径

在 `application.yml` 中配置：

```yaml
logging:
  file:
    path: /var/logs/app  # 避免使用 /tmp 目录
    # 或者直接指定文件名
    # name: /var/logs/app/application.log
```

### 2. 完善的 logback-spring.xml 配置

关键配置要点：

```xml
<!-- 明确日志路径 -->
<property name="LOG_PATH" value="${LOG_PATH:-/var/logs/app}"/>

<!-- 滚动策略 -->
<rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
    <fileNamePattern>${LOG_PATH}/application.%d{yyyy-MM-dd}.%i.log.gz</fileNamePattern>
    <maxFileSize>100MB</maxFileSize>
    <maxHistory>30</maxHistory>
    <totalSizeCap>3GB</totalSizeCap>
    <!-- 关键：启动时清理历史日志 -->
    <cleanHistoryOnStart>true</cleanHistoryOnStart>
</rollingPolicy>

<!-- 立即刷新到磁盘 -->
<immediateFlush>true</immediateFlush>
```

### 3. 使用异步日志提高性能

```xml
<appender name="ASYNC_FILE" class="ch.qos.logback.classic.AsyncAppender">
    <discardingThreshold>0</discardingThreshold>
    <queueSize>1024</queueSize>
    <includeCallerData>false</includeCallerData>
    <appender-ref ref="FILE"/>
</appender>
```

## 最佳实践建议

### 1. 目录权限设置

```bash
# 创建日志目录
sudo mkdir -p /var/logs/app

# 设置权限
sudo chown -R your-app-user:your-app-group /var/logs/app
sudo chmod 755 /var/logs/app
```

### 2. 系统级别的日志轮转

配置 logrotate（可选，作为双重保险）：

```bash
# /etc/logrotate.d/your-app
/var/logs/app/*.log {
    daily
    rotate 30
    compress
    delaycompress
    missingok
    notifempty
    create 644 your-app-user your-app-group
    postrotate
        # 重新加载应用或发送信号
        systemctl reload your-app || true
    endscript
}
```

### 3. 监控和告警

```bash
# 监控脚本示例
#!/bin/bash
LOG_DIR="/var/logs/app"
MAX_SIZE_GB=5

# 检查日志目录大小
SIZE=$(du -s $LOG_DIR | cut -f1)
SIZE_GB=$((SIZE / 1024 / 1024))

if [ $SIZE_GB -gt $MAX_SIZE_GB ]; then
    echo "警告：日志目录大小超过 ${MAX_SIZE_GB}GB，当前大小：${SIZE_GB}GB"
    # 发送告警
fi
```

### 4. Docker 环境配置

如果使用 Docker，确保日志目录挂载：

```yaml
# docker-compose.yml
version: '3.8'
services:
  your-app:
    image: your-app:latest
    volumes:
      - ./logs:/var/logs/app
    environment:
      - LOG_PATH=/var/logs/app
```

### 5. JVM 参数优化

```bash
# 启动参数
java -jar your-app.jar \
  -Djava.io.tmpdir=/opt/app/tmp \
  -Dlogging.file.path=/var/logs/app \
  -Xms512m -Xmx2g
```

## 验证解决方案

### 1. 检查日志文件位置

```bash
# 确认日志文件在正确位置
ls -la /var/logs/app/

# 检查是否还有临时文件
find /tmp -name "*spring*" -o -name "*logback*" 2>/dev/null
```

### 2. 监控日志轮转

```bash
# 查看日志轮转是否正常
ls -la /var/logs/app/ | grep -E "\.(log|gz)$"

# 检查历史日志清理
find /var/logs/app -name "*.log*" -mtime +30
```

### 3. 应用启动测试

```bash
# 重启应用，检查启动时是否清理了历史日志
tail -f /var/logs/app/application.log | grep -i "clean"
```

## 故障排查

### 1. 权限问题

```bash
# 检查目录权限
ls -ld /var/logs/app/
namei -l /var/logs/app/

# 检查文件权限
ls -la /var/logs/app/
```

### 2. 磁盘空间问题

```bash
# 检查磁盘空间
df -h /var/logs/

# 检查 inode 使用情况
df -i /var/logs/
```

### 3. 配置验证

```bash
# 验证 logback 配置
java -jar your-app.jar --debug | grep -i logback

# 检查配置文件语法
xmllint --noout /path/to/logback-spring.xml
```

## 总结

通过以上配置和最佳实践，可以有效解决 Spring Boot logback 临时文件不删除的问题：

1. ✅ 明确指定日志文件路径，避免使用 `/tmp` 目录
2. ✅ 配置完善的日志滚动策略，包括 `cleanHistoryOnStart`
3. ✅ 使用异步日志提高性能
4. ✅ 设置合理的文件大小和保留期限
5. ✅ 建立监控和告警机制

这些措施将确保日志文件得到妥善管理，避免临时文件堆积占用磁盘空间。