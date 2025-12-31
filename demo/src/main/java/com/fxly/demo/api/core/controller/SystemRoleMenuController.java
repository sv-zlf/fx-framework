package com.fxly.demo.api.core.controller;

import com.fxly.demo.api.core.entity.SystemMenu;
import com.fxly.demo.api.core.service.ISystemRoleMenuService;
import com.fxly.demo.system.global.HttpResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * @author zlf
 * @data 2025/12/1
 * @@description
 */

@Tag(name = "角色菜单配置管理")
@RestController
@RequestMapping("/system/roleMenu")
@Slf4j
@Validated
public class SystemRoleMenuController {

   @Resource
    private ISystemRoleMenuService roleMenuService;

    @Operation(summary = "授权")
    @GetMapping("/toBind")
    public HttpResult toBind(@NotNull(message = "角色ID不能为空") @RequestParam("roleId") Long roleId) {
        List<SystemMenu> menuTree = roleMenuService.toBind(roleId);
        return HttpResult.success(menuTree);
    }

    @Operation(summary ="绑定菜单")
    @PostMapping("/bindMenu")
    @Parameters({
            @Parameter(name = "roleId", required = true, in = ParameterIn.QUERY,
                    schema = @io.swagger.v3.oas.annotations.media.Schema(type = "integer", format = "int64")),
            @Parameter(name = "menuIds", required = true, in = ParameterIn.QUERY,
                    schema = @io.swagger.v3.oas.annotations.media.Schema(type = "array", implementation = Long.class))
    })
    public HttpResult bindMenu(@NotNull(message = "角色ID不能为空") @RequestParam("roleId") Long roleId, @NotEmpty(message = "菜单ID不能为空") @RequestParam("menuIds") Set<Long> menuIds) {
        //
        boolean b = roleMenuService.bindMenu(roleId, menuIds);
        return b ? HttpResult.success()
                : HttpResult.error();
    }
}
