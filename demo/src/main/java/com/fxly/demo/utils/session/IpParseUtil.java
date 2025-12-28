package com.fxly.demo.utils.session;

import jakarta.servlet.http.HttpServletRequest;
import org.lionsoul.ip2region.xdb.Searcher;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.InitializingBean;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;

/**
 * IP解析工具类
 */
@Component
@Slf4j
public class IpParseUtil implements InitializingBean {

	private static final String IP2REGION_XDB_PATH = "ip2region/ip2region.xdb";
	// 全局ip2region搜索器
	private static Searcher searcher;

	@Override
	public void afterPropertiesSet() throws Exception {
		InputStream inputStream = null;
		try {
			ClassPathResource resource = new ClassPathResource(IP2REGION_XDB_PATH);
			inputStream = resource.getInputStream();
			// 将xdb文件读取到内存（提升解析性能）
			byte[] dbBinStr = new byte[inputStream.available()];
			inputStream.read(dbBinStr);
			// 初始化搜索器
			searcher = Searcher.newWithBuffer(dbBinStr);
		} catch (IOException e) {
			throw new RuntimeException("初始化IP解析工具失败：" + e.getMessage(), e);
		} finally {
			// 关闭输入流，避免资源泄漏
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					log.warn("关闭IP解析工具输入流失败", e);
				}
			}
		}
	}

	/**
	 * 获取客户端真实IP（兼容反向代理/nginx/负载均衡场景）
	 * @param request HttpServletRequest（Spring 3.5基于Servlet 2.5）
	 * @return 真实IP地址
	 */
	public String getRealIp(HttpServletRequest request) {
		// 依次从反向代理头获取真实IP
		String ip = request.getHeader("x-forwarded-for");
		if (isInvalidIp(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (isInvalidIp(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (isInvalidIp(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (isInvalidIp(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		// 最终取原生IP
		if (isInvalidIp(ip)) {
			ip = request.getRemoteAddr();
		}
		// 处理多IP场景（x-forwarded-for可能包含多个IP，用逗号分隔）
		if (StringUtils.isNotBlank(ip) && ip.contains(",")) {
			ip = ip.split(",")[0].trim();
		}
		// 本地IP转换
		if ("0:0:0:0:0:0:0:1".equals(ip) || "127.0.0.1".equals(ip)) {
			ip = "127.0.0.1";
		}
		return ip;
	}

	/**
	 * 解析IP对应的地理位置
	 * @param ip 待解析的IP地址
	 * @return 格式化后的位置（如：中国北京市北京市联通），未知IP返回"未知位置"
	 */
	public String getIpLocation(String ip) {
		// 本地IP直接返回
		if ("127.0.0.1".equals(ip) || StringUtils.isBlank(ip)) {
			return "本地局域网";
		}
		try {
			// 调用ip2region解析IP
			String region = searcher.search(ip);
			if (StringUtils.isBlank(region)) {
				return "未知位置";
			}
			// 格式化解析结果（去掉0值，如：中国|0|北京|北京市|联通 → 中国北京市北京市联通）
			String[] regionParts = region.split("\\|");
			StringBuilder location = new StringBuilder();
			for (String part : regionParts) {
				if (StringUtils.isNotBlank(part) && !"0".equals(part)) {
					location.append(part);
				}
			}
			return location.length() > 0 ? location.toString() : "未知位置";
		} catch (Exception e) {
			// 解析失败降级返回
			return "未知位置";
		}
	}

	/**
	 * 私有方法：判断IP是否无效（空/unknown）
	 */
	private boolean isInvalidIp(String ip) {
		return StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip.trim());
	}
}