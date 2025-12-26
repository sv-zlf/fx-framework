package com.fxly.demo.api.core.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fxly.demo.api.core.dto.UserQueryDTO;
import com.fxly.demo.api.core.entity.SystemUser;
import com.fxly.demo.system.global.HttpResult;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author zlf
 * @data 2025/11/28
 * @@description
 */
public interface ISystemUserService extends IService<SystemUser> {

    /*
     * 分页列表
     */
    Page<SystemUser> getPageList(UserQueryDTO userQueryDto);

    /**
     * 根据用户名查询用户（含角色权限）
     */
    SystemUser getUserWithRolesAndPermissions(String userName);

    /**
     * 用户注册
     */
    HttpResult register(SystemUser systemUser);

    /**
     * 根据用户名查询用户是否存在
     */
    boolean existsByUserName(String userName);

    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @param request 请求对象
     * @return HttpResult
     */
    HttpResult login(String username, String password, HttpServletRequest request);

    /**
     * 用户登出
     * @param token Token
     * @param request 请求对象
     * @return HttpResult
     */
    HttpResult logout(String token, HttpServletRequest request);

}