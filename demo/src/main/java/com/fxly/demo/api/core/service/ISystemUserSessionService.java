package com.fxly.demo.api.core.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fxly.demo.api.core.dto.SessionQueryDTO;
import com.fxly.demo.api.core.entity.SystemUserSession;
import jakarta.servlet.http.HttpServletRequest;

/**
* 系统用户会话表 Service
* @author admin
*/

public interface ISystemUserSessionService extends IService<SystemUserSession> {

    Page<SystemUserSession> getPageList(SessionQueryDTO query);

    public void createSession(String token, String loginName, HttpServletRequest request);

    public void updateLastAccessTime(String token);

    public void updateSessionStatus(String token, Integer status);
}