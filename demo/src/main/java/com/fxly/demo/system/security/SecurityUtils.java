package com.fxly.demo.system.security;


import com.fxly.demo.api.core.entity.SystemRole;
import com.fxly.demo.api.core.entity.SystemUser;
import com.fxly.demo.system.global.GlobalException;
import com.fxly.demo.system.global.HttpResultEnum;
import com.fxly.demo.system.redis.RedisCache;
import com.fxly.demo.system.redis.RedisConstants;
import com.fxly.demo.util.ApplicationContextUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class SecurityUtils {

    public static Long getUserId() {
        return getCurrentLoginUser().getId();
    }

    public static String getUserName() {
        return getCurrentLoginUser().getUserName();
    }

    public static List<SystemRole> getRoleList() {
        return getCurrentLoginUser().getRoleList();
    }

    public static Set<Long> getRoleIds() {
        List<SystemRole> roleList = getCurrentLoginUser().getRoleList();
        Set<Long> currentLoginUserRoleIds = Collections.emptySet();
        if (roleList != null && !roleList.isEmpty()) {
            currentLoginUserRoleIds = roleList.stream()
                    .map(SystemRole::getId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
        }
        return currentLoginUserRoleIds;
    }
    public static List<String> getPermissions() {
        return getCurrentLoginUser().getPermissionList();
    }

    // 获取当前登录用户
    public static SystemUser getCurrentLoginUser() {
        return getCurrentLoginUser(null);
    }

    // 获取当前登录用户
    public static SystemUser getCurrentLoginUser(String token) {
        CustomUserDetails userDetails = getUserDetails(token);
        return userDetails.getSystemUser();
    }

    // 获取当前登录用户
    public static CustomUserDetails getUserDetails(String token) {
        if(Objects.isNull(token)) {
            return getUserDetailsFromAuthentication();
        }
        return getUserDetailsFromRedis(token);
    }
    // 从Spring Security的认证上下文中获取用户详情
    private static CustomUserDetails getUserDetailsFromAuthentication() {
        Authentication authentication = getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new GlobalException(HttpResultEnum.UNAUTHORIZED.getMessage());
        }
        return (CustomUserDetails) authentication.getPrincipal();
    }

    private static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    // 从Redis缓存中获取用户详情
    private static CustomUserDetails getUserDetailsFromRedis(String token) {
        RedisCache redisCache = ApplicationContextUtils.getBean(RedisCache.class);
        String key = RedisConstants.TOKEN.concat(token);
        if(redisCache.hasKey(key)) {
            return redisCache.getObject(key, CustomUserDetails.class);
        }
        throw new GlobalException(HttpResultEnum.AUTH_ERROR.getMessage());
    }

    /**
     * 生成安全的密码哈希值
     * @param rawPassword 待加密的原始密码字符串
     * @return 加密后的密码哈希值
     */
    public static String encodePassword(String rawPassword) {
        return ApplicationContextUtils
                .getBean(PasswordEncoder.class)
                .encode(rawPassword);
    }

    /**
     * 比较原始密码与已加密密码是否匹配
     * @param rawPassword 用户输入的原始密码字符串
     * @param encodedPassword 数据库中存储的已加密密码哈希值
     * @return 如果匹配则返回true，否则返回false
     */
    public static boolean comparePassword(String rawPassword, String encodedPassword) {
        return ApplicationContextUtils
                .getBean(PasswordEncoder.class)
                .matches(rawPassword, encodedPassword);
    }

}
