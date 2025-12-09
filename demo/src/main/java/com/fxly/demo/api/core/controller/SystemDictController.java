package com.fxly.demo.api.core.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fxly.demo.api.core.dto.SystemDictQueryDTO;
import com.fxly.demo.api.core.entity.SystemDictItem;
import com.fxly.demo.api.core.entity.SystemDictType;
import com.fxly.demo.api.core.service.ISystemDictItemService;
import com.fxly.demo.api.core.service.ISystemDictTypeService;
import com.fxly.demo.system.global.HttpResult;
import com.fxly.demo.system.global.HttpResultEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "数据字典类型")
@RestController
@RequestMapping("/system/dict/type")
public class SystemDictController {

    @Resource
    private ISystemDictTypeService dictService;

    @Resource
    private ISystemDictItemService itemService;


    @Operation(summary = "获取字典类型列表")
    @PostMapping("/getPageList")
    public HttpResult getDictTypePageList(@RequestBody SystemDictQueryDTO dictQuery) {
        return HttpResult.success(dictService.getPageList(dictQuery));
    }

    @Operation(summary = "新增")
    @PostMapping(value = "/insert")
    @Transactional(rollbackFor = Exception.class)
    public HttpResult insertDictType(@RequestBody SystemDictType dictType) {
        boolean b = dictService.save(dictType);
        if(b && CollectionUtils.isNotEmpty(dictType.getItemList())) {
            List<SystemDictItem> itemList = getDictItemList(dictType);
            b = itemService.saveBatch(itemList);
        }
        return b ? HttpResult.setResult(HttpResultEnum.INSERT_SUCCESS)
                : HttpResult.setResult(HttpResultEnum.INSERT_ERROR);
    }

    private static List<SystemDictItem> getDictItemList(SystemDictType dictType) {
        List<SystemDictItem> itemList = dictType.getItemList();
        itemList.forEach(item -> {
            item.setDictTypeCode(dictType.getDictTypeCode());
        });
        return itemList;
    }

    @Operation(summary = "修改")
    @PostMapping("/update")
    @Transactional(rollbackFor = Exception.class)
    public HttpResult updateDictType(@RequestBody SystemDictType dictType) {
        // 保存字典
        boolean b = dictService.updateById(dictType);
        if(b && CollectionUtils.isNotEmpty(dictType.getItemList())) {
            // 删除字典关联数据
            deleteDictItemByDictTypeCode(dictType.getDictTypeCode());
            // 保存新的数据
            List<SystemDictItem> itemList = getDictItemList(dictType);
            b = itemService.saveOrUpdateBatch(itemList);
        }
        return b ? HttpResult.setResult(HttpResultEnum.UPDATE_SUCCESS)
                : HttpResult.setResult(HttpResultEnum.UPDATE_ERROR);
    }

    private void deleteDictItemByDictTypeCode(String dictTypeCode) {
        LambdaQueryWrapper queryWrapper = new LambdaQueryWrapper<SystemDictItem>()
                .eq(SystemDictItem::getDictTypeCode, dictTypeCode);
        itemService.remove(queryWrapper);
    }

    @Operation(summary = "删除")
    @PostMapping("/delete")
    @Transactional(rollbackFor = Exception.class)
    public HttpResult deleteDictType(@RequestParam("id") Long id) {
        SystemDictType dictType = dictService.getById(id);
        //
        boolean b = dictService.removeById(id);
        if(b) {
            deleteDictItemByDictTypeCode(dictType.getDictTypeCode());
        }
        return b ? HttpResult.setResult(HttpResultEnum.DELETE_SUCCESS)
                : HttpResult.setResult(HttpResultEnum.DELETE_ERROR);
    }

}

