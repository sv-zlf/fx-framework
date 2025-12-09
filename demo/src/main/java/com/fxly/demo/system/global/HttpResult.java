package com.fxly.demo.system.global;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * 统一结果集
 */
@Data
@JsonInclude
public class HttpResult {

    //
    private Integer code;
    private String msg;
    private Object data;

    // 自主设值
    public static HttpResult setResult(Integer code, String msg, Object obj){
        HttpResult httpResult = new HttpResult();
        httpResult.setCode(code);
        httpResult.setMsg(msg);
        httpResult.setData(obj);
        return httpResult;
    }
    //
    public static HttpResult setResult(Integer code, String msg){
        return setResult(code, msg, null);
    }
    //
    public static HttpResult setResult(HttpResultEnum httpResultEnum){
        return setResult(httpResultEnum.getCode(), httpResultEnum.getMessage(),null);
    }

    /**
     * 返回成功
     */
    public static HttpResult success(String msg, Object data){
        return setResult(HttpResultEnum.OK.getCode(), msg, data);
    }
    //
    public static HttpResult success(Object data){
        return success(null, data);
    }
    //
    public static HttpResult success() {
        return success(null);
    }

    /**
     * 返回失败
     */
    //
    public static HttpResult error(Integer code, String msg) {
        return setResult(code, msg, null);
    }
    //
    public static HttpResult error(String msg) {
        return error(HttpResultEnum.INTERNAL_SERVER_ERROR.getCode(), msg);
    }
    //
    public static HttpResult error() {
        return error(null);
    }

    /**
     * 链式设值
     */
    public HttpResult code(Integer code){
        this.setCode(code);
        return this;
    }
    //
    public HttpResult msg(String msg){
        this.setMsg(msg);
        return this;
    }
    //
    public HttpResult data(Object data){
        this.setData(data);
        return this;
    }

}