package com.fxly.demo.utils.quartz;

import com.fxly.demo.api.core.entity.SystemCronTask;
import com.fxly.demo.api.core.service.ISystemCronTaskService;
import com.fxly.demo.system.global.HttpResultEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Quartz工具类
 * @author admin
 */
@Slf4j
@Component
public class QuartzUtil {

    @Resource
    private ISystemCronTaskService systemCronTaskService;

    /**
     * 创建Job
     */
    public void createJob(Scheduler scheduler, SystemCronTask task) throws SchedulerException {
        Class<?> clazz = null;
        try {
            clazz = Class.forName(task.getInvokeTarget());
        } catch (ClassNotFoundException e) {
            throw new SchedulerException("任务类不存在：" + task.getInvokeTarget());
        }

        // 构建JobDetail
        JobDetail jobDetail = JobBuilder.newJob((Class<? extends Job>) clazz)
                .withIdentity(getJobKey(task))
                .withDescription(task.getDescription())
                .build();

        // 设置任务参数
        jobDetail.getJobDataMap().put("taskId", task.getId());
        jobDetail.getJobDataMap().put("taskName", task.getTaskName());

        // 构建Trigger
        CronTrigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(getTriggerKey(task))
                .withDescription(task.getTaskName())
                .withSchedule(CronScheduleBuilder.cronSchedule(task.getCronExpression()))
                .build();

        // 如果任务状态为暂停，则暂停Job
        if (task.getStatus() == 1) {
            scheduler.scheduleJob(jobDetail, trigger);
            scheduler.pauseJob(getJobKey(task));
        } else {
            scheduler.scheduleJob(jobDetail, trigger);
        }

        log.info("创建定时任务成功：{}", task.getTaskName());
    }

    /**
     * 更新Job
     */
    public  void updateJob(Scheduler scheduler, SystemCronTask task, boolean wasPaused) throws SchedulerException {
        TriggerKey triggerKey = getTriggerKey(task);
        CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
        if (trigger == null) {
            throw new SchedulerException("触发器不存在：" + task.getTaskName());
        }

        // 更新Cron表达式
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(task.getCronExpression());
        trigger = trigger.getTriggerBuilder()
                .withSchedule(cronScheduleBuilder)
                .build();

        // 重新调度
        scheduler.rescheduleJob(triggerKey, trigger);

        // 如果之前是暂停的，现在也暂停
        if (wasPaused) {
            scheduler.pauseJob(getJobKey(task));
        } else {
            // 如果之前不是暂停的，现在也不是暂停的，恢复Job
            if (task.getStatus() != 1) {
                scheduler.resumeJob(getJobKey(task));
            } else {
                scheduler.pauseJob(getJobKey(task));
            }
        }

        log.info("更新定时任务成功：{}", task.getTaskName());
    }

    /**
     * 删除Job
     */
    public static void deleteJob(Scheduler scheduler, SystemCronTask task) throws SchedulerException {
        JobKey jobKey = getJobKey(task);
        if (scheduler.checkExists(jobKey)) {
            scheduler.deleteJob(jobKey);
            log.info("删除定时任务成功：{}", task.getTaskName());
        }
    }

    /**
     * 暂停Job
     */
    public static void pauseJob(Scheduler scheduler, SystemCronTask task) throws SchedulerException {
        JobKey jobKey = getJobKey(task);
        if (scheduler.checkExists(jobKey)) {
            scheduler.pauseJob(jobKey);
            log.info("暂停定时任务成功：{}", task.getTaskName());
        }
    }

    /**
     * 恢复Job
     */
    public static void resumeJob(Scheduler scheduler, SystemCronTask task) throws SchedulerException {
        JobKey jobKey = getJobKey(task);
        if (scheduler.checkExists(jobKey)) {
            scheduler.resumeJob(jobKey);
            log.info("恢复定时任务成功：{}", task.getTaskName());
        }
    }

    /**
     * 立即执行Job
     */
    public static void executeJob(Scheduler scheduler, SystemCronTask task) throws SchedulerException {
        JobKey jobKey = getJobKey(task);
        if (scheduler.checkExists(jobKey)) {
            scheduler.triggerJob(jobKey);
            log.info("立即执行定时任务：{}", task.getTaskName());
        }
    }

    /**
     * 获取JobKey
     */
    private static JobKey getJobKey(SystemCronTask task) {
        return new JobKey(task.getTaskName(), task.getTaskGroup());
    }

    /**
     * 获取TriggerKey
     */
    private static TriggerKey getTriggerKey(SystemCronTask task) {
        return new TriggerKey(task.getTaskName(), task.getTaskGroup());
    }
}