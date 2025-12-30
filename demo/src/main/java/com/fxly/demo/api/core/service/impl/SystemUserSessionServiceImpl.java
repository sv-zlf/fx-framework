package com.fxly.demo.api.core.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fxly.demo.api.core.dto.SessionQueryDTO;
import com.fxly.demo.api.core.entity.SystemUserSession;
import com.fxly.demo.api.core.mapper.SystemUserSessionMapper;
import com.fxly.demo.api.core.service.ISystemUserSessionService;
import com.fxly.demo.system.global.HttpResult;
import com.fxly.demo.system.global.HttpResultEnum;
import com.fxly.demo.system.security.JwtTokenManage;
import com.fxly.demo.system.security.JwtUtil;
import com.fxly.demo.system.security.SecurityUtils;
import com.fxly.demo.utils.session.IpParseUtil;
import com.fxly.demo.utils.session.UserAgentParseUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

/**
* 系统用户会话表Service实现类
* @author admin
*/

@Slf4j
@Service
public class SystemUserSessionServiceImpl extends ServiceImpl<SystemUserSessionMapper, SystemUserSession> implements ISystemUserSessionService {

    @Resource
    private IpParseUtil ipParseUtil;
    @Resource
    private UserAgentParseUtil userAgentParseUtil;
    @Resource
    private JwtUtil jwtUtil;
    @Resource
    private JwtTokenManage tokenManage;

    @Override
    public Page<SystemUserSession> getUserSessionList(SessionQueryDTO query) {
        // 分页
        Page<SystemUserSession> page = new Page<>(query.getPageIndex(), query.getPageSize());
        // 查询条件
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LambdaUpdateWrapper  queryWrapper = new LambdaUpdateWrapper<SystemUserSession>()
                .like(StringUtils.isNotEmpty(query.getLoginName()),SystemUserSession::getLoginName,query.getLoginName()) // 登录用户名
                .like(StringUtils.isNotEmpty(query.getLoginLocation()),SystemUserSession::getLoginLocation,query.getLoginLocation())  // 登录地点
                .ge(StringUtils.isNotEmpty(query.getStartLoginTime()),SystemUserSession::getLoginTime, LocalDateTimeUtil.parse(query.getStartLoginTime(), formatter)) // 大于等于登录时间
                .le(StringUtils.isNotEmpty(query.getEndLoginTime()),SystemUserSession::getLoginTime, LocalDateTimeUtil.parse(query.getEndLoginTime(), formatter)) // 小于等于登录时间
                .orderByDesc(SystemUserSession::getCreateTime);
        // 执行
        baseMapper.selectPage(page,queryWrapper);
        return page;
    }

    @Override
    public void createSession(String token, String loginName, HttpServletRequest request) {
        // 解析客户端信息
        String ip = ipParseUtil.getRealIp(request);
        String loginLocation = ipParseUtil.getIpLocation(ip);
        String userAgentStr = request.getHeader("User-Agent");
        String browser = userAgentParseUtil.getBrowser(userAgentStr);
        String os = userAgentParseUtil.getOs(userAgentStr);
        Date expireTime = jwtUtil.extractExpiration(token);

        // 构建实体
        SystemUserSession session = new SystemUserSession();
        session.setSessionId(token);
        session.setLoginName(loginName);
        session.setHost(ip);
        session.setLoginLocation(loginLocation);
        session.setBrowser(browser);
        session.setOs(os);
        session.setSessionStatus(1);
        session.setLoginTime(LocalDateTime.now());
        session.setLastAccessTime(LocalDateTime.now());
        session.setExpireTime(DateUtil.toLocalDateTime(expireTime));
        baseMapper.insert(session);
    }

    /**
     * 更新最后访问时间
     */
    public void updateLastAccessTime(String token) {
        LambdaUpdateWrapper updateWrapper = new LambdaUpdateWrapper<SystemUserSession>()
                .eq(SystemUserSession::getSessionId, token)
                .set(SystemUserSession::getLastAccessTime, LocalDateTime.now());
        baseMapper.update(updateWrapper);
    }

    /**
     * 更新会话状态
     */
    public void updateSessionStatus(String token, Integer status) {
        LambdaUpdateWrapper updateWrapper = new LambdaUpdateWrapper<SystemUserSession>()
                .eq(SystemUserSession::getSessionId, token)
                .set(SystemUserSession::getSessionStatus,status);
        baseMapper.update(updateWrapper);
    }

    /**
     * 强制退出用户会话
     */
    @Override
    public HttpResult forceLogout(String sessionId) {
        try {
            // 查询会话信息
            LambdaQueryWrapper queryWrapper = new LambdaQueryWrapper<SystemUserSession>()
                    .eq(SystemUserSession::getSessionId, sessionId);
            List<SystemUserSession> sessionList = baseMapper.selectList(queryWrapper);
            if (ObjectUtil.isEmpty(sessionList)) {
                return HttpResult.error(400, "会话不存在");
            }
            else {
                // 检查会话状态
                SystemUserSession session = sessionList.get(0);
                if (session.getSessionStatus() == 0) {
                    return HttpResult.error(400, "该会话已离线");
                }
                // 获取当前登录用户名
                String currentUser = SecurityUtils.getUserName();
                log.info("管理员 {} 强制退出用户 {} 的会话", currentUser, session.getLoginName());
                tokenManage.delToken(sessionId);
                LambdaUpdateWrapper updateWrapper = new LambdaUpdateWrapper<SystemUserSession>()
                        .eq(SystemUserSession::getSessionId, sessionId)
                        .set(SystemUserSession::getSessionStatus, 0);
                baseMapper.update(updateWrapper);
                return HttpResult.success("强制退出成功");
            }

        } catch (Exception e) {
            log.error("强制退出失败", e);
            return HttpResult.setResult(HttpResultEnum.DELETE_ERROR);
        }
    }

    /**
     * 清理过期会话
     */
//    public void cleanExpiredSession() {
//        sysUserSessionMapper.deleteExpiredSession(new Date());
//    }
}

