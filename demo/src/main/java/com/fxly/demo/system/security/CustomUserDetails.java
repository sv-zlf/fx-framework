package com.fxly.demo.system.security;

import com.fxly.demo.api.core.entity.SystemUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 自定义用户详情（适配system_user表）
 */
public class CustomUserDetails implements UserDetails {

    private final SystemUser systemUser;

    public CustomUserDetails(SystemUser systemUser) {
        this.systemUser = systemUser;
    }

    /**
     * 加载用户权限（角色+菜单权限标识）
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        // 添加角色权限（ROLE_ADMIN/ROLE_USER）
        if (systemUser.getRoleList() != null){
            systemUser.getRoleList().forEach(role ->
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRoleCode()))
            );
        }
        if (systemUser.getPermissionList() != null){
            systemUser.getPermissionList().forEach(permission ->
                    authorities.add(new SimpleGrantedAuthority(permission))
            );
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return systemUser.getPassword();
    }

    @Override
    public String getUsername() {
        return systemUser.getUserName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return systemUser.getStatus() == 1;
    }

    // 获取原始用户信息
    public SystemUser getSystemUser() {
        return systemUser;
    }
}