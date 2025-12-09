package com.fxly.demo.system.global;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 自定义异常
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GlobalException extends RuntimeException {

    private Integer code;
    private String message;

    public GlobalException() {
    }

    public GlobalException(String message) {
        super(message);
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
