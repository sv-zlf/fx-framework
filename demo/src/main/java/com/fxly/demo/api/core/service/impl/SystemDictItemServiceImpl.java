package com.fxly.demo.api.core.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fxly.demo.api.core.dto.DictQueryDTO;
import com.fxly.demo.api.core.entity.SystemDictItem;
import com.fxly.demo.api.core.mapper.SystemDictItemMapper;
import com.fxly.demo.api.core.service.ISystemDictItemService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zlf
 * @data 2025/12/5
 * @@description
 */

@Service
public class SystemDictItemServiceImpl extends ServiceImpl<SystemDictItemMapper, SystemDictItem> implements ISystemDictItemService {
    @Override
    public Page<SystemDictItem> getPageList(DictQueryDTO dictQuery) {
        Page<SystemDictItem> page = new Page<>(dictQuery.getPageIndex(), dictQuery.getPageSize());
        LambdaQueryWrapper<SystemDictItem> queryWrapper = new LambdaQueryWrapper<SystemDictItem>()
                .eq(ObjectUtil.isNotEmpty(dictQuery.getDictTypeCode()),SystemDictItem::getDictTypeCode, dictQuery.getDictTypeCode())
                .like(ObjectUtil.isNotEmpty(dictQuery.getDictItemName()),SystemDictItem::getDictItemName, dictQuery.getDictItemName())
                .like(ObjectUtil.isNotEmpty(dictQuery.getDictItemCode()),SystemDictItem::getDictItemCode, dictQuery.getDictItemCode());
        return baseMapper.selectPage(page, queryWrapper);
    }

    @Override
    public List<SystemDictItem> getItemList(String dictTypeCode) {
        LambdaQueryWrapper<SystemDictItem> queryWrapper = new LambdaQueryWrapper<SystemDictItem>()
                .eq(SystemDictItem::getDictTypeCode, dictTypeCode);
        return list(queryWrapper);
    }
}
