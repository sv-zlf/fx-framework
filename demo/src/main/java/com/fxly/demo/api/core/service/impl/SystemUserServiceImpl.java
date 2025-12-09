package com.fxly.demo.api.core.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fxly.demo.api.core.dto.UserQueryDTO;
import com.fxly.demo.api.core.entity.SystemRole;
import com.fxly.demo.api.core.entity.SystemUser;
import com.fxly.demo.api.core.entity.SystemUserRole;
import com.fxly.demo.api.core.mapper.SystemUserRoleMapper;
import com.fxly.demo.api.core.mapper.SystemRoleMapper;
import com.fxly.demo.api.core.mapper.SystemUserMapper;
import com.fxly.demo.api.core.service.ISystemUserRoleService;
import com.fxly.demo.api.core.service.ISystemUserService;
import com.fxly.demo.system.constant.SystemConstants;
import com.fxly.demo.system.global.GlobalException;
import com.fxly.demo.system.global.HttpResult;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author zlf
 * @data 2025/11/28
 * @@description
 */

@Service
public class SystemUserServiceImpl extends ServiceImpl<SystemUserMapper, SystemUser> implements ISystemUserService {

    @Resource
    private ISystemUserRoleService systemUserRoleService;

    @Resource
    private SystemRoleMapper systemRoleMapper;

    @Resource
    private SystemUserRoleMapper sysUserRoleMapper;

    @Resource
    @Lazy
    private PasswordEncoder passwordEncoder;


    @Override
    public Page<SystemUser> getPageList(UserQueryDTO userQueryDto) {
        Page<SystemUser> page = new Page<>(userQueryDto.getPageIndex(),userQueryDto.getPageSize());
        LambdaQueryWrapper<SystemUser> queryWrapper = new LambdaQueryWrapper<>();
        if (ObjectUtil.isNotEmpty(userQueryDto.getUsername())){
            queryWrapper.like(SystemUser::getUserName, userQueryDto.getUsername());
        }
        if (ObjectUtil.isNotEmpty(userQueryDto.getRealname())){
            queryWrapper.like(SystemUser::getNickName, userQueryDto.getRealname());
        }
        IPage<SystemUser> pageList = baseMapper.selectPage(page, queryWrapper);
        // 获取用户关联的角色列表
        for (SystemUser user : pageList.getRecords()){
            user.setRoleList(systemUserRoleService.getRoleListByUserId(user.getId()));
        }
        return page;
    }

    @Override
    public SystemUser getUserWithRolesAndPermissions(String userName) {
        SystemUser systemUser = baseMapper.selectOne(new LambdaQueryWrapper<SystemUser>()
                .eq(SystemUser::getUserName, userName));
        if (ObjectUtil.isNotEmpty(systemUser)){
            List<SystemRole> roles = systemUserRoleService.getRoleListByUserId(systemUser.getId());
            systemUser.setRoleList(roles);
        }
        return systemUser;
    }

    @Override
    @Transactional(rollbackFor = Exception.class) // 事务控制，失败回滚
    public HttpResult register(SystemUser systemUser) {
        // 校验用户名是否已存在
        if (existsByUserName(systemUser.getUserName())) {
            throw new GlobalException(500, "用户名已存在");
        }

        // 查询默认角色（ROLE_USER），确保角色存在
        LambdaQueryWrapper<SystemRole> roleWrapper = new LambdaQueryWrapper<>();
        roleWrapper.eq(SystemRole::getRoleCode, SystemConstants.Common.DEFAULT_ROLE_CODE);
        SystemRole defaultRole = systemRoleMapper.selectOne(roleWrapper);
        if (defaultRole == null) {
            throw new GlobalException(500, "默认角色不存在");
        }

        // 密码加密（BCrypt）
        String encryptPassword = passwordEncoder.encode(systemUser.getPassword());

        // 构建SystemUser实体
        SystemUser user = new SystemUser();
        systemUser.setUserName(systemUser.getUserName());
        systemUser.setPassword(encryptPassword); // 存储加密后的密码
        systemUser.setNickName(systemUser.getNickName());
        systemUser.setEmail(systemUser.getEmail());
        systemUser.setPhone(systemUser.getPhone());
        systemUser.setStatus(1); // 启用状态

        // 插入用户数据
        baseMapper.insert(systemUser);

        // 关联用户-角色（sys_user_role）
        SystemUserRole systemUserRole = new SystemUserRole();
        systemUserRole.setUserId(systemUser.getId());
        systemUserRole.setRoleId(defaultRole.getId());
        sysUserRoleMapper.insert(systemUserRole);

        return HttpResult.success();
    }

    @Override
    public boolean existsByUserName(String userName) {
        LambdaQueryWrapper<SystemUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemUser::getUserName, userName);
        return baseMapper.exists(wrapper);
    }
}
