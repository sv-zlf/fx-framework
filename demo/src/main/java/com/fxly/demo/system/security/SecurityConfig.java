package com.fxly.demo.system.security;

import com.alibaba.fastjson.JSONObject;
import com.fxly.demo.system.global.HttpResult;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // 开启方法级权限（@PreAuthorize）
public class SecurityConfig {

    @Resource
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Resource
    private UserDetailsService userDetailsService;


    public static final String[] noAuthUrl = {"/", "/index", "/doc.html","/webjars/**", "/v3/api-docs/**", "/knife4j/**",
            "/swagger-resources/**", "/swagger-ui/**", "/system/login", "/system/register","/druid/**"
    };

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        // 注册用户名密码认证的Provider
        authBuilder.authenticationProvider(authenticationProvider());
        return authBuilder.build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 配置CSP（Content Security Policy）
                .headers(headers -> headers
                        .frameOptions(frame -> frame.disable()) // 关闭X-Frame-Options（因为用CSP替代）
                        .contentSecurityPolicy(csp -> csp
                                        // 配置允许嵌入的域名：self（同域名） + 指定域名（如localhost:3000）
                                        .policyDirectives("frame-ancestors http://localhost:5173 'self';")
                                // 若需允许所有域名（极不推荐，安全风险）：frame-ancestors *;
                        )
                )
                .httpBasic(c -> c.disable())  // 禁用默认表单登录
                .formLogin(c -> c.disable())   // 禁用默认注销功能
                .logout(c -> c.disable())
                .csrf(csrf -> csrf.disable()) // 禁用CSRF
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // 禁用Session
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(noAuthUrl).permitAll() // 放行接口
                        .anyRequest().authenticated() // 其他接口需认证
                )
                .authenticationProvider(authenticationProvider()) // 添加认证提供者
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // 异常处理
        http.exceptionHandling(ex -> {
            // 配置未认证（401）处理
            ex.authenticationEntryPoint((request, response, authException) ->
                    extracted(request, response, HttpStatus.UNAUTHORIZED, "未认证成功！！！")
            );
            // 配置权限不足（403）处理
            ex.accessDeniedHandler((request, response, accessDeniedException) ->
                    extracted(request, response, HttpStatus.FORBIDDEN, "资源未授权！！！")
            );
        });
        return http.build();
    }

    private static void extracted(HttpServletRequest request, HttpServletResponse response, HttpStatus status, String other) throws IOException {
        response.setStatus(status.value());
        response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        // Ensuring the errorMessage is properly retrieved and handled
        String errorMessage = Optional.ofNullable(request.getAttribute("errorMessage"))
                .map(Object::toString)
                .orElse(other);
        HttpResult httpResult = HttpResult.setResult(status.value(), errorMessage);
        String jsonResult = JSONObject.toJSONString(httpResult);
        response.getWriter().write(jsonResult);
    }
}
