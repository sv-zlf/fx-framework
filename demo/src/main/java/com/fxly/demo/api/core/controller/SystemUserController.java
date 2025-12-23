package com.fxly.demo.api.core.controller;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fxly.demo.api.core.dto.UserQueryDTO;
import com.fxly.demo.api.core.entity.SystemUser;
import com.fxly.demo.api.core.service.ISystemUserRoleService;
import com.fxly.demo.api.core.service.ISystemUserService;
import com.fxly.demo.system.global.HttpResult;
import com.fxly.demo.system.global.HttpResultEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * @author zlf
 * @data 2025/11/30
 * @@description
 */

@Tag(name = "用户管理")
@RestController
@RequestMapping("/system/user")
@Slf4j
public class SystemUserController {

    @Resource
    private ISystemUserService userService;

    @Resource
    @Lazy
    private PasswordEncoder passwordEncoder;

    @Resource
    private ISystemUserRoleService userRoleService;

    @Operation(summary = "获取分页列表")
    @PreAuthorize("hasAuthority('user:list')")
    @PostMapping("/getPageList")
    public HttpResult getUserPageList(@RequestBody UserQueryDTO userQueryDto) {
        return HttpResult.success(userService.getPageList(userQueryDto));
    }

    @Operation(summary = "修改用户信息")
    @PostMapping("/saveOrUpdate")
    public HttpResult saveOrUpdateUser(@RequestBody SystemUser user) {

        if (ObjectUtil.isEmpty(user.getId())){
            HttpResult setResult = userValid(user);
            if (setResult != null) {
                return setResult;
            }
            // 设置默认密码
            user.setPassword(passwordEncoder.encode("123456"));
        }
        else {
            SystemUser dbUser = userService.getById(user.getId());
            // 更新用户信息密码不做变更
            user.setPassword(dbUser.getPassword());
        }
        boolean b = userService.saveOrUpdate(user);

        // 角色权限
        if (b){
            if (user.getRoles() != null){
                userRoleService.grantRole(user.getId(), user.getRoles());
            }
        }

        return b ? HttpResult.setResult(HttpResultEnum.UPDATE_SUCCESS)
                : HttpResult.setResult(HttpResultEnum.UPDATE_ERROR);
    }

    @Operation(summary = "删除用户")
    @PostMapping("/delete")
    public HttpResult deleteUser(@RequestParam("userId") Long userId) {
        boolean b = userService.removeById(userId);
        return b ? HttpResult.setResult(HttpResultEnum.DELETE_SUCCESS)
                : HttpResult.setResult(HttpResultEnum.DELETE_ERROR);
    }

    @Operation(summary = "批量删除用户")
    @PostMapping("/deleteBatch")
    public HttpResult batchDeleteUser(@RequestParam("userIds")  List<Long> userIds) {
        boolean b = userService.removeByIds(userIds);
        return b ? HttpResult.setResult(HttpResultEnum.DELETE_SUCCESS)
                : HttpResult.setResult(HttpResultEnum.DELETE_ERROR);
    }

    // 基本信息设置
    @Operation(summary = "基本信息设置")
    @PostMapping("/basicSetting")
    public HttpResult basicSetting(@RequestBody SystemUser user) {
        // 参数校验
        if(Objects.isNull(user) || Objects.isNull(user.getId())) {
            return HttpResult.setResult(400, "用户id不能为空");
        }
        //
        if (StringUtils.isBlank(user.getUserName())) {
            return HttpResult.setResult(400, "用户名不能为空");
        }
        //
        LambdaUpdateWrapper<SystemUser> updateWrapper = new LambdaUpdateWrapper<SystemUser>()
                .eq(SystemUser::getId, user.getId())
                .set(SystemUser::getUserName, user.getUserName())
                .set(SystemUser::getNickName, user.getNickName());
        boolean update = userService.update(updateWrapper);
        return update ? HttpResult.success("更新成功")
                : HttpResult.error("更新失败");
    }
    @Operation(summary = "安全设置")
    @PostMapping("/safeSetting")
    @Transactional
    public HttpResult resetPassword(@RequestBody SystemUser user) {
        // 参数校验
        if(Objects.isNull(user) || Objects.isNull(user.getId())) {
            return HttpResult.setResult(400, "用户id不能为空");
        }
        //
        if (StringUtils.isBlank(user.getPassword())) {
            return HttpResult.setResult(400, "密码不能为空");
        }
        //
        LambdaUpdateWrapper<SystemUser> updateWrapper = new LambdaUpdateWrapper<SystemUser>()
                .eq(SystemUser::getId, user.getId())
                .set(SystemUser::getPassword, passwordEncoder.encode(user.getPassword()))
                .set(SystemUser::getPhone, user.getPhone())
                .set(SystemUser::getEmail, user.getEmail());
        boolean update = userService.update(updateWrapper);
        return update ? HttpResult.success("更新成功")
                : HttpResult.error("更新失败");
    }

    private HttpResult userValid(SystemUser user) {
        // TODO demo 系统暂时无需这个校验，仅通过用户名称作为唯一标识
        // 根据用户名、手机号及邮箱查重，系统允许使用用户名、手机号及邮箱多种方式登录，所以同一个系统中用户名、手机号及邮箱不能重复
        LambdaQueryWrapper<SystemUser> queryWrapper = Wrappers.lambdaQuery(SystemUser.class)
                .eq(StringUtils.checkValNotNull(user.getUserName()), SystemUser::getUserName, user.getUserName())
                .or()
                .eq(StringUtils.checkValNotNull(user.getPhone()), SystemUser::getPhone, user.getPhone());
        //todo 邮箱校验先去掉，太多导入数据
//                .or()
//                .eq(StringUtils.checkValNotNull(user.getEmail()), SystemUser::getEmail, user.getEmail());
        // 查询条件不为空
        if(queryWrapper.nonEmptyOfWhere()) {
            List<SystemUser> userList = userService.list(queryWrapper);
            if(CollectionUtils.isNotEmpty(userList)) {
                for (SystemUser dbUser : userList) {
                    if(dbUser != null && !dbUser.getId().equals(user.getId())) {
                        if(dbUser.getUserName().equals(user.getUserName())) {
                            return HttpResult.error(400,"用户名已注册！");
                        } else if(StringUtils.isNotBlank(dbUser.getPhone()) && StringUtils.isNotBlank(user.getPhone()) && dbUser.getPhone().equals(user.getPhone())) {
                            return HttpResult.error(400,"手机号已注册！");
                        }
//                        else if(StringUtils.isNotBlank(dbUser.getEmail()) && StringUtils.isNotBlank(user.getEmail()) && dbUser.getEmail().equals(user.getEmail())) {
//                            return HttpResult.error(400,"邮箱已注册！");
//                        }
                    }
                }
            }
        }
        return null;
    }
}
