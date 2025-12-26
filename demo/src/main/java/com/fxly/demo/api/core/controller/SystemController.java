package com.fxly.demo.api.core.controller;

import com.fxly.demo.api.core.entity.SystemUser;
import com.fxly.demo.api.core.service.ISystemUserService;
import com.fxly.demo.system.global.HttpResult;
import com.fxly.demo.system.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

/**
 * @author zlf
 * @data 2025/11/28
 * @@description
 */

@Tag(name = "系统管理")
@RestController
@RequestMapping("/system")
public class SystemController {

    @Resource
    private ISystemUserService systemUserService;

    private static final String TOKEN_PREFIX = "Bearer ";

    @Operation(summary = "登录")
    @PostMapping("/login")
    public HttpResult login(@RequestParam("username") String username, 
                            @RequestParam("password") String password, 
                            HttpServletRequest request) {
        return systemUserService.login(username, password, request);
    }

    @Operation(summary = "注册")
    @PostMapping("/register")
    public HttpResult register(@RequestBody SystemUser systemUser) {
        return systemUserService.register(systemUser);
    }

    @Operation(summary = "注销")
    @PostMapping("/logout")
    public HttpResult logout(@RequestHeader("Authorization") String authHeader, 
                             HttpServletRequest request) {
        String token = authHeader.substring(TOKEN_PREFIX.length()).trim();
        return systemUserService.logout(token, request);
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/currentUser")
    public HttpResult getCurrentUser() {
        return HttpResult.success(SecurityUtils.getCurrentLoginUser());
    }
}