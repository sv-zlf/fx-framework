package com.fxly.demo.api.core.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fxly.demo.api.core.dto.SystemDictQueryDTO;
import com.fxly.demo.api.core.entity.SystemDictItem;

import java.util.List;

/**
 * @author zlf
 * @data 2025/12/4
 * @@description
 */
public interface ISystemDictItemService extends IService<SystemDictItem> {

    Page<SystemDictItem> getPageList(SystemDictQueryDTO dictQuery);

    List<SystemDictItem> getItemList(String dictTypeCode);
}
