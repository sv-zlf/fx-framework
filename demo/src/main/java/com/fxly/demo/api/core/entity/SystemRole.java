package com.fxly.demo.api.core.entity;

import com.baomidou.mybatisplus.annotation.*;
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
@TableName("system_role")
public class SystemRole implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键Id")
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "角色名称")
    private String roleName;

    @Schema(description = "角色编码")
    private String roleCode;

    @Schema(description = "角色描述")
    private String description;

    @Schema(description = "角色状态")
    private Integer status;

    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

    // 非数据库字段：角色关联的菜单列表
    @TableField(exist = false)
    private List<SystemMenu> menus;
}