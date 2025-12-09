package com.fxly.demo.system.global;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * 统一异常处理
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获 Error（致命错误，如 NoSuchMethodError）
     */
    @ExceptionHandler(Error.class)
    public HttpResult handleError(Error e, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        String errorMsg = e.getMessage() == null ? "服务器内部致命错误" : e.getMessage();
        log.error("请求地址：{}，致命错误：{}", requestUri, errorMsg, e); // 打印堆栈到日志框架
        return HttpResult.error()
                .code(500)
                .msg("服务器内部错误，请联系管理员"); // 隐藏敏感信息，返回友好提示
    }

    /**
     * 全局 Exception 处理（兜底）
     */
    @ExceptionHandler(Exception.class)
    public HttpResult exceptionHandler(Exception e, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        String errorMsg = e.getMessage() == null ? "请求处理失败" : e.getMessage();
        log.error("请求地址：{}，服务器内部错误：{}", requestUri, errorMsg, e); // 替换 e.printStackTrace()
        return HttpResult.error()
                .code(500)
                .msg(errorMsg);
    }

    /**
     * 自定义异常处理
     */
    @ExceptionHandler(GlobalException.class)
    public HttpResult handleCustomException(GlobalException e, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        log.warn("请求地址：{}，自定义异常：{}", requestUri, e.getMessage()); // 用 warn 级别
        return HttpResult.error()
                .code(e.getCode())
                .msg(e.getMessage());
    }

    /**
     * 未进入控制器的异常（400类异常）
     */
    @ExceptionHandler({
            HttpRequestMethodNotSupportedException.class,
            HttpMediaTypeNotSupportedException.class,
            HttpMediaTypeNotAcceptableException.class,
            MissingPathVariableException.class,
            MissingServletRequestParameterException.class,
            ServletRequestBindingException.class,
            ConversionNotSupportedException.class,
            TypeMismatchException.class,
            HttpMessageNotReadableException.class,
            HttpMessageNotWritableException.class,
            MethodArgumentNotValidException.class,
            MissingServletRequestPartException.class,
            BindException.class,
            NoHandlerFoundException.class,
            AsyncRequestTimeoutException.class
    })
    public HttpResult handleServletException(Exception e, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        String errorMsg = e.getMessage() == null ? "请求参数/格式错误" : e.getMessage();
        log.error("请求地址：{}，未进入控制器的异常：{}", requestUri, errorMsg, e);
        return HttpResult.error()
                .code(400)
                .msg(errorMsg);
    }

}