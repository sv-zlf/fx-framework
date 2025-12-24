package com.fxly.demo.api.core.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalDate;

/**
* 系统用户会话表
* @author admin
*/

@Accessors(chain = true)
@Data
@EqualsAndHashCode(callSuper = false)
public class SystemUserSession implements Serializable{

    private static final long serialVersionUID = 1L;

    @Schema(description = "'主键ID'")
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "'会话编号'")
    private String sessionId;

    @Schema(description = "'登录名称'")
    private String loginName;

    @Schema(description = "'主机（客户端IP）'")
    private String host;

    @Schema(description = "'登录地点'")
    private String loginLocation;

    @Schema(description = "'浏览器'")
    private String browser;

    @Schema(description = "'操作系统'")
    private String os;

    @Schema(description = "'会话状态：0-离线/过期，1-在线，2-强制下线'")
    private Integer sessionStatus;

    @Schema(description = "'登录时间'")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", locale="zh", timezone="GMT+8")
    private LocalDateTime loginTime;

    @Schema(description = "'最后访问时间'")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", locale="zh", timezone="GMT+8")
    private LocalDateTime lastAccessTime;

    @Schema(description = "'会话过期时间'")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", locale="zh", timezone="GMT+8")
    private LocalDateTime expireTime;

    @Schema(description = "'创建时间'")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", locale="zh", timezone="GMT+8")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @Schema(description = "'更新时间'")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", locale="zh", timezone="GMT+8")
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;


}