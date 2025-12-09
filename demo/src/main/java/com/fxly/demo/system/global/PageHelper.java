package com.fxly.demo.system.global;

import lombok.Data;

/**
 * 分页查询对象
 */
@Data
public class PageHelper {

    // 分页查询参数
    private Long pageIndex = 1L // 页码，默认第一页
            , pageSize = 10L; // 每页显示条数，默认每页显示10条数据

}