package com.fxly.demo.system.security;

import com.fxly.demo.api.core.entity.SystemUser;
import com.fxly.demo.api.core.service.ISystemUserService;
import jakarta.annotation.Resource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 自定义用户详情服务（适配新权限表）
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Resource
    private ISystemUserService systemUserService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 查询用户（含角色+权限）
        SystemUser systemUser = systemUserService.getUserWithRolesAndPermissions(username);
        if (systemUser == null) {
            throw new UsernameNotFoundException("用户不存在：" + username);
        }
        // 返回自定义用户详情
        return new CustomUserDetails(systemUser);
    }
}