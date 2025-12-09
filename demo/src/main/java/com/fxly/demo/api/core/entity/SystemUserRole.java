package com.fxly.demo.api.core.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author zlf
 * @data 2025/11/28
 * @@description
 */
@Accessors(chain = true)
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("system_user_role")
public class SystemUserRole implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键Id")
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "用户编号")
    private Long userId;

    @Schema(description = "角色编号")
    private Long roleId;


}