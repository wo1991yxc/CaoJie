package com.example.demo.context;

import java.util.UUID;

/**
 * 流程追踪上下文
 * 用于管理业务流程的追踪信息
 */
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