package com.fxly.demo.api.core.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fxly.demo.api.core.dto.TaskQueryDTO;
import com.fxly.demo.api.core.entity.SystemCronTask;
import com.fxly.demo.system.global.HttpResult;

/**
 * 定时任务Service
 * @author admin
 */
public interface ISystemCronTaskService extends IService<SystemCronTask> {

    /**
     * 分页列表
     */
    Page<SystemCronTask> getTaskList(TaskQueryDTO taskQueryDto);

    /**
     * 保存或更新任务
     */
    HttpResult saveOrUpdateTask(SystemCronTask task);

    /**
     * 删除任务
     */
    HttpResult deleteTask(Long id);

    /**
     * 执行任务
     */
    HttpResult executeTask(Long id);

    /**
     * 暂停任务
     */
    HttpResult pauseTask(Long id);

    /**
     * 恢复任务
     */
    HttpResult resumeTask(Long id);

    /**
     * 更新执行结果
     */
    void updateExecutionResult(Long id, String result, boolean success);
}

