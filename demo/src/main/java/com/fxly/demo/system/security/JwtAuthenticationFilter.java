package com.fxly.demo.system.security;

import com.fxly.demo.system.global.GlobalException;
import com.fxly.demo.system.global.HttpResultEnum;
import com.fxly.demo.util.ResponseUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext; // 导入ApplicationContext
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Resource
    private JwtUtil jwtUtil;

    @Resource
    private JwtTokenManage tokenManage;

    // 注入ApplicationContext，而非直接注入UserDetailsService
    @Resource
    private ApplicationContext applicationContext;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = jwtUtil.extractToken(request);
            if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                String username = jwtUtil.extractUsername(token);
                if (username != null) {
                    // 方法内动态获取UserDetailsService，避免字段注入的循环依赖
                    UserDetailsService userDetailsService = applicationContext.getBean(UserDetailsService.class);
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    // 校验token有效性
                    if (!jwtUtil.validateToken(token, userDetails)) {
                        ResponseUtil.writeErrorResponse(response, HttpResultEnum.UNAUTHORIZED_TOKEN_INVALID);
                    }
                    // 验证token是否缓存存在
                    Object object = tokenManage.getToken(token);
                    if(Objects.isNull(object)) {
                        ResponseUtil.writeErrorResponse(response, HttpResultEnum.UNAUTHORIZED_TOKEN_NOT_EXIST);
                    }
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities()
                        );
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
            filterChain.doFilter(request, response);
        }
        catch (Exception e) {
            log.error("JWT认证过程异常", e);
            ResponseUtil.writeErrorResponse(response, HttpResultEnum.AUTH_ERROR);
        }

    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.equals("/system/login") || path.equals("/system/register");
    }
}