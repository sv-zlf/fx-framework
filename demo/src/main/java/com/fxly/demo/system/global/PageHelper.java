package com.fxly.demo.system.global;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 分页查询对象
 */
@Data
public class PageHelper {

    @Schema(name = "分页查询参数")
    @TableField(exist = false)
    private Long pageIndex = 1L // 页码，默认第一页
            , pageSize = 10L; // 每页显示条数，默认每页显示10条数据

}