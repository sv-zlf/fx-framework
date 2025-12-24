package com.fxly.demo.utils.session;

import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;


/**
 * User-Agent解析工具类
 * 解析客户端浏览器、操作系统信息
 */
@Component
public class UserAgentParseUtil {

    /**
     * 从Request中获取User-Agent字符串
     */
    public String getUserAgentStr(HttpServletRequest request) {
        if (request == null) {
            return "";
        }
        return StringUtils.defaultIfBlank(request.getHeader("User-Agent"), "");
    }

    /**
     * 解析浏览器信息（格式：浏览器名称 + 版本，如：Chrome 120.0.0.0）
     * @param userAgentStr Request头中的User-Agent字符串
     */
    public String getBrowser(String userAgentStr) {
        if (StringUtils.isBlank(userAgentStr)) {
            return "未知浏览器";
        }
        try {
            // 解析User-Agent
            UserAgent userAgent = UserAgent.parseUserAgentString(userAgentStr);
            Browser browser = userAgent.getBrowser();
            // 获取浏览器版本
            String browserVersion = userAgent.getBrowserVersion().getVersion();
            // 格式化结果（处理版本为空的情况）
            return StringUtils.isNotBlank(browserVersion)
                    ? browser.getName() + " " + browserVersion
                    : browser.getName();
        } catch (Exception e) {
            // 解析失败降级返回
            return "未知浏览器";
        }
    }

    /**
     * 解析操作系统信息（如：Windows 10、macOS 14、Android 13）
     * @param userAgentStr Request头中的User-Agent字符串
     */
    public String getOs(String userAgentStr) {
        if (StringUtils.isBlank(userAgentStr)) {
            return "未知操作系统";
        }
        try {
            // 解析User-Agent
            UserAgent userAgent = UserAgent.parseUserAgentString(userAgentStr);
            OperatingSystem os = userAgent.getOperatingSystem();
            return os.getName();
        } catch (Exception e) {
            // 解析失败降级返回
            return "未知操作系统";
        }
    }

    /**
     * 重载方法：直接从Request解析浏览器信息
     */
    public String getBrowser(HttpServletRequest request) {
        String userAgentStr = getUserAgentStr(request);
        return getBrowser(userAgentStr);
    }

    /**
     * 重载方法：直接从Request解析操作系统信息
     */
    public String getOs(HttpServletRequest request) {
        String userAgentStr = getUserAgentStr(request);
        return getOs(userAgentStr);
    }
}
