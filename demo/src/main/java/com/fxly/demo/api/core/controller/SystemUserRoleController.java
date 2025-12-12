package com.fxly.demo.api.core.controller;

import com.fxly.demo.api.core.entity.SystemRole;
import com.fxly.demo.api.core.service.ISystemUserRoleService;
import com.fxly.demo.system.global.HttpResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Tag(name = "用户角色配置管理")
@RestController
@RequestMapping("/systemUserRole")
@Slf4j
public class SystemUserRoleController {

    @Autowired
    private ISystemUserRoleService userRoleService;

    @Operation(summary = "根据用户编号获取已分配的角色列表")
    @GetMapping("/getRoleListByUserId.do")
    public HttpResult getRoleListByUserId(@RequestParam("userId") Long userId) {
        List<SystemRole> roleList = userRoleService.getRoleListByUserId(userId);
        return HttpResult.success(roleList);
    }

    @Operation(summary = "分配角色")
    @PostMapping("/grantRole.do")
    public HttpResult grantRole(@RequestParam("userId") Long userId, @RequestParam("roleIds") List<Long> roleIds) {
        //
        boolean b = userRoleService.grantRole(userId, roleIds);
        return b ? HttpResult.success()
                : HttpResult.error();
    }

    @Operation(summary = "批量授权")
    @PostMapping("/batchGrant.do")
    public HttpResult batchGrant(@RequestParam("userIds") Set<Long> userIds, @RequestParam("roleIds") Set<Long> roleIds) {
        //
        boolean b = userRoleService.batchGrant(userIds, roleIds);
        return b ? HttpResult.success()
                : HttpResult.error();
    }

}

