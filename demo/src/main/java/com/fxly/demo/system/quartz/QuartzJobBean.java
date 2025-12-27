package com.fxly.demo.system.quartz;

import com.fxly.demo.api.core.entity.SystemCronTask;
import com.fxly.demo.api.core.service.ISystemCronTaskService;
import com.fxly.demo.system.quartz.InvokeTarget;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

import org.quartz.*;
import org.springframework.stereotype.Component;

/**
 * Quartz Job Bean
 * @author admin
 */
@Slf4j
@Component
public class QuartzJobBean implements Job {

    @Resource
    private ISystemCronTaskService systemCronTaskService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        Long taskId = dataMap.getLong("taskId");
        String taskName = dataMap.getString("taskName");

        log.info("执行定时任务：taskId={}, taskName={}", taskId, taskName);

        // 查询任务信息
        SystemCronTask task = systemCronTaskService.getById(taskId);
        if (task == null) {
            log.error("任务不存在：taskId={}", taskId);
            return;
        }

        // 检查任务状态
        if (task.getStatus() == 1) {
            log.warn("任务已暂停，跳过执行：taskId={}", taskId);
            return;
        }

        // 检查是否允许并发
        if (task.getConcurrent() == 0) {
            // 检查是否有相同任务正在执行
//            Trigger trigger = context.getTrigger();
//            if (trigger.getFireTimeBefore(new Date()) != null) {
//                log.warn("任务禁止并发，跳过执行：taskId={}", taskId);
//                return;
//            }
        }

        // 执行任务
        try {
            Class<?> clazz = Class.forName(task.getInvokeTarget());
            if (!InvokeTarget.class.isAssignableFrom(clazz)) {
                throw new JobExecutionException("任务类未实现InvokeTarget接口：" + task.getInvokeTarget());
            }

            InvokeTarget invokeTarget = (InvokeTarget) clazz.getDeclaredConstructor().newInstance();
            String result = invokeTarget.invoke(taskId, taskName);

            // 更新执行结果
            systemCronTaskService.updateExecutionResult(taskId, result, true);
            log.info("定时任务执行成功：taskId={}, result={}", taskId, result);

        } catch (ClassNotFoundException e) {
            log.error("任务类不存在：{}", task.getInvokeTarget(), e);
            systemCronTaskService.updateExecutionResult(taskId, "任务类不存在：" + task.getInvokeTarget(), false);
        } catch (Exception e) {
            log.error("定时任务执行失败：taskId={}", taskId, e);
            systemCronTaskService.updateExecutionResult(taskId, "执行失败：" + e.getMessage(), false);
            throw new JobExecutionException(e);
        }
    }
}