package com.fxly.demo.api.core.task;

import com.fxly.demo.system.quartz.InvokeTarget;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 示例定时任务
 * @author admin
 */
@Slf4j
@Component("sampleTask")
public class SampleTask implements InvokeTarget {

    @Override
    public String invoke(Long taskId, String taskName) {
        log.info("执行示例任务：taskId={}, taskName={}", taskId, taskName);
        
        try {
            // 这里编写具体的业务逻辑
            // 例如：
            // 1. 清理过期数据
            // 2. 生成报表
            // 3. 发送通知
            // 4. 数据同步
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String now = LocalDateTime.now().format(formatter);
            
            // 模拟任务执行
            Thread.sleep(1000);
            
            String result = String.format("任务执行成功，时间：%s", now);
            log.info("示例任务执行完成：{}", result);
            
            return result;
        } catch (Exception e) {
            log.error("示例任务执行失败", e);
            return "任务执行失败：" + e.getMessage();
        }
    }
}