package com.fxly.demo.api.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fxly.demo.api.core.entity.SystemRole;
import com.fxly.demo.api.core.mapper.SystemRoleMapper;
import com.fxly.demo.api.core.service.ISystemRoleService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zlf
 * @data 2025/11/28
 * @@description
 */

@Service
public class SystemRoleServiceImpl extends ServiceImpl<SystemRoleMapper, SystemRole> implements ISystemRoleService {

    @Override
    public Page<SystemRole> getPageList(String roleName, Integer pageIndex, Integer pageSize) {
        Page<SystemRole> page = new Page<>(pageIndex,pageSize);
        LambdaQueryWrapper<SystemRole> queryWrapper = new LambdaQueryWrapper();
        if (roleName != null && !roleName.equals("")){
            queryWrapper.like(SystemRole::getRoleName, roleName);
        }
        return this.page(page, queryWrapper);
    }

    @Override
    public List<SystemRole> getRoleList(String roleName) {
        LambdaQueryWrapper<SystemRole> queryWrapper = new LambdaQueryWrapper();
        if (roleName != null && !roleName.equals("")){
            queryWrapper.like(SystemRole::getRoleName, roleName);
        }
        return this.list(queryWrapper);
    }
}
