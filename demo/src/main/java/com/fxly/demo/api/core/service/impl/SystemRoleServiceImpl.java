package com.fxly.demo.api.core.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fxly.demo.api.core.dto.RoleQueryDTO;
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
    public Page<SystemRole> getPageList(RoleQueryDTO query) {
        // 分页
        Page<SystemRole> page = new Page<>(query.getPageIndex(),query.getPageSize());
        // 查询条件
        LambdaQueryWrapper<SystemRole> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.like(ObjectUtil.isNotEmpty(query.getRoleName()),SystemRole::getRoleName, query.getRoleName())
                .like(ObjectUtil.isNotEmpty(query.getRoleCode()),SystemRole::getRoleCode, query.getRoleCode())
                .eq(ObjectUtil.isNotEmpty(query.getStatus()),SystemRole::getStatus, query.getStatus());
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
