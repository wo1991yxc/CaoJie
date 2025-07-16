package com.example.demo.aspect;

import com.example.demo.annotation.BusinessLog;
import com.example.demo.context.ProcessTraceContext;
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
import java.util.Date;

/**
 * 业务日志AOP切面
 * 统一处理业务流程日志记录
 */
@Aspect
@Component
public class BusinessLogAspect {
    
    private static final Logger logger = LoggerFactory.getLogger(BusinessLogAspect.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Pointcut("@annotation(com.example.demo.annotation.BusinessLog)")
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