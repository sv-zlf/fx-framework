package com.fxly.demo.system.annotation;
import com.fxly.demo.system.constant.LogType;
import java.lang.annotation.*;
/**
 * 系统日志注解
 * 用于记录操作日志
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogOperation {
    
    /**
     * 模块名称
     */
    String module() default "";
    
    /**
     * 操作类型
     */
    LogType type() default LogType.OTHER;
    
    /**
     * 操作描述
     */
    String description() default "";
    
    /**
     * 是否保存请求参数
     */
    boolean saveRequestData() default true;
    
    /**
     * 是否保存响应参数
     */
    boolean saveResponseData() default false;
}
