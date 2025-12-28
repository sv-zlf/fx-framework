package com.fxly.demo.api.core.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文件信息表
 */

@Accessors(chain = true)
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("system_file")
public class SystemFile implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "原始文件名")
    private String originalName;

    @Schema(description = "存储路径（相对路径）")
    private String filePath;

    @Schema(description = "文件扩展名")
    private String fileExtension;

    @Schema(description = "文件大小（字节）")
    private Long fileSize;

    @Schema(description = "文件类型（image/document/video/audio等）")
    private String fileType;

    @Schema(description = "MIME类型")
    private String mimeType;

    @Schema(description = "MD5校验值")
    private String md5;

    @Schema(description = "上传用户ID")
    private Long userId;

    @Schema(description = "上传用户名")
    private String username;

    @Schema(description = "是否删除：0-否，1-是")
    @TableLogic
    private Integer deleted;

    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

}
