package com.fxly.demo.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fxly.demo.system.global.HttpResult;
import com.fxly.demo.system.global.HttpResultEnum;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import java.io.IOException;

/**
 * 过滤器/拦截器返回JSON报错的工具类
 */
public class ResponseUtil {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * 写入JSON格式的报错响应
     */
    public static void writeErrorResponse(HttpServletResponse response, HttpResultEnum resultEnum) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(resultEnum.getCode());
        // 封装统一返回体
        HttpResult result = HttpResult.setResult(resultEnum);
        response.getWriter().write(OBJECT_MAPPER.writeValueAsString(result));
    }

    /**
     * 自定义报错信息
     */
    public static void writeErrorResponse(HttpServletResponse response, int code, String msg) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(code);
        HttpResult result = HttpResult.setResult(code, msg);
        response.getWriter().write(OBJECT_MAPPER.writeValueAsString(result));
    }
}