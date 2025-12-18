package com.fxly.demo.api.generate.controller;

import com.fxly.demo.api.generate.service.ITableInfoService;
import com.fxly.demo.system.global.HttpResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
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
public class GenerateController {

    @Resource
    private ITableInfoService tableInfoService;

    @Operation(summary = "按照建表语句生成信息")
    @PostMapping("/genTable")
    public HttpResult genTable(@RequestParam("sql") String sql,
                               @RequestParam("moudleName") String moudleName) {
        return HttpResult.success(tableInfoService.generateTable(sql, moudleName));
    }

    @Operation(summary = "按照表ID生成代码，本地工程")
    @PostMapping("/genCode")
    public HttpResult genCode(@RequestParam("tableId") Long tableId ) {
        tableInfoService.generateCode(tableId);
        return HttpResult.success();
    }

    @Operation(summary = "按照表ID生成代码，压缩包")
    @PostMapping("/genCodeZip")
    public void genCodeZip(@RequestParam("tableId") Long tableId,HttpServletResponse response) throws IOException {
        response.setContentType("application/zip"); // ZIP文件类型
        response.setCharacterEncoding("UTF-8");
        // 文件名编码
        String fileName = URLEncoder.encode("code-gen.zip", "UTF-8").replace("+", "%20");
        // 触发浏览器下载文件
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        try (ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream())) {
            // 调用Service方法，生成代码到ZIP流
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

}
