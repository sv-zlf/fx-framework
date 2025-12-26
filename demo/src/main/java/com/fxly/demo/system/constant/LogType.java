package com.fxly.demo.system.constant;

/**
 * 操作类型枚举
 */
public enum LogType {
    
    /**
     * 其他
     */
    OTHER("其他"),
    
    /**
     * 新增
     */
    INSERT("新增"),
    
    /**
     * 修改
     */
    UPDATE("修改"),
    
    /**
     * 删除
     */
    DELETE("删除"),
    
    /**
     * 查询
     */
    SELECT("查询"),
    
    /**
     * 导出
     */
    EXPORT("导出"),
    
    /**
     * 导入
     */
    IMPORT("导入"),
    
    /**
     * 登录
     */
    LOGIN("登录"),
    
    /**
     * 登出
     */
    LOGOUT("登出");
    
    private final String description;
    
    LogType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
