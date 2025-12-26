package com.fxly.demo.api.core.controller;

import com.fxly.demo.api.core.dto.SessionQueryDTO;
import com.fxly.demo.api.core.entity.SystemUserSession;
import com.fxly.demo.api.core.service.ISystemUserSessionService;
import com.fxly.demo.system.annotation.LogOperation;
import com.fxly.demo.system.constant.LogType;
import com.fxly.demo.system.global.HttpResultEnum;
import com.fxly.demo.system.global.HttpResult;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import io.swagger.v3.oas.annotations.Operation;

import java.util.List;

/**
* 系统用户会话表控制器
* @author admin
*/

@Tag(name = "用户会话")
@RestController
@RequestMapping("/system/user/session")
public class SystemUserSessionController{

    @Resource
    private ISystemUserSessionService systemUserSessionService;

    @Operation(summary = "列表查询")
    @GetMapping("/getPageList")
    public HttpResult getUserSessionPageList(SessionQueryDTO query) {
        return HttpResult.success(systemUserSessionService.getPageList(query)) ;
    }


    @Operation(summary = "强制退出")
    @PostMapping("/forceLogout")
    @LogOperation(module = "会话管理", type = LogType.OTHER, description = "强制退出用户")
    @PreAuthorize("hasRole('ADMIN')")
    public HttpResult forceLogout(@RequestParam("sessionId") String sessionId) {
        return systemUserSessionService.forceLogout(sessionId);
    }
}