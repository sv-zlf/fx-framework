package com.fxly.demo.api.core.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fxly.demo.api.core.entity.SystemLog;

/**
* 系统日志表 Service
* @author admin
*/
public interface ISystemLogService extends IService<SystemLog> {

    Page<SystemLog> getPageList(SystemLog systemLog);

}