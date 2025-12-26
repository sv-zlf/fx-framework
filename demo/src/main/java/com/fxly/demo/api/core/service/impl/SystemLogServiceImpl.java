package com.fxly.demo.api.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fxly.demo.api.core.entity.SystemLog;
import com.fxly.demo.api.core.mapper.SystemLogMapper;
import com.fxly.demo.api.core.service.ISystemLogService;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

/**
* 系统日志表 Service实现类
* @author admin
*/
@Service
public class SystemLogServiceImpl extends ServiceImpl<SystemLogMapper, SystemLog> implements ISystemLogService {

    @Override
    public Page<SystemLog> getPageList(SystemLog systemLog) {
        // 分页
        Page<SystemLog> page = new Page<>(systemLog.getPageIndex(),systemLog.getPageSize());
        // 查询条件
        LambdaQueryWrapper<SystemLog> queryWrapper = new LambdaQueryWrapper<>();

        // 执行查询
        baseMapper.selectPage(page,queryWrapper);
        return page;
    }
}