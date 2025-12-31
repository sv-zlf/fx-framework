package com.fxly.demo.api.core.controller;

import cn.hutool.core.util.ObjectUtil;
import com.fxly.demo.api.core.dto.MenuQueryDTO;
import com.fxly.demo.api.core.entity.SystemMenu;
import com.fxly.demo.api.core.service.ISystemMenuService;
import com.fxly.demo.system.annotation.LogOperation;
import com.fxly.demo.system.constant.LogType;
import com.fxly.demo.system.global.GlobalException;
import com.fxly.demo.system.global.HttpResult;
import com.fxly.demo.system.global.HttpResultEnum;
import com.fxly.demo.system.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * @author zlf
 * @data 2025/11/28
 * @@description
 */

@Tag(name = "菜单管理")
@RestController
@RequestMapping("/system/menu")
@Slf4j
@Validated
public class SystemMenuController {

   @Resource
    private ISystemMenuService menuService;

    @Operation(summary = "获取菜单列表")
    @GetMapping("/list")
    public HttpResult getMenuList(MenuQueryDTO menuQuery) {
        List<SystemMenu> menuList = menuService.getMenuList(menuQuery);
        return HttpResult.success(menuList);
    }

    @Operation(summary = "获取当前用户树形结构数据")
    @GetMapping("/getMenuTree")
    public HttpResult getMenuTree() {
        // 获取当前登录用户信息
        if(SecurityUtils.getRoleList().isEmpty()) {
            return HttpResult.setResult(HttpResultEnum.AUTH_ZERO);
        }
        MenuQueryDTO menuQuery = new MenuQueryDTO();
        menuQuery.setCurrentLoginUserRoleIds(SecurityUtils.getRoleIds());
        menuQuery.setStatus(1); // 获取已启用数据
        //
        List<SystemMenu> menuTree = menuService.getMenuTree(menuQuery);
        return HttpResult.success(menuTree);
    }

    @Operation(summary = "获取全部树形结构数据")
    @PostMapping("/getMenuTreeAll")
    public HttpResult getMenuTreeAll(@RequestBody MenuQueryDTO menuQuery) {
        List<SystemMenu> menuTree = menuService.getMenuTree(menuQuery);
        return HttpResult.success(menuTree);
    }

    @Operation(summary = "新增菜单")
    @LogOperation(module = "菜单管理", type = LogType.INSERT, description = "新增菜单")
    @PostMapping(value = "/insert")
    public HttpResult insertMenu(@Valid @RequestBody SystemMenu menu) {
        boolean b = menuService.insert(menu);
        return b ? HttpResult.setResult(HttpResultEnum.INSERT_SUCCESS)
                : HttpResult.setResult(HttpResultEnum.INSERT_ERROR);
    }

    @Operation(summary = "修改")
    @LogOperation(module = "菜单管理", type = LogType.UPDATE, description = "修改菜单")
    @PostMapping("/update")
    public HttpResult updateMenu(@Valid @RequestBody SystemMenu menu) {
        SystemMenu dbMenu = menuService.getById(menu.getId());
        if(Objects.isNull(dbMenu)) {
            throw new GlobalException(400, "无效的菜单编号");
        }
        // 下级菜单检查
        if(ObjectUtil.isNotEmpty(menu.getParentId())) {
            SystemMenu parentMenu = menuService.getById(menu.getParentId());
            if(Objects.isNull(parentMenu)) {
                throw new GlobalException(400, "无效的父级编号");
            }
        }
        //
        boolean b =  menuService.updateById(menu);
        return b ? HttpResult.setResult(HttpResultEnum.UPDATE_SUCCESS)
                : HttpResult.setResult(HttpResultEnum.UPDATE_ERROR);
    }

    @Operation(summary = "排序")
    @LogOperation(module = "菜单管理", type = LogType.UPDATE, description = "菜单排序")
    @PostMapping("/sortedMenu")
    public HttpResult sorted(@RequestBody List<SystemMenu> menuList) {
        //
        boolean b = menuService.updateBatchById(menuList);
        return b ? HttpResult.setResult(HttpResultEnum.UPDATE_SUCCESS)
                : HttpResult.setResult(HttpResultEnum.UPDATE_ERROR);
    }

    @Operation(summary = "删除")
    @LogOperation(module = "菜单管理", type = LogType.DELETE, description = "删除菜单")
    @PostMapping("/delete")
    public HttpResult deleteMenu(@NotNull(message = "菜单ID不能为空") @RequestParam("menuId") Long menuId) {
        //
        SystemMenu dbMenu = menuService.getById(menuId);
        if(Objects.isNull(dbMenu)) {
            throw new GlobalException(400, "无效的菜单编号");
        }
        // 删除该菜单
        boolean b = menuService.delete(menuId);
        return b ? HttpResult.setResult(HttpResultEnum.DELETE_SUCCESS)
                : HttpResult.setResult(HttpResultEnum.DELETE_ERROR);
    }

}
