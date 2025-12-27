package com.fxly.demo.api.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fxly.demo.api.core.dto.TaskQueryDTO;
import com.fxly.demo.api.core.entity.SystemCronTask;
import com.fxly.demo.api.core.mapper.SystemCronTaskMapper;
import com.fxly.demo.api.core.service.ISystemCronTaskService;
import com.fxly.demo.system.global.GlobalException;
import com.fxly.demo.system.global.HttpResult;
import com.fxly.demo.system.global.HttpResultEnum;
import com.fxly.demo.system.quartz.QuartzUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 定时任务Service实现类
 * @author admin
 */
@Slf4j
@Service
public class SystemCronTaskServiceImpl extends ServiceImpl<SystemCronTaskMapper, SystemCronTask> 
        implements ISystemCronTaskService {

    @Resource
    private Scheduler scheduler;
    
    @Resource
    private QuartzUtil quartzUtil;

    @Override
    public Page<SystemCronTask> getPageList(TaskQueryDTO taskQueryDto) {
        // 分页
        Page<SystemCronTask> page = new Page<>(taskQueryDto.getPageIndex(), taskQueryDto.getPageSize());
        
        // 查询条件
        LambdaQueryWrapper<SystemCronTask> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(taskQueryDto.getTaskName() != null, SystemCronTask::getTaskName, taskQueryDto.getTaskName())
            .eq(taskQueryDto.getTaskGroup() != null, SystemCronTask::getTaskGroup, taskQueryDto.getTaskGroup())
            .eq(taskQueryDto.getStatus() != null, SystemCronTask::getStatus, taskQueryDto.getStatus())
            .orderByDesc(SystemCronTask::getCreateTime);
        
        return baseMapper.selectPage(page, queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HttpResult saveOrUpdateTask(SystemCronTask task) {
        // 验证Cron表达式
        try {
            org.quartz.CronExpression.validateExpression(task.getCronExpression());
        } catch (Exception e) {
            return HttpResult.error(400, "Cron表达式格式错误");
        }
        
        // 检查任务名称是否重复
        LambdaQueryWrapper<SystemCronTask> checkWrapper = new LambdaQueryWrapper<>();
        checkWrapper.eq(SystemCronTask::getTaskName, task.getTaskName());
        if (task.getId() != null) {
            checkWrapper.ne(SystemCronTask::getId, task.getId());
        }
        if (baseMapper.exists(checkWrapper)) {
            return HttpResult.error(400, "任务名称已存在");
        }
        
        try {
            if (task.getId() == null) {
                // 新增
                task.setStatus(0); // 正常
                task.setConcurrent(1); // 允许并发
                task.setExecutionCount(0);
                task.setFailureCount(0);
                boolean b = save(task);
                if (b) {
                    // 创建Job
                    quartzUtil.createJob(scheduler, task);
                }
                return b ? HttpResult.setResult(HttpResultEnum.INSERT_SUCCESS) 
                        : HttpResult.setResult(HttpResultEnum.INSERT_ERROR);
            } else {
                // 更新
                SystemCronTask dbTask = getById(task.getId());
                if (dbTask == null) {
                    return HttpResult.error(400, "任务不存在");
                }
                // 检查任务是否暂停
                boolean wasPaused = dbTask.getStatus() == 1;
                
                boolean b = updateById(task);
                if (b) {
                    // 更新Job
                    quartzUtil.updateJob(scheduler, task, wasPaused);
                }
                return b ? HttpResult.setResult(HttpResultEnum.UPDATE_SUCCESS)
                        : HttpResult.setResult(HttpResultEnum.UPDATE_ERROR);
            }
        } catch (Exception e) {
            log.error("保存或更新任务失败", e);
            throw new GlobalException(500,"操作失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HttpResult deleteTask(Long id) {
        SystemCronTask task = getById(id);
        if (task == null) {
            return HttpResult.error(400, "任务不存在");
        }
        
        try {
            // 删除Job
            quartzUtil.deleteJob(scheduler, task);
            
            // 删除数据库记录
            boolean b = removeById(id);
            return b ? HttpResult.setResult(HttpResultEnum.DELETE_SUCCESS)
                    : HttpResult.setResult(HttpResultEnum.DELETE_ERROR);
        } catch (Exception e) {
            log.error("删除任务失败", e);
            return HttpResult.error(500, "删除失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HttpResult executeTask(Long id) {
        SystemCronTask task = getById(id);
        if (task == null) {
            return HttpResult.error(400, "任务不存在");
        }
        
        if (task.getStatus() == 1) {
            return HttpResult.error(400, "任务已暂停");
        }
        
        try {
            // 立即执行任务
            quartzUtil.executeJob(scheduler, task);
            return HttpResult.success("执行成功");
        } catch (Exception e) {
            log.error("执行任务失败", e);
            return HttpResult.error(500, "执行失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HttpResult pauseTask(Long id) {
        SystemCronTask task = getById(id);
        if (task == null) {
            return HttpResult.error(400, "任务不存在");
        }
        
        try {
            // 暂停Job
            quartzUtil.pauseJob(scheduler, task);
            
            // 更新状态
            task.setStatus(1); // 暂停
            updateById(task);
            
            return HttpResult.success("暂停成功");
        } catch (Exception e) {
            log.error("暂停任务失败", e);
            return HttpResult.error(500, "暂停失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HttpResult resumeTask(Long id) {
        SystemCronTask task = getById(id);
        if (task == null) {
            return HttpResult.error(400, "任务不存在");
        }
        
        try {
            // 恢复Job
            quartzUtil.resumeJob(scheduler, task);
            
            // 更新状态
            task.setStatus(0); // 正常
            updateById(task);
            
            return HttpResult.success("恢复成功");
        } catch (Exception e) {
            log.error("恢复任务失败", e);
            return HttpResult.error(500, "恢复失败：" + e.getMessage());
        }
    }

    @Override
    public void updateExecutionResult(Long id, String result, boolean success) {
        SystemCronTask task = getById(id);
        if (task == null) {
            return;
        }
        
        task.setLastExecutionTime(java.time.LocalDateTime.now());
        task.setLastExecutionResult(result);
        task.setExecutionCount(task.getExecutionCount() + 1);
        
        if (!success) {
            task.setFailureCount(task.getFailureCount() + 1);
        }
        
        updateById(task);
    }
}