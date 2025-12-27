package com.fxly.demo.system.quartz;

/**
 * 定时任务调用目标接口
 * 所有需要被Quartz调度的任务类都需要实现此接口
 * @author admin
 */
public interface InvokeTarget {
    
    /**
     * 任务执行方法
     * @param taskId 任务ID
     * @param taskName 任务名称
     * @return 执行结果
     */
    String invoke(Long taskId, String taskName);
}