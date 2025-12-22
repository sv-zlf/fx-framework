package com.fxly.demo.api.generate.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fxly.demo.api.generate.entity.ColumnInfo;

/**
 * @author zlf
 * @data 2025/12/16
 * @@description
 */

public interface IColumnInfoService extends IService<ColumnInfo> {

    boolean removeByTableId(Long tableId);
}
