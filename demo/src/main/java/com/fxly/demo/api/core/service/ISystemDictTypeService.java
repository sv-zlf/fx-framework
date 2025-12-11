package com.fxly.demo.api.core.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fxly.demo.api.core.dto.DictQueryDTO;
import com.fxly.demo.api.core.entity.SystemDictType;

/**
 * @author zlf
 * @data 2025/12/4
 * @@description
 */
public interface ISystemDictTypeService extends IService<SystemDictType> {

    Page<SystemDictType> getPageList(DictQueryDTO dictQuery);

}
