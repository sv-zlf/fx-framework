package com.fxly.demo.api.generate.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fxly.demo.api.generate.entity.ColumnInfo;
import com.fxly.demo.api.generate.entity.TableInfo;
import com.fxly.demo.api.generate.service.IColumnInfoService;
import com.fxly.demo.api.generate.service.ITableInfoService;
import com.fxly.demo.system.annotation.LogOperation;
import com.fxly.demo.system.constant.LogType;
import com.fxly.demo.system.global.HttpResult;
import com.fxly.demo.system.global.HttpResultEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.zip.ZipOutputStream;

/**
 * @author zlf
 * @data 2025/12/16
 * @@description
 */

@Tag(name = "代码生成")
@RestController
@RequestMapping("/tool/gen")
@Slf4j
@Validated
public class GenerateController {

   @Resource
    private ITableInfoService tableInfoService;

    @Resource
    private IColumnInfoService columnInfoService;

    @Operation(summary = "选择数据库表生成信息")
    @LogOperation(module = "代码生成", type = LogType.INSERT, description = "导入表结构")
    @PostMapping("/genTable")
    public HttpResult genTable(@NotBlank(message = "SQL不能为空") @RequestParam("sql") String sql,
                               @NotBlank(message = "模块名称不能为空") @RequestParam("moduleName") String moduleName) {
        return HttpResult.success(tableInfoService.generateTable(sql, moduleName));
    }

    @Operation(summary = "选择表ID生成代码，本地项目")
    @LogOperation(module = "代码生成", type = LogType.OTHER, description = "生成代码到本地", saveRequestData = false)
    @PostMapping("/genCode")
    public HttpResult genCode(@NotNull(message = "表ID不能为空") @RequestParam("tableId") Long tableId ) {
        tableInfoService.generateCode(tableId);
        return HttpResult.success();
    }

    @Operation(summary = "选择表ID生成代码，下载包")
    @LogOperation(module = "代码生成", type = LogType.OTHER, description = "下载代码压缩包", saveRequestData = false)
    @GetMapping("/genCodeZip")
    public void genCodeZip(@NotNull(message = "表ID不能为空") @RequestParam("tableId") Long tableId,HttpServletResponse response) throws IOException {
        response.setContentType("application/zip"); // ZIP文件格式
        response.setCharacterEncoding("UTF-8");
        // 文件名编码
        String fileName = URLEncoder.encode("code-gen.zip", "UTF-8").replace("+", "%20");
        // 提示写入浏览器下载文件
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        try (ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream())) {
            // 调用Service层，生成代码到ZIP流
            tableInfoService.generateCode(tableId, zipOut);
            zipOut.finish();
        } catch (Exception e) {
            log.error("生成代码失败：", e);
        }
    }

    @Operation(summary = "分页列表")
    @GetMapping("/getPageList")
    public HttpResult getTableInfoPageList(@RequestParam(value = "pageIndex",defaultValue = "1") Integer pageIndex,
                                   @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize,
                                   @RequestParam(value = "tableName", required = false) String tableName) {
        return HttpResult.success(tableInfoService.getPageList(pageIndex, pageSize, tableName));
    }

    @Operation(summary = "更新")
    @LogOperation(module = "代码生成", type = LogType.UPDATE, description = "更新代码生成配置")
    @PostMapping("/update")
    @Transactional
    public HttpResult updateTableInfo(@NotNull(message = "表信息不能为空") @RequestBody TableInfo tableInfo) {
        Boolean b = tableInfoService.updateById(tableInfo);
        if (b){
            for (ColumnInfo columnInfo : tableInfo.getColumnList()){
                columnInfoService.updateById(columnInfo);
            }
        }
        return b ? HttpResult.setResult(HttpResultEnum.UPDATE_SUCCESS)
                : HttpResult.setResult(HttpResultEnum.UPDATE_ERROR);
    }

    @Operation(summary = "删除")
    @LogOperation(module = "代码生成", type = LogType.DELETE, description = "删除代码生成配置")
    @PostMapping("/delete")
    public HttpResult deleteTableInfo(@NotNull(message = "ID不能为空") @RequestParam("id") Long id) {
        Boolean b = tableInfoService.removeById(id);
        if (b){
            columnInfoService.removeByTableId(id);
        }
        return b ? HttpResult.setResult(HttpResultEnum.DELETE_SUCCESS)
                : HttpResult.setResult(HttpResultEnum.DELETE_ERROR);
    }

    @Operation(summary = "查询表字段")
    @GetMapping("/getColumnList")
    public HttpResult getColumnList(@NotNull(message = "表ID不能为空") @RequestParam("tableId") Long tableId) {
        return HttpResult.success(columnInfoService.list(
                new LambdaQueryWrapper<ColumnInfo>().eq(ColumnInfo::getTableId, tableId)));
    }

    @Operation(summary = "批量更新表字段")
    @LogOperation(module = "代码生成", type = LogType.UPDATE, description = "批量更新字段配置")
    @PostMapping("/batchUpdateColumn")
    public HttpResult batchUpdateColumn(@NotNull(message = "列信息不能为空") @RequestBody List<ColumnInfo> columnInfoList) {
        return columnInfoService.updateBatchById(columnInfoList) ? HttpResult.setResult(HttpResultEnum.UPDATE_SUCCESS)
                : HttpResult.setResult(HttpResultEnum.UPDATE_ERROR);
    }

    @Operation(summary = "删除表字段")
    @LogOperation(module = "代码生成", type = LogType.DELETE, description = "删除字段配置")
    @PostMapping("/deleteColumn")
    public HttpResult deleteColumn(@NotNull(message = "ID不能为空") @RequestParam("id") Long id) {
        return columnInfoService.removeById(id) ? HttpResult.setResult(HttpResultEnum.DELETE_SUCCESS)
                : HttpResult.setResult(HttpResultEnum.DELETE_ERROR);
    }
}
