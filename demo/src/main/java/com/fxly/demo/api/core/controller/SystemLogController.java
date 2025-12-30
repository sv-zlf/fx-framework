package com.fxly.demo.api.core.controller;

import com.fxly.demo.api.core.entity.SystemLog;
import com.fxly.demo.api.core.service.ISystemLogService;
import com.fxly.demo.system.global.HttpResultEnum;
import com.fxly.demo.system.global.HttpResult;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import io.swagger.v3.oas.annotations.Operation;

import java.util.List;

/**
* 系统日志表 控制器
* @author admin
*/
@RestController
@RequestMapping("/system/log")
public class SystemLogController{

    @Resource
    private ISystemLogService systemLogService;

    @Operation(summary = "列表查询")
    @GetMapping("/list")
    public HttpResult getLogList(SystemLog systemLog) {
        return HttpResult.success(systemLogService.getLogList(systemLog)) ;
    }
    @Operation(summary = "删除")
    @PostMapping("/delete")
    public HttpResult deleteSystemLog(@RequestParam("id") Long id) {
        boolean b = systemLogService.removeById(id);
        return b ? HttpResult.setResult(HttpResultEnum.DELETE_SUCCESS)
         : HttpResult.setResult(HttpResultEnum.DELETE_ERROR);
    }
}
