package com.fxly.demo.api.core.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fxly.demo.api.core.dto.MenuQueryDTO;
import com.fxly.demo.api.core.dto.UserQueryDTO;
import com.fxly.demo.api.core.entity.SystemLog;
import com.fxly.demo.api.core.entity.SystemMenu;
import com.fxly.demo.api.core.entity.SystemRole;
import com.fxly.demo.api.core.entity.SystemUser;
import com.fxly.demo.api.core.entity.SystemUserRole;
import com.fxly.demo.api.core.mapper.SystemUserRoleMapper;
import com.fxly.demo.api.core.mapper.SystemRoleMapper;
import com.fxly.demo.api.core.mapper.SystemUserMapper;
import com.fxly.demo.api.core.service.*;
import com.fxly.demo.system.constant.SystemConstants;
import com.fxly.demo.system.global.GlobalException;
import com.fxly.demo.system.global.HttpResult;
import com.fxly.demo.system.global.HttpResultEnum;
import com.fxly.demo.system.security.CustomUserDetails;
import com.fxly.demo.system.security.JwtTokenManage;
import com.fxly.demo.system.security.JwtUtil;
import com.fxly.demo.utils.session.IpParseUtil;
import eu.bitwalker.useragentutils.UserAgent;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

/**
 * @author zlf
 * @data 2025/11/28
 * @@description
 */

@Slf4j
@Service
public class SystemUserServiceImpl extends ServiceImpl<SystemUserMapper, SystemUser> implements ISystemUserService {

    @Resource
    private ISystemUserRoleService systemUserRoleService;

    @Resource
    private ISystemMenuService systemMenuService;

    @Resource
    private SystemRoleMapper systemRoleMapper;

    @Resource
    private SystemUserRoleMapper sysUserRoleMapper;

    @Resource
    @Lazy
    private PasswordEncoder passwordEncoder;

    @Resource
    @Lazy
    private AuthenticationManager authenticationManager;

    @Resource
    private JwtUtil jwtUtil;

    @Resource
    private JwtTokenManage tokenManage;

    @Resource
    private ISystemLogService systemLogService;

    @Resource
    private ISystemUserSessionService systemUserSessionService;

    @Resource
    private IpParseUtil ipParseUtil;

    private static final String TOKEN_PREFIX = "Bearer ";

