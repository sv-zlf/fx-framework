package com.fxly.demo.system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * CORS跨域配置
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        
        // 允许携带凭证（Cookie、Authorization等）
        config.setAllowCredentials(true);
        
        // 允许所有来源（开发环境）
        // 生产环境建议改为具体域名，如：config.addAllowedOrigin("http://localhost:9000");
        config.addAllowedOriginPattern("*");
        
        // 允许所有请求头
        config.addAllowedHeader("*");
        
        // 允许所有HTTP方法（GET、POST、PUT、DELETE等）
        config.addAllowedMethod("*");
        
        // 预检请求缓存时间（秒）
        config.setMaxAge(3600L);
        
        // 注册CORS配置
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
}
