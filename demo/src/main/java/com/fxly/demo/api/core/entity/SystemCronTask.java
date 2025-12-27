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
 * 定时任务表
 * @author admin
 */

@Accessors(chain = true)
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("system_cron_task")
public class SystemCronTask implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "任务名称")
    private String taskName;

    @Schema(description = "任务分组")
    private String taskGroup;

    @Schema(description = "Cron表达式")
    private String cronExpression;

    @Schema(description = "调用目标字符串")
    private String invokeTarget;

    @Schema(description = "任务描述")
    private String description;

    @Schema(description = "任务状态：0-正常,1-暂停")
    private Integer status;

    @Schema(description = "并发执行：0-禁止,1-允许")
    private Integer concurrent;

    @Schema(description = "上次执行时间")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", locale="zh", timezone="GMT+8")
    private LocalDateTime lastExecutionTime;

    @Schema(description = "上次执行结果")
    private String lastExecutionResult;

    @Schema(description = "执行次数")
    private Integer executionCount;

    @Schema(description = "失败次数")
    private Integer failureCount;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", locale="zh", timezone="GMT+8")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", locale="zh", timezone="GMT+8")
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

}