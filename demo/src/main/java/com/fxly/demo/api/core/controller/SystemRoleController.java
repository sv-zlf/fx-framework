package com.fxly.demo.api.core.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fxly.demo.api.core.dto.RoleQueryDTO;
import com.fxly.demo.api.core.entity.SystemRole;
import com.fxly.demo.api.core.entity.SystemRoleMenu;
import com.fxly.demo.api.core.entity.SystemUserRole;
import com.fxly.demo.api.core.service.ISystemRoleMenuService;
import com.fxly.demo.api.core.service.ISystemRoleService;
import com.fxly.demo.api.core.service.ISystemUserRoleService;
import com.fxly.demo.system.global.HttpResult;
import com.fxly.demo.system.global.HttpResultEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * @author zlf
 * @data 2025/11/28
 * @@description
 */

@Tag(name = "角色管理")
@RestController
@RequestMapping("/system/role")
@Slf4j
public class SystemRoleController {

    @Resource
    private ISystemUserRoleService userRoleService;
    @Resource
    private ISystemRoleService roleService;
    @Resource
    private ISystemRoleMenuService roleMenuService;

    @Operation(summary = "获取分页列表")
    @PostMapping("/getPageList")
    public HttpResult getRolePageList(@RequestBody RoleQueryDTO query) {
        return HttpResult.success(roleService.getPageList(query));
    }

    @Operation(summary = "获取列表")
    @GetMapping("/getRoleList")
    public HttpResult getRoleList(@RequestParam(value = "roleName", required = false) String roleName) {
        return HttpResult.success(roleService.getRoleList(roleName));
    }

    @Operation(summary = "新增角色")
    @PostMapping("/insert")
    public HttpResult insertRole(@RequestBody SystemRole role) {
        //
        LambdaQueryWrapper<SystemRole> queryWrapper = Wrappers.lambdaQuery(SystemRole.class)
                .eq(SystemRole::getRoleName, role.getRoleName());
        SystemRole dbRole = roleService.getOne(queryWrapper);
        if(dbRole != null) {
            return HttpResult.setResult(400,"角色已存在！");
        }
        boolean b = roleService.save(role);
        return b ? HttpResult.setResult(HttpResultEnum.INSERT_SUCCESS)
                : HttpResult.setResult(HttpResultEnum.INSERT_ERROR);
    }

    @Operation(summary = "修改角色")
    @PostMapping("/update")
    public HttpResult updateRole(@RequestBody  SystemRole role) {
        //
        LambdaQueryWrapper<SystemRole> queryWrapper = Wrappers.lambdaQuery(SystemRole.class)
                .eq(StringUtils.isNotBlank(role.getRoleName()), SystemRole::getRoleName, role.getRoleName());
        // 查询条件不为空
        if(queryWrapper.nonEmptyOfWhere()){
            SystemRole dbRole = roleService.getOne(queryWrapper);
            if(dbRole != null && !dbRole.getId().equals(role.getId())){
                return HttpResult.setResult(400,"角色已存在！");
            }
        }
        //
        boolean b = roleService.updateById(role);
        return b ? HttpResult.setResult(HttpResultEnum.UPDATE_SUCCESS)
                : HttpResult.setResult(HttpResultEnum.UPDATE_ERROR);
    }

    @Operation(summary = "删除角色")
    @PostMapping("/delete")
    public HttpResult deleteRole(@RequestParam("roleId") Long roleId) {
        // 首先，查看该角色是否绑定用户
        LambdaQueryWrapper queryUserRole = new LambdaQueryWrapper<SystemUserRole>()
                .eq(SystemUserRole::getRoleId, roleId);
        Long count = userRoleService.count(queryUserRole);
        if(count > 0) {
            return HttpResult.setResult(400,"操作失败，该角色已绑定用户！");
        }
        // 然后，删除该角色
        boolean b = roleService.removeById(roleId);
        if(b) {
            // 最后，移除该角色绑定的菜单
            roleMenuService.remove(
                    new LambdaQueryWrapper<SystemRoleMenu>()
                            .eq(SystemRoleMenu::getRoleId, roleId)
            );
        }
        return b ? HttpResult.setResult(HttpResultEnum.DELETE_SUCCESS)
                : HttpResult.setResult(HttpResultEnum.DELETE_ERROR);
    }

}

