package com.fxly.demo.api.core.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fxly.demo.util.tree.Treeable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Accessors(chain = true)
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("system_menu")
public class SystemMenu implements Serializable,Treeable<SystemMenu> {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键Id")
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "父菜单ID")
    private Long parentId;

    @Schema(description = "菜单名称")
    private String menuName;

    @Schema(description = "前端路由路径")
    private String path;

    @Schema(description = "前端组件路径")
    private String component;

    @Schema(description = "权限标识")
    private String permission;

    @Schema(description = "菜单类型")
    private Integer type;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "svg图标")
    private String svgIcon;

    @Schema(description = "图标")
    private String icon;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "是否叶子节点（true=最后一级，无下级子菜单）")
    private Boolean isLeaf = true;

    @Schema(description = "是否外链（true=外部链接，false=内部路由）")
    private Boolean isExternal = false;

    @Schema(description = "是否全屏")
    private Boolean isFull;

    @Schema(description = "是否内嵌iframe")
    private Integer iframe;

    @Schema(description = "是否固定")
    private Integer affix = 0;

    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

    @Schema(description = "子菜单列表")
    @TableField(exist = false)
    private List<SystemMenu> children = new ArrayList<>();;

    @Schema(description = "是否菜单")
    @TableField(exist = false)
    private Boolean isMenu;

    @Schema(description = "是否选中")
    @TableField(exist = false)
    private Boolean isSelected;

}