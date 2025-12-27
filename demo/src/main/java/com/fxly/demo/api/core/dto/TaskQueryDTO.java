package com.fxly.demo.api.core.dto;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.fxly.demo.system.global.PageHelper;
import lombok.Data;
import java.io.Serializable;

/**
 * 定时任务查询DTO
 * @author admin
 */
@Data
public class TaskQueryDTO extends PageHelper {

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 任务分组
     */
    private String taskGroup;

    /**
     * 任务状态：0-正常,1-暂停
     */
    private Integer status;
}