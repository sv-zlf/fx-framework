package com.fxly.demo.system.global;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Builder;
import lombok.Data;

/**
 * 分页查询对象
 */
@Builder
@Data
public class PageResult {

    // 总记录数
    private Long totalNum;
    // 分页数据
    private Object pageList;

    public static PageResult getPageResult(IPage page) {
        return PageResult.builder()
                .totalNum(page.getTotal())
                .pageList(page.getRecords())
                .build();
    }

    public static PageResult getPageResult(Long totalNum, Object pageList) {
        return PageResult.builder()
                .totalNum(totalNum)
                .pageList(pageList)
                .build();
    }

}