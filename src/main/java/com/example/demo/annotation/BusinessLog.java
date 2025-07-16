package com.example.demo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 业务日志注解
 * 用于标记需要记录业务流程日志的方法
 */
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