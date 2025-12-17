package com.fxly.demo.api.generate.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fxly.demo.api.generate.entity.ColumnInfo;
import com.fxly.demo.api.generate.entity.TableInfo;
import com.fxly.demo.api.generate.mapper.ColumnInfoMapper;
import com.fxly.demo.api.generate.mapper.TableInfoMapper;
import com.fxly.demo.api.generate.service.ITableInfoService;
import com.fxly.demo.util.FreemarkerUtil;
import com.fxly.demo.util.SqlParserUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.zip.ZipOutputStream;

/**
 * @author zlf
 * @data 2025/12/16
 * @@description
 */

@Service
public class TableInfoServiceImpl extends ServiceImpl<TableInfoMapper, TableInfo> implements ITableInfoService {

    @Resource
    private ColumnInfoMapper columnInfoMapper;

    @Override
    @Transactional
    public List<TableInfo> generateTable(String sql, String moudleName) {
        List<TableInfo> tableInfoList = SqlParserUtil.parseSqls(sql, moudleName);
        // 保存表信息
        this.saveBatch(tableInfoList);
        for (TableInfo tableInfo : tableInfoList){
            Long tableId = tableInfo.getId();
            // 保存字段信息
            for (ColumnInfo columnInfo : tableInfo.getColumnList()) {
                columnInfo.setTableId(tableId);
                columnInfoMapper.insert(columnInfo);
            }
        }
        return tableInfoList;
    }

    @Override
    public void generateCode(Long tableId) {
        TableInfo tableInfo = this.getById(tableId);
        if (tableInfo == null){
            throw new RuntimeException("表不存在");
        }
        else {
            List<ColumnInfo> columnInfoList = columnInfoMapper.selectList(new LambdaQueryWrapper<ColumnInfo>()
                    .eq(ColumnInfo::getTableId, tableId));
            tableInfo.setColumnList(columnInfoList);
        }
        FreemarkerUtil.generateCode(tableInfo);
    }

    @Override
    public void generateCode(Long tableId, ZipOutputStream zipOut) {
        TableInfo tableInfo = this.getById(tableId);
        if (tableInfo == null){
            throw new RuntimeException("表不存在");
        }
        else {
            List<ColumnInfo> columnInfoList = columnInfoMapper.selectList(new LambdaQueryWrapper<ColumnInfo>()
                    .eq(ColumnInfo::getTableId, tableId));
            tableInfo.setColumnList(columnInfoList);
        }
        FreemarkerUtil.generateCode(tableInfo, zipOut);
    }
}
