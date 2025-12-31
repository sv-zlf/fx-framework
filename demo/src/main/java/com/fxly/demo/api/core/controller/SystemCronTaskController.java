package com.fxly.demo.api.core.controller;

import com.fxly.demo.api.core.dto.TaskQueryDTO;
import com.fxly.demo.api.core.entity.SystemCronTask;
import com.fxly.demo.api.core.service.ISystemCronTaskService;
import com.fxly.demo.system.annotation.LogOperation;
import com.fxly.demo.system.constant.LogType;
import com.fxly.demo.system.global.HttpResult;
import com.fxly.demo.system.global.HttpResultEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 定时任务控制器
 * @author admin
 */
@Tag(name = "定时任务")
@RestController
@RequestMapping("/system/task")
public class SystemCronTaskController{

    @Resource
    private ISystemCronTaskService systemCronTaskService;

    @Operation(summary = "分页列表")
    @GetMapping("/list")
    public HttpResult getTaskList(TaskQueryDTO taskQuery) {
        return HttpResult.success(systemCronTaskService.getTaskList(taskQuery));
    }

    @Operation(summary = "保存或更新")
    @LogOperation(module = "定时任务", type = LogType.OTHER, description = "保存定时任务")
    @PostMapping("/saveOrUpdate")
    public HttpResult saveOrUpdate(@RequestBody SystemCronTask task) {
        return systemCronTaskService.saveOrUpdateTask(task);
    }

    @Operation(summary = "删除")
    @LogOperation(module = "定时任务", type = LogType.DELETE, description = "删除定时任务")
    @PostMapping("/delete")
    public HttpResult delete(@RequestParam("id") Long id) {
        return systemCronTaskService.deleteTask(id);
    }

    @Operation(summary = "执行")
    @LogOperation(module = "定时任务", type = LogType.OTHER, description = "执行定时任务")
    @PostMapping("/execute")
    public HttpResult execute(@RequestParam("id") Long id) {
        return systemCronTaskService.executeTask(id);
    }

    @Operation(summary = "暂停")
    @LogOperation(module = "定时任务", type = LogType.OTHER, description = "暂停定时任务")
    @PostMapping("/pause")
    public HttpResult pause(@RequestParam("id") Long id) {
        return systemCronTaskService.pauseTask(id);
    }

    @Operation(summary = "恢复")
    @LogOperation(module = "定时任务", type = LogType.OTHER, description = "恢复定时任务")
    @PostMapping("/resume")
    public HttpResult resume(@RequestParam("id") Long id) {
        return systemCronTaskService.resumeTask(id);
    }
}
