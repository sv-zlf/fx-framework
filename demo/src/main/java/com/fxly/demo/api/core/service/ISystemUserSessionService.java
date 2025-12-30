package com.fxly.demo.api.core.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fxly.demo.api.core.dto.SessionQueryDTO;
import com.fxly.demo.api.core.entity.SystemUserSession;
import com.fxly.demo.system.global.HttpResult;
import jakarta.servlet.http.HttpServletRequest;

/**
* 系统用户会话表Service
* @author admin
*/

public interface ISystemUserSessionService extends IService<SystemUserSession> {

    Page<SystemUserSession> getUserSessionList(SessionQueryDTO query);

    public void createSession(String token, String loginName, HttpServletRequest request);

    public void updateLastAccessTime(String token);

    /**
     * 更新会话状态
     * @param token 用于标识会话的唯一令牌
     * @param status 需要更新的状态值
     */
    public void updateSessionStatus(String token, Integer status);

    /**
     * 强制退出用户会话
     * @param sessionId 会话ID
     * @return 操作结果
     */
    HttpResult forceLogout(String sessionId);
}

