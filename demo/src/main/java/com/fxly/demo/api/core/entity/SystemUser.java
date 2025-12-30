package com.fxly.demo.api.core.entity;

import com.baomidou.mybatisplus.annotation.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Accessors(chain = true)
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("system_user")
public class SystemUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键Id")
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "用户名")
    @NotBlank(message = "用户名不能为空", groups = {Create.class, Update.class})
    private String userName;

    @Schema(description = "密码")
    @NotBlank(message = "密码不能为空", groups = Create.class)
    private String password;

    @Schema(description = "昵称")
    private String nickName;

    @Schema(description = "邮箱")
    @Email(message = "邮箱格式不正确")
    private String email;

    @Schema(description = "手机")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "创建时间")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", locale="zh", timezone="GMT+8")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", locale="zh", timezone="GMT+8")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @Schema(description = "用户关联的角色列表")
    @TableField(exist = false)
    private List<SystemRole> roleList;

    @TableField(exist = false)
    private List<Long> roles;

    @Schema(description = "用户拥有的权限标识列表")
    @TableField(exist = false)
    private List<String> permissionList;

    public interface Create {}
    public interface Update {}
}
