package com.fxly.demo.api.core.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fxly.demo.api.core.entity.SystemRole;
import com.fxly.demo.api.core.entity.SystemUserRole;
import com.fxly.demo.api.core.mapper.SystemUserRoleMapper;
import com.fxly.demo.api.core.service.ISystemUserRoleService;
import com.fxly.demo.system.constant.SystemConstants;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author zlf
 * @data 2025/11/29
 * @@description
 */

@Service
public class SystemUserRoleServiceImpl extends ServiceImpl<SystemUserRoleMapper, SystemUserRole> implements ISystemUserRoleService {

    @Override
    public List<SystemRole> getRoleListByUserId(Long userId) {
        return baseMapper.getRoleListByUserId(userId);
    }
    @Override
    public boolean grantRole(Long userId, Set<Long> roleIds) {
        // 首先，删除已分配的角色
        List<SystemRole> roleList = baseMapper.getRoleListByUserId(userId);
        if(roleList != null && !roleList.isEmpty()) {
            List<Long> roleIdList = roleList.stream()
                    .filter(role -> {
                        if (role == null) {
                            return false;
                        }
                        return !ObjectUtil.equal(role.getRoleCode(), SystemConstants.Common.DEFAULT_ROLE_CODE);
                    })
                    .map(SystemRole::getId) // 然后，提取所有的普通角色编号
                    .collect(Collectors.toList());
            if(!roleIdList.isEmpty()){
                baseMapper.deleteBatch(userId, roleIdList); // 最后，删除已分配的角色信息
            }
        }
        // 没有分配任何角色的情况
        if(roleIds != null && roleIds.isEmpty()) {
            return true;
        }
        // 然后，重新分配
        return baseMapper.grantRole(userId, roleIds);
    }

    @Override
    public boolean batchGrant(Set<Long> userIds, Set<Long> roleIds) {

        // 空值检查
        if (userIds == null || userIds.isEmpty() || roleIds == null || roleIds.isEmpty()) {
            return false;
        }

        // 预估列表大小以优化内存分配
        List<SystemUserRole> userRoleList = new ArrayList<>();
        // 获取已分配的角色列表
        LambdaQueryWrapper<SystemUserRole> queryWrapper = Wrappers.<SystemUserRole>lambdaQuery()
                .in(SystemUserRole::getUserId, userIds);
        List<SystemUserRole> dbUserRoleList = baseMapper.selectList(queryWrapper);
        if (CollectionUtils.isNotEmpty(dbUserRoleList)) {
            Map<Long, List<SystemUserRole>> userRoleListMap = dbUserRoleList.stream().collect(Collectors.groupingBy(SystemUserRole::getUserId));
            // 遍历创建对象
            for (Long userId : userIds) {
                if (userRoleListMap.containsKey(userId)) {
                    Set<Long> dbRoleIds = userRoleListMap.get(userId).stream().map(SystemUserRole::getRoleId).collect(Collectors.toSet());
                    for (Long roleId : roleIds) {
                        if (!dbRoleIds.contains(roleId)) {
                            userRoleList.add(new SystemUserRole().setUserId(userId).setRoleId(roleId));
                        }
                    }
                }
                else {
                    for (Long roleId : roleIds) {
                        userRoleList.add(new SystemUserRole().setUserId(userId).setRoleId(roleId));
                    }
                }
            }
        }
        else {
            // 遍历创建对象
            for (Long userId : userIds) {
                for (Long roleId : roleIds) {
                    userRoleList.add(new SystemUserRole().setUserId(userId).setRoleId(roleId));
                }
            }
        }

        // 批量保存
        if (CollectionUtils.isNotEmpty(userRoleList)) {
            return saveBatch(userRoleList);
        }
        return true;
    }

}
