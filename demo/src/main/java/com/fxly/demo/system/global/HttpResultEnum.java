package com.fxly.demo.system.global;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public enum HttpResultEnum {

    //预设值
    OK(200, "请求成功")
    ,BAD_REQUEST(400, "请求失败")
    ,USERNAME_PASSWORD_ERROR(400, "用户名或密码错误！")
    ,UNAUTHORIZED(401, "账号未登录！")
    ,AUTH_ERROR(402, "凭据有误或已失效，请重新登录！")
    ,AUTH_ZERO(403, "暂无权限，请联系管理员！")
    ,FORBIDDEN(403, "请求的资源未授权")
    ,NOT_FOUND(404, "请求的资源不存在")
    ,METHOD_NOT_ALLOWED(405, "方法不被允许")
    ,INTERNAL_SERVER_ERROR(500, "服务器内部错误")

    ,UNAUTHORIZED_TOKEN_INVALID(401, "token无效或已过期")
    ,UNAUTHORIZED_TOKEN_NOT_EXIST(401, "token不存在或已登出")
    ,UNAUTHORIZED_USER_NOT_FOUND(401, "用户名不存在")
    ,UNAUTHORIZED_BEAN_ERROR(500, "获取用户认证服务失败")
    ,UNAUTHORIZED_UNKNOWN_ERROR(500, "认证过程异常")

    //业务逻辑
    ,REQUEST_PARAMETER_CANNOT_EMPTY(400, "请求参数不能为空")
    ,INVALID_PARAMETER(400, "无效参数")
    ,INSERT_SUCCESS(200, "新增成功")
    ,INSERT_ERROR(500, "新增失败")
    ,UPDATE_SUCCESS(200, "修改成功")
    ,UPDATE_ERROR(500, "修改失败")
    ,DELETE_SUCCESS(200, "删除成功")
    ,DELETE_ERROR(500, "删除失败")
    , FILE_TYPE_NOT_SUPPORT(400, "不支持的文件格式")
    ,UPLOAD_SUCCESS(200, "上传成功")
    ,UPLOAD_ERROR(500, "上传失败")
    ,DOWNLOAD_SUCCESS(200, "下载成功")
    ,DOWNLOAD_ERROR(500, "下载失败");

    // 响应状态码
    private Integer code;
    // 响应信息
    private String message;

    public void setCode(Integer code) {
        this.code = code;
    }

    public void setMsg(String message) {
        this.message = message;
    }

}