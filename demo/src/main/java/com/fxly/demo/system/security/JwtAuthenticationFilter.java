package com.fxly.demo.system.security;

import com.fxly.demo.system.global.HttpResultEnum;
import com.fxly.demo.util.ResponseUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Resource
    private JwtUtil jwtUtil;

    @Resource
    private JwtTokenManage tokenManage;

    @Resource
    private UserDetailsService userDetailsService;

    private static final List<String> NO_FILTER_PATHS = Arrays.asList(SecurityConfig.noAuthUrl);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException,ServletException {
        try {
            String token = jwtUtil.extractToken(request);
            if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // 校验Token过期时间
                if (!jwtUtil.isTokenExpired(token)) {
                    ResponseUtil.writeErrorResponse(response, HttpResultEnum.UNAUTHORIZED_TOKEN_INVALID);
                    return;
                }
                // 提取用户名
                String username = jwtUtil.extractUsername(token);
                if (username == null) {
                    ResponseUtil.writeErrorResponse(response, HttpResultEnum.UNAUTHORIZED_USER_NOT_FOUND);
                    return;
                }
                // 加载用户信息
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (userDetails == null) {
                    ResponseUtil.writeErrorResponse(response, HttpResultEnum.UNAUTHORIZED_USER_NOT_FOUND);
                    return;
                }
                // 校验Token是否存在于Redis
                Object object = tokenManage.getToken(token);
                if (Objects.isNull(object)) {
                    ResponseUtil.writeErrorResponse(response, HttpResultEnum.UNAUTHORIZED_TOKEN_NOT_EXIST);
                    return;
                }
                // 校验Token与用户信息的匹配性（可选，增强安全性）
                if (!jwtUtil.validateToken(token, userDetails)) {
                    ResponseUtil.writeErrorResponse(response, HttpResultEnum.AUTH_ERROR);
                    return;
                }
                // 设置认证信息
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
            // 放行请求
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) { // Token过期
            log.error("JWT Token已过期", e);
            ResponseUtil.writeErrorResponse(response, HttpResultEnum.UNAUTHORIZED_TOKEN_INVALID);
        } catch (SignatureException e) { // Token签名错误
            log.error("JWT Token签名错误",e);
            ResponseUtil.writeErrorResponse(response, HttpResultEnum.UNAUTHORIZED_TOKEN_NOT_EXIST);
        } catch (Exception e) { // 其他异常
            log.error("JWT认证过程异常", e);
            ResponseUtil.writeErrorResponse(response, HttpResultEnum.AUTH_ERROR);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        return NO_FILTER_PATHS.contains(path);
    }
}