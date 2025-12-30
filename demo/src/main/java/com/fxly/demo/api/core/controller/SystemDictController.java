package com.fxly.demo.api.core.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.fxly.demo.api.core.dto.DictQueryDTO;
import com.fxly.demo.api.core.entity.SystemDictItem;
import com.fxly.demo.api.core.entity.SystemDictType;
import com.fxly.demo.api.core.service.ISystemDictItemService;
import com.fxly.demo.api.core.service.ISystemDictTypeService;
import com.fxly.demo.system.annotation.LogOperation;
import com.fxly.demo.system.constant.LogType;
import com.fxly.demo.system.global.HttpResult;
import com.fxly.demo.system.global.HttpResultEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "数据字典类型")
@RestController
@RequestMapping("/system/dict/type")
@Validated
public class SystemDictController {

   @Resource
    private ISystemDictTypeService dictService;

    @Resource
    private ISystemDictItemService itemService;



    @Operation(summary = "获取字典类型列表")
    @PostMapping("/list")
    public HttpResult getDictTypeList(@RequestBody DictQueryDTO dictQuery) {
        return HttpResult.success(dictService.getDictTypeList(dictQuery));
    }

    @Operation(summary = "新增")
    @LogOperation(module = "字典管理", type = LogType.INSERT, description = "新增字典类型")
    @PostMapping(value = "/insert")
    @Transactional(rollbackFor = Exception.class)
    public HttpResult insertDictType(@Valid @RequestBody SystemDictType dictType) {
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
    @LogOperation(module = "字典管理", type = LogType.UPDATE, description = "修改字典类型")
    @PostMapping("/update")
    @Transactional(rollbackFor = Exception.class)
    public HttpResult updateDictType(@Valid @RequestBody SystemDictType dictType) {
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
    @LogOperation(module = "字典管理", type = LogType.DELETE, description = "删除字典类型")
    @PostMapping("/delete")
    @Transactional(rollbackFor = Exception.class)
    public HttpResult deleteDictType(@NotNull(message = "字典ID不能为空") @RequestParam("id") Long id) {
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
