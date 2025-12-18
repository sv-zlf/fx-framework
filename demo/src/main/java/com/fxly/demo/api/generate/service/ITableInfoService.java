package com.fxly.demo.api.generate.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fxly.demo.api.generate.entity.TableInfo;

import java.util.List;
import java.util.zip.ZipOutputStream;

/**
 * @author zlf
 * @data 2025/12/16
 * @@description
 */
public interface ITableInfoService extends IService<TableInfo> {

    List<TableInfo> generateTable(String sql, String moudleName);

    void generateCode(Long tableId);

    void generateCode(Long tableId, ZipOutputStream zipOut);

    Page<TableInfo> getPageList(Integer pageIndex, Integer pageSize, String tableName);
}
