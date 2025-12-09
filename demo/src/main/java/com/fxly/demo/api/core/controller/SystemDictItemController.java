package com.fxly.demo.api.core.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "数据字典项")
@RestController
@RequestMapping("/system/dict/item")
public class SystemDictItemController {

    @Resource
    private ISystemDictItemService itemService;


    @Operation(summary = "获取字典类型列表")
    @PostMapping("/getPageList")
    public HttpResult getDictItemPageList(@RequestBody SystemDictQueryDTO dictQuery) {
        return HttpResult.success(itemService.getPageList(dictQuery));
    }

    @Operation(summary = "获取字典项列表")
    @GetMapping("/getDictItemList")
    public HttpResult getDictItemList(@RequestParam("dictTypeCode") String dictTypeCode) {
        List<SystemDictItem> itemList = itemService.getItemList(dictTypeCode);
        return HttpResult.success(itemList);
    }

    @Operation(summary = "新增")
    @PostMapping(value = "/insert")
    public HttpResult insertDictItem(@RequestBody SystemDictItem dictItem) {
        boolean b = itemService.save(dictItem);
        return b ? HttpResult.setResult(HttpResultEnum.INSERT_SUCCESS)
                : HttpResult.setResult(HttpResultEnum.INSERT_ERROR);
    }


    @Operation(summary = "修改")
    @PostMapping("/update")
    public HttpResult updateDictItem(@RequestBody SystemDictItem dictItem) {
        // 保存字典
        boolean b = itemService.updateById(dictItem);
        return b ? HttpResult.setResult(HttpResultEnum.UPDATE_SUCCESS)
                : HttpResult.setResult(HttpResultEnum.UPDATE_ERROR);
    }

    @Operation(summary = "删除")
    @PostMapping("/delete")
    public HttpResult deleteDictItem(@RequestParam("id") Long id) {
        boolean b = itemService.removeById(id);
        return b ? HttpResult.setResult(HttpResultEnum.DELETE_SUCCESS)
                : HttpResult.setResult(HttpResultEnum.DELETE_ERROR);
    }

}

