package com.fxly.demo.api.generate.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author zlf
 * @data 2025/12/16
 * @@description
 */

@Accessors(chain = true)
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("table_info")
public class TableInfo implements Serializable {


    private static final long serialVersionUID = 1L;

    @Schema(description = "主键Id")
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "表名")
    private String tableName;

    @Schema(description = "类名")
    private String className;

    @Schema(description = "表注释")
    private String tableComment;

    @Schema(description = "作者")
    private String author;

    @Schema(description = "模块名")
    private String moudleName;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "生成方式,默认0-压缩包,1-本地工程模块")
    private Integer generateType;

    @Schema(description = "字段列表")
    @TableField(exist = false)
    private List<ColumnInfo> columnList;

    @Schema(description = "基础包名")
    @TableField(exist = false)
    private String basePackage;


    /** 主键字段 */
//    private ColumnInfo primaryKey;
}
