package com.fxly.demo.system.global;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 自定义异常基类
 * 用于系统级别的异常处理
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GlobalException extends RuntimeException {

	/** 错误码 */
	private Integer code;
	/** 错误信息 */
	private String message;

	public GlobalException() {
	}

	public GlobalException(String message) {
		super(message);
		this.message = message;
	}
	
	public GlobalException(Integer code, String message) {
		super(message);
		this.code = code;
		this.message = message;
	}
	
	public GlobalException(HttpResultEnum httpResultEnum) {
		this.code = httpResultEnum.getCode();
		this.message = httpResultEnum.getMessage();
	}

	public GlobalException(HttpResultEnum httpResultEnum, Throwable cause) {
		super(cause);
		this.code = httpResultEnum.getCode();
		this.message = httpResultEnum.getMessage();
	}

}