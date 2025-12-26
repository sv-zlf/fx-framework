package com.fxly.demo.api.core.controller;

import com.fxly.demo.api.core.entity.SystemUser;
import com.fxly.demo.api.core.service.ISystemUserService;
import com.fxly.demo.api.core.service.ISystemUserSessionService;
import com.fxly.demo.system.annotation.LogOperation;
import com.fxly.demo.system.constant.LogType;
import com.fxly.demo.system.global.HttpResult;
import com.fxly.demo.system.global.HttpResultEnum;
import com.fxly.demo.system.security.CustomUserDetails;
import com.fxly.demo.system.security.JwtTokenManage;
import com.fxly.demo.system.security.JwtUtil;
import com.fxly.demo.system.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author zlf
 * @data 2025/11/28
 * @@description
 */

@Tag(name = "系统管理")
@RestController
@RequestMapping("/system")
@Slf4j
public class SystemController {

    @Resource
    @Lazy
    private AuthenticationManager authenticationManager;

    @Resource
    private ISystemUserService systemUserService;

    @Resource
    private ISystemUserSessionService systemUserSessionService;
    @Resource
    private JwtUtil jwtUtil;

    @Resource
    private JwtTokenManage tokenManage;

    private static final String TOKEN_PREFIX = "Bearer ";

    @Operation(summary = "登录")
    @LogOperation(module = "示例模块", type = LogType.LOGIN)
    @PostMapping("/login")
    public HttpResult login(@RequestParam("username") String username, @RequestParam("password") String password, HttpServletRequest request) {

        // 非空校验
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            return HttpResult.error(400, "用户名或密码不能为空");
        }
        // 执行登录
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            if (authentication == null) {
                return HttpResult.error(HttpResultEnum.AUTH_ERROR.getCode(), "认证结果为空");
            }
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            // 把当前用户信息存储到 Redis
            String token = jwtUtil.generateToken(userDetails.getUsername());
            boolean b = tokenManage.setToken(token, userDetails);
            if(b) {
                log.info("当前用户信息已存储到Redis：{}", userDetails);
            }
            // 登记会话信息
            systemUserSessionService.createSession(token, userDetails.getUsername(), request);

            // 封装结果
//            Map<String, Object> resultMap = new HashMap<>();
//            resultMap.put("token", token);
//            resultMap.put("currentLoginUser", userDetails.getSystemUser());
            log.info("> Logged in as: " + userDetails.getUsername());
            return HttpResult.success(token);
        }catch (BadCredentialsException e) { // 用户名或密码错误
            return HttpResult.setResult(HttpResultEnum.USERNAME_PASSWORD_ERROR);
        } catch (Exception e) { // 其他认证异常
            log.error("登录失败", e);
            return HttpResult.setResult(HttpResultEnum.AUTH_ERROR);
        }
    }

    @Operation(summary = "注册")
    @PostMapping("/register")
    public HttpResult register(@RequestBody SystemUser systemUser) {
        return systemUserService.register(systemUser);
    }

    @Operation(summary = "注销")
    @PostMapping("/logout")
    public HttpResult logout(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(TOKEN_PREFIX.length()).trim();
        tokenManage.delToken(token);
        log.info("> Logout as: " + SecurityUtils.getUserName());
        SecurityContextHolder.clearContext();
        return HttpResult.success();
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/currentUser")
    public HttpResult getCurrentUser() {
        return HttpResult.success(SecurityUtils.getCurrentLoginUser());
    }
}
