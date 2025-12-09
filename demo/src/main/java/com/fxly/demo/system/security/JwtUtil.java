package com.fxly.demo.system.security;


import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT工具类（适配Spring Boot 3.x/Jakarta EE）
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expire-time}")
    private long expireTime;

    @Value("${jwt.header}")
    private String jwtHeader;

    @Value("${jwt.prefix}")
    private String jwtPrefix;

    /**
     * 生成密钥（适配JJWT 0.12.x）
     */
    private SecretKey getSecretKey() {
        // 密钥长度需≥256位（32个字符），否则报错
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * 生成Token（基于用户名）
     */
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .claims(claims)          // 自定义载荷
                .subject(username)       // 主题（存储用户名）
                .issuedAt(new Date())    // 签发时间
                .expiration(new Date(System.currentTimeMillis() + expireTime)) // 过期时间
                .signWith(getSecretKey())// 签名
                .compact();
    }

    /**
     * 从Token中提取用户名
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * 提取Token中的载荷
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 解析Token所有载荷
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 验证Token是否过期
     */
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * 提取过期时间
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * 验证Token有效性（用户名+未过期）
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * 从Request中提取Token
     */
    public String extractToken(HttpServletRequest request) {
        String header = request.getHeader(jwtHeader);
        if (StringUtils.hasText(header) && header.startsWith(jwtPrefix + " ")) {
            return header.substring(jwtPrefix.length() + 1);
        }
        return null;
    }
}