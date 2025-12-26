package com.fxly.demo.api.core.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fxly.demo.system.global.PageHelper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalDate;

/**
* 系统日志表
* @author admin
*/

@Accessors(chain = true)
@Data
@EqualsAndHashCode(callSuper = false)
public class SystemLog extends PageHelper implements Serializable{

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "模块名称")
    private String moduleName;

    @Schema(description = "操作类型")
    private String operationType;

    @Schema(description = "操作描述")
    private String description;

    @Schema(description = "操作人ID")
    private Long userId;

    @Schema(description = "操作人名称")
    private String userName;

    @Schema(description = "IP地址")
    private String ipAddress;

    @Schema(description = "请求URL")
    private String requestUrl;

    @Schema(description = "请求方法")
    private String requestMethod;

    @Schema(description = "请求参数")
    private String requestParams;

    @Schema(description = "响应结果")
    private String responseData;

    @Schema(description = "状态(0-失败,1-成功)")
    private Integer status;

    @Schema(description = "错误信息")
    private String errorMsg;

    @Schema(description = "执行时间(ms)")
    private Integer executionTime;

    @Schema(description = "浏览器")
    private String browser;

    @Schema(description = "操作系统")
    private String os;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;


}