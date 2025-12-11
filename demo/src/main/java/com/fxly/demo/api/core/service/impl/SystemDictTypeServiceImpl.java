package com.fxly.demo.api.core.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fxly.demo.api.core.dto.DictQueryDTO;
import com.fxly.demo.api.core.entity.SystemDictType;
import com.fxly.demo.api.core.mapper.SystemDictTypeMapper;
import com.fxly.demo.api.core.service.ISystemDictTypeService;
import org.springframework.stereotype.Service;

/**
 * @author zlf
 * @data 2025/12/5
 * @@description
 */

@Service
public class SystemDictTypeServiceImpl extends ServiceImpl<SystemDictTypeMapper,SystemDictType> implements ISystemDictTypeService {
    @Override
    public Page<SystemDictType> getPageList(DictQueryDTO dictQuery) {
        Page<SystemDictType> page = new Page<>(dictQuery.getPageIndex(), dictQuery.getPageSize());
        // 查询条件
        LambdaQueryWrapper<SystemDictType> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.like(ObjectUtil.isNotEmpty(dictQuery.getDictTypeName()), SystemDictType::getDictTypeName, dictQuery.getDictTypeName())       // 名称
                .like(ObjectUtil.isNotEmpty(dictQuery.getDictTypeCode()),SystemDictType::getDictTypeCode, dictQuery.getDictTypeCode())
                .eq(ObjectUtil.isNotEmpty(dictQuery.getStatus()), SystemDictType::getStatus, dictQuery.getStatus());  // 状态
        return baseMapper.selectPage(page, queryWrapper);
    }
}