    @Override
    public Page<SystemUser> getUserList(UserQueryDTO userQueryDto) {
        // 分页
        Page<SystemUser> page = new Page<>(userQueryDto.getPageIndex(),userQueryDto.getPageSize());
        // 查询条件
        String name = userQueryDto.getName();
        LambdaQueryWrapper<SystemUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.nested(ObjectUtil.isNotEmpty(name), q ->
                        q.like(SystemUser::getUserName, name).or().like(SystemUser::getNickName, name))
                .like(ObjectUtil.isNotEmpty(userQueryDto.getPhone()),SystemUser::getPhone, userQueryDto.getPhone())
                .eq(ObjectUtil.isNotEmpty(userQueryDto.getStatus()),SystemUser::getStatus, userQueryDto.getStatus());

        IPage<SystemUser> pageList = baseMapper.selectPage(page, queryWrapper);
        // 获取用户关联的角色列表
        for (SystemUser user : pageList.getRecords()){
            List<SystemRole> roleList = systemUserRoleService.getRoleListByUserId(user.getId());
            user.setRoleList(roleList);
            List<Long> roleIds = roleList.stream().map(SystemRole::getId).toList();
            user.setRoles( roleIds);
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
            List<Long> roleIds = roles.stream().map(SystemRole::getId).toList();
            systemUser.setRoles(roleIds);
            // 获取菜单权限列表
            List<SystemMenu> menus = systemMenuService.getMenuList(new HashSet<>(roleIds));
            if (ObjectUtil.isNotEmpty( menus)){
                systemUser.setPermissionList(menus.stream()
                        .filter(menu -> ObjectUtil.isNotEmpty(menu.getPermission()))
                        .map(SystemMenu::getPermission).toList());
            }
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

    @Override
    public HttpResult login(String username, String password, HttpServletRequest request) {
        // 非空校验
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            return HttpResult.error(400, "用户名或密码不能为空");
        }

        // 执行登录
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
            );
            if (authentication == null) {
                return HttpResult.error(HttpResultEnum.AUTH_ERROR.getCode(), "认证结果为空");
            }
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            
            // 生成Token并存储到Redis
            String token = jwtUtil.generateToken(userDetails.getUsername());
            boolean b = tokenManage.setToken(token, userDetails);
            if(b) {
                log.info("当前用户信息已存储到Redis：{}", userDetails);
            }
            
            // 登记会话信息
            systemUserSessionService.createSession(token, userDetails.getUsername(), request);

            // 记录登录日志
            saveLoginLog(request, userDetails, token, true, null);

            log.info("> Logged in as: " + userDetails.getUsername());
            return HttpResult.success(token);
        } catch (BadCredentialsException e) {
            // 记录登录失败日志
            saveLoginLog(request, username, null, false, e.getMessage());
            return HttpResult.setResult(HttpResultEnum.USERNAME_PASSWORD_ERROR);
        } catch (Exception e) {
            log.error("登录失败", e);
            // 记录登录失败日志
            saveLoginLog(request, username, null, false, e.getMessage());
            return HttpResult.setResult(HttpResultEnum.AUTH_ERROR);
        }
    }

    @Override
    public HttpResult logout(String token, HttpServletRequest request) {
        // 删除Token
        tokenManage.delToken(token);
        
        // 记录登出日志
        String userName = com.fxly.demo.system.security.SecurityUtils.getUserName();
        saveLogoutLog(request, userName);
        
        log.info("> Logout as: " + userName);
        return HttpResult.success();
    }

    /**
     * 保存登录日志
     */
    private void saveLoginLog(HttpServletRequest request, Object userOrUsername, String token, boolean success, String errorMsg) {
        try {
            // 获取IP地址
            String ipAddress = ipParseUtil.getRealIp(request);

            // 获取浏览器和操作系统信息
            String userAgentStr = request.getHeader("User-Agent");
            UserAgent userAgent = UserAgent.parseUserAgentString(userAgentStr);
            String browser = userAgent.getBrowser().getName() + " " + userAgent.getBrowserVersion();
            String os = userAgent.getOperatingSystem().getName();

            // 构建日志对象
            SystemLog systemLog = new SystemLog();
            systemLog.setModuleName("系统管理");
            systemLog.setOperationType("LOGIN");
            systemLog.setDescription("用户登录");
            systemLog.setIpAddress(ipAddress);
            systemLog.setRequestUrl(request.getRequestURL().toString());
            systemLog.setRequestMethod(request.getMethod());
            systemLog.setBrowser(browser);
            systemLog.setOs(os);
            systemLog.setStatus(success ? 1 : 0);
            systemLog.setExecutionTime(0);

            if (success) {
                // 登录成功
                CustomUserDetails userDetails = (CustomUserDetails) userOrUsername;
                SystemUser systemUser = userDetails.getSystemUser();
                systemLog.setUserId(systemUser.getId());
                systemLog.setUserName(systemUser.getUserName());

                // 保存请求参数（密码脱敏）
                String username = request.getParameter("username");
                Object[] params = new Object[]{username, "******"};
                systemLog.setRequestParams(JSONObject.toJSONString(params));

                // 保存响应结果（Token脱敏）
                String maskedToken = TOKEN_PREFIX + token.substring(0, Math.min(20, token.length())) + "...";
                systemLog.setResponseData(JSONObject.toJSONString(HttpResult.success(maskedToken)));

                log.info("登录日志已记录：用户名={}, IP={}", systemUser.getUserName(), ipAddress);
            } else {
                // 登录失败
                String username = (String) userOrUsername;
                systemLog.setUserName(username);
                systemLog.setErrorMsg(errorMsg);

                // 保存请求参数（密码脱敏）
                Object[] params = new Object[]{username != null ? username : "", "******"};
                systemLog.setRequestParams(JSONObject.toJSONString(params));

                log.warn("登录失败日志已记录：用户名={}, IP={}, 错误信息={}", username, ipAddress, errorMsg);
            }

            // 保存日志
            systemLogService.save(systemLog);

        } catch (Exception e) {
            log.error("保存登录日志失败", e);
        }
    }

    /**
     * 保存登出日志
     */
    private void saveLogoutLog(HttpServletRequest request, String userName) {
        try {
            if (userName == null) {
                return;
            }

            // 获取IP地址
            String ipAddress = ipParseUtil.getRealIp(request);

            // 获取浏览器和操作系统信息
            String userAgentStr = request.getHeader("User-Agent");
            UserAgent userAgent = UserAgent.parseUserAgentString(userAgentStr);
            String browser = userAgent.getBrowser().getName() + " " + userAgent.getBrowserVersion();
            String os = userAgent.getOperatingSystem().getName();

            // 构建日志对象
            SystemLog systemLog = new SystemLog();
            systemLog.setModuleName("系统管理");
            systemLog.setOperationType("LOGOUT");
            systemLog.setDescription("用户登出");
            systemLog.setUserName(userName);
            systemLog.setIpAddress(ipAddress);
            systemLog.setRequestUrl(request.getRequestURL().toString());
            systemLog.setRequestMethod(request.getMethod());
            systemLog.setBrowser(browser);
            systemLog.setOs(os);
            systemLog.setStatus(1);
            systemLog.setExecutionTime(0);

            // 保存日志
            systemLogService.save(systemLog);
            log.info("登出日志已记录：用户名={}, IP={}", userName, ipAddress);

        } catch (Exception e) {
            log.error("保存登出日志失败", e);
        }
    }
}

