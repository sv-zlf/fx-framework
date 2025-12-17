package com.fxly.demo.api.generate.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author zlf
 * @data 2025/12/16
 * @@description
 */

@Accessors(chain = true)
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("column_info")
public class ColumnInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键Id")
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "关联数据库表主键Id")
    private Long tableId;

    @Schema(description = "数据库字段名")
    private String columnName;

    @Schema(description = "数据库字段属性名")
    private String propertyName;

    @Schema(description = "数据库字段类型")
    private String columnType;

    @Schema(description = "Java类型")
    private String javaType;

    @Schema(description = "字段注释")
    private String comment;

    @Schema(description = "是否主键")
    private boolean primaryKey;

    @Schema(description = "是否非空")
    private boolean notNull;

    @Schema(description = "是否自增")
    private boolean autoIncrement;
}
