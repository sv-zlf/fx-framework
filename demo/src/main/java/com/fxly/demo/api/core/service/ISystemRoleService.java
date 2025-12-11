package com.fxly.demo.api.core.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fxly.demo.api.core.dto.RoleQueryDTO;
import com.fxly.demo.api.core.entity.SystemRole;

import java.util.List;

/**
 * @author zlf
 * @data 2025/11/28
 * @@description
 */
public interface ISystemRoleService extends IService<SystemRole> {

    Page<SystemRole> getPageList(RoleQueryDTO query);

    List<SystemRole> getRoleList(String roleName);

}

