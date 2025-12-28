package com.fxly.demo.system.global;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
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
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.stream.Collectors;

/**
 * 统一异常处理器
 * 优先级从高到低：具体异常 -> GlobalException -> Exception -> Error
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	/**
	 * 捕获 Error（致命错误，如 NoSuchMethodError）
	 * 优先级最低，作为最后兜底
	 */
	@ExceptionHandler(Error.class)
	public HttpResult handleError(Error e, HttpServletRequest request) {
		String requestUri = request.getRequestURI();
		String errorMsg = e.getMessage() == null ? "服务器内部致命错误" : e.getMessage();
		log.error("[致命错误] 请求地址：{}，错误类型：{}，错误信息：{}", 
				  requestUri, e.getClass().getSimpleName(), errorMsg, e);
		return HttpResult.error()
				.code(500)
				.msg("服务器内部错误，请联系管理员");
	}

	/**
	 * 全局 Exception 处理（兜底）
	 * 优先级高于 Error，作为普通异常的兜底处理
	 */
	@ExceptionHandler(Exception.class)
	public HttpResult exceptionHandler(Exception e, HttpServletRequest request) {
		String requestUri = request.getRequestURI();
		String errorMsg = e.getMessage() == null ? "请求处理失败" : e.getMessage();
		log.error("[系统异常] 请求地址：{}，异常类型：{}，错误信息：{}", 
				  requestUri, e.getClass().getSimpleName(), errorMsg, e);
		return HttpResult.error()
				.code(500)
				.msg("服务器内部错误，请稍后重试");
	}

	/**
	 * 自定义异常处理
	 * 优先级高于普通 Exception
	 */
	@ExceptionHandler(GlobalException.class)
	public HttpResult handleGlobalException(GlobalException e, HttpServletRequest request) {
		String requestUri = request.getRequestURI();
		if (e.getCode() != null && e.getCode() >= 500) {
			log.error("[自定义异常] 请求地址：{}，错误信息：{}", requestUri, e.getMessage(), e);
		} else {
			log.warn("[自定义异常] 请求地址：{}，错误信息：{}", requestUri, e.getMessage());
		}
		return HttpResult.error()
				.code(e.getCode() != null ? e.getCode() : 500)
				.msg(e.getMessage());
	}

	/**
	 * 参数校验异常
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public HttpResult handleMethodArgumentNotValid(MethodArgumentNotValidException e, HttpServletRequest request) {
		String requestUri = request.getRequestURI();
		String errorMsg = e.getBindingResult().getFieldErrors().stream()
				.map(FieldError::getDefaultMessage)
				.collect(Collectors.joining(", "));
		log.warn("[参数校验异常] 请求地址：{}，错误详情：{}", requestUri, errorMsg);
		return HttpResult.error()
				.code(400)
				.msg(errorMsg.isEmpty() ? "参数校验失败" : errorMsg);
	}

	/**
	 * 绑定异常
	 */
	@ExceptionHandler(BindException.class)
	public HttpResult handleBindException(BindException e, HttpServletRequest request) {
		String requestUri = request.getRequestURI();
		String errorMsg = e.getBindingResult().getFieldErrors().stream()
				.map(FieldError::getDefaultMessage)
				.collect(Collectors.joining(", "));
		log.warn("[绑定异常] 请求地址：{}，错误详情：{}", requestUri, errorMsg);
		return HttpResult.error()
				.code(400)
				.msg(errorMsg.isEmpty() ? "参数绑定失败" : errorMsg);
	}

	/**
	 * 请求参数类型不匹配
	 */
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public HttpResult handleTypeMismatch(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
		String requestUri = request.getRequestURI();
		String typeStr = e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "unknown";
		String errorMsg = String.format("参数 '%s' 的值 '%s' 应为类型 '%s'", e.getName(), e.getValue(), typeStr);
		log.warn("[参数类型异常] 请求地址：{}，{}", requestUri, errorMsg);
		return HttpResult.error()
				.code(400)
				.msg(errorMsg);
	}

	/**
	 * 缺少请求参数
	 */
	@ExceptionHandler(MissingServletRequestParameterException.class)
	public HttpResult handleMissingParameter(MissingServletRequestParameterException e, HttpServletRequest request) {
		String requestUri = request.getRequestURI();
		String errorMsg = String.format("缺少必需参数：%s，类型：%s", e.getParameterName(), e.getParameterType());
		log.warn("[缺少参数异常] 请求地址：{}，{}", requestUri, errorMsg);
		return HttpResult.error()
				.code(400)
				.msg(errorMsg);
	}

	/**
	 * 请求方法不支持
	 */
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public HttpResult handleMethodNotSupported(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
		String requestUri = request.getRequestURI();
		String supportedMethods = e.getSupportedMethods() != null ? String.join(", ", e.getSupportedMethods()) : "无";
		String errorMsg = String.format("请求方法 '%s' 不支持，支持的方法：%s", e.getMethod(), supportedMethods);
		log.warn("[方法不支持异常] 请求地址：{}，{}", requestUri, errorMsg);
		return HttpResult.error()
				.code(405)
				.msg(errorMsg);
	}

	/**
	 * 请求体不可读
	 */
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public HttpResult handleMessageNotReadable(HttpMessageNotReadableException e, HttpServletRequest request) {
		String requestUri = request.getRequestURI();
		log.warn("[请求体异常] 请求地址：{}，错误详情：请求体格式错误或解析失败", requestUri);
		return HttpResult.error()
				.code(400)
				.msg("请求体格式错误或解析失败");
	}

	/**
	 * 资源未找到
	 */
	@ExceptionHandler(NoHandlerFoundException.class)
	public HttpResult handleNoHandlerFound(NoHandlerFoundException e, HttpServletRequest request) {
		String requestUri = request.getRequestURI();
		String errorMsg = String.format("请求的资源不存在：{} {}", e.getHttpMethod(), requestUri);
		log.warn("[资源未找到异常] 请求地址：{}，错误详情：{}", requestUri, errorMsg);
		return HttpResult.error()
				.code(404)
				.msg("请求的资源不存在");
	}

	/**
	 * 权限不足异常
	 */
	@ExceptionHandler(AuthorizationDeniedException.class)
	public HttpResult handleAuthorizationDeniedException(AuthorizationDeniedException e, HttpServletRequest request) {
		String requestUri = request.getRequestURI();
		log.warn("[权限不足异常] 请求地址：{}，错误信息：{}", requestUri, e.getMessage());
		return HttpResult.error()
				.code(403)
				.msg("权限不足，无法访问该资源");
	}

	/**
	 * 其他Servlet异常（400类异常的兜底）
	 */
	@ExceptionHandler({
			HttpMediaTypeNotSupportedException.class,
			HttpMediaTypeNotAcceptableException.class,
			MissingPathVariableException.class,
			ServletRequestBindingException.class,
			ConversionNotSupportedException.class,
			TypeMismatchException.class,
			HttpMessageNotWritableException.class,
			MissingServletRequestPartException.class,
			AsyncRequestTimeoutException.class
	})
	public HttpResult handleServletException(Exception e, HttpServletRequest request) {
		String requestUri = request.getRequestURI();
		String errorMsg = e.getMessage() == null ? "请求参数/格式错误" : e.getMessage();
		log.warn("[Servlet异常] 请求地址：{}，异常类型：{}，错误信息：{}", 
				 requestUri, e.getClass().getSimpleName(), errorMsg);
		return HttpResult.error()
				.code(400)
				.msg(errorMsg);
	}

}