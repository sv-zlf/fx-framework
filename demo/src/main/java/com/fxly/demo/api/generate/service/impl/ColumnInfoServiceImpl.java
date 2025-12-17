package com.fxly.demo.api.generate.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fxly.demo.api.generate.entity.ColumnInfo;
import com.fxly.demo.api.generate.mapper.ColumnInfoMapper;
import com.fxly.demo.api.generate.service.IColumnInfoService;
import org.springframework.stereotype.Service;

/**
 * @author zlf
 * @data 2025/12/16
 * @@description
 */

@Service
public class ColumnInfoServiceImpl extends ServiceImpl<ColumnInfoMapper, ColumnInfo> implements IColumnInfoService {
}
