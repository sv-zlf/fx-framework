package com.fxly.demo.api.core.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fxly.demo.api.core.dto.UserQueryDTO;
import com.fxly.demo.api.core.entity.SystemUser;
import com.fxly.demo.system.global.HttpResult;

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
     * 根据用户名查询用户（含角色+权限）
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


}
