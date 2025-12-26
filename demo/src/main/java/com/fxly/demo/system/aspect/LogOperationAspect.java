package com.fxly.demo.system.aspect;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.fxly.demo.api.core.entity.SystemLog;
import com.fxly.demo.api.core.service.ISystemLogService;
import com.fxly.demo.system.annotation.LogOperation;
import com.fxly.demo.system.constant.LogType;
import com.fxly.demo.system.global.HttpResult;
import com.fxly.demo.utils.session.IpParseUtil;
import eu.bitwalker.useragentutils.UserAgent;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

/**
 * 系统日志切面
 * 用于拦截带有@LogOperation注解的方法，自动记录操作日志
 */
@Slf4j
@Aspect
@Component
public class LogOperationAspect {

    @Resource
    private IpParseUtil ipParseUtil;

    private final ISystemLogService systemLogService;

    public LogOperationAspect(ISystemLogService systemLogService) {
        this.systemLogService = systemLogService;
    }

    /**
     * 线程本地变量，用于存储开始时间
     */
    private static final ThreadLocal<Long> TIME_THREADLOCAL = new ThreadLocal<>();

    /**
     * 线程本地变量，用于存储当前日志对象
     */
    private static final ThreadLocal<SystemLog> LOG_THREADLOCAL = new ThreadLocal<>();

    @Pointcut("@annotation(com.fxly.demo.system.annotation.LogOperation)")
    public void logPointCut() {}

    @Before("logPointCut() && @annotation(logOperation)")
    public void doBefore(JoinPoint joinPoint, LogOperation logOperation) {
        try {
            // 记录开始时间
            TIME_THREADLOCAL.set(System.currentTimeMillis());

            // 获取当前请求
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                return;
            }

            HttpServletRequest request = attributes.getRequest();

            // 获取用户信息
            Long userId = null;
            String userName = "anonymous";
            try {
                Object userAttr = request.getAttribute("currentUser");
                if (userAttr != null) {
                    // TODO: 根据实际的用户信息结构获取
                    // userId = ...
                    // userName = ...
                }
            } catch (Exception e) {
                log.debug("获取用户信息失败", e);
            }

            // 获取IP地址
            String ipAddress = ipParseUtil.getRealIp(request);

            // 获取浏览器和操作系统信息
            String userAgentStr = request.getHeader("User-Agent");
            UserAgent userAgent = UserAgent.parseUserAgentString(userAgentStr);
            String browser = userAgent.getBrowser().getName() + " " + userAgent.getBrowserVersion();
            String os = userAgent.getOperatingSystem().getName();

            // 构建日志对象
            SystemLog log = new SystemLog();
            log.setModuleName(logOperation.module());
            log.setOperationType(logOperation.type().name());
            log.setDescription(StrUtil.isNotBlank(logOperation.description())
                ? logOperation.description()
                : logOperation.type().getDescription());
            log.setUserId(userId);
            log.setUserName(userName);
            log.setIpAddress(ipAddress);
            log.setRequestUrl(request.getRequestURL().toString());
            log.setRequestMethod(request.getMethod());
            log.setBrowser(browser);
            log.setOs(os);

            // 保存请求参数
            if (logOperation.saveRequestData()) {
                Object[] args = joinPoint.getArgs();
                if (args != null && args.length > 0) {
                    String params = JSONUtil.toJsonStr(args);
                    // 限制参数长度
                    if (params.length() > 2000) {
                        params = params.substring(0, 2000) + "...";
                    }
                    log.setRequestParams(params);
                }
            }

            // 存储到线程本地变量
            LOG_THREADLOCAL.set(log);

        } catch (Exception e) {
            log.error("系统日志前置处理异常", e);
        }
    }

    @AfterReturning(pointcut = "logPointCut() && @annotation(logOperation)", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, LogOperation logOperation, Object jsonResult) {
        try {
            SystemLog log = LOG_THREADLOCAL.get();
            if (log == null) {
                return;
            }

            // 计算执行时间
            long executionTime = System.currentTimeMillis() - TIME_THREADLOCAL.get();
            log.setExecutionTime((int) executionTime);

            // 判断操作是否成功
            if (jsonResult instanceof HttpResult) {
                HttpResult result = (HttpResult) jsonResult;
                log.setStatus(result.getCode() == 200 ? 1 : 0);
                if (result.getCode() != 200) {
                    log.setErrorMsg(result.getMsg());
                }

                // 保存响应结果
                if (logOperation.saveResponseData()) {
                    String response = JSONUtil.toJsonStr(result);
                    if (response.length() > 2000) {
                        response = response.substring(0, 2000) + "...";
                    }
                    log.setResponseData(response);
                }
            } else {
                log.setStatus(1);
            }

            // 保存日志
            systemLogService.save(log);

        } catch (Exception e) {
            log.error("系统日志后置处理异常", e);
        } finally {
            // 清理线程本地变量
            TIME_THREADLOCAL.remove();
            LOG_THREADLOCAL.remove();
        }
    }

    @AfterThrowing(pointcut = "logPointCut() && @annotation(logOperation)", throwing = "throwable")
    public void doAfterThrowing(JoinPoint joinPoint, LogOperation logOperation, Throwable throwable) {
        try {
            SystemLog log = LOG_THREADLOCAL.get();
            if (log == null) {
                return;
            }

            // 计算执行时间
            long executionTime = System.currentTimeMillis() - TIME_THREADLOCAL.get();
            log.setExecutionTime((int) executionTime);

            // 记录异常信息
            log.setStatus(0);
            log.setErrorMsg(throwable.getMessage());

            // 保存日志
            systemLogService.save(log);

        } catch (Exception e) {
            log.error("系统日志异常处理异常", e);
        } finally {
            // 清理线程本地变量
            TIME_THREADLOCAL.remove();
            LOG_THREADLOCAL.remove();
        }
    }
}
