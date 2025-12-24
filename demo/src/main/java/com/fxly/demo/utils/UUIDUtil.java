package com.fxly.demo.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

/**
 * @author zlf
 * @data 2025/12/23
 * @@description
 */

@Component
public class UUIDUtil {

    /**
     * MD5哈希 → Token→32位固定长度字符串
     * @param token 原始JWT Token
     * @return 32位短会话ID
     */
    public String generateByMd5(String token) {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Token不能为空");
        }
        // MD5哈希后转小写，固定32位
        return DigestUtils.md5Hex(token).toLowerCase();
    }

    /**
     * UUID（无哈希，36位）
     * @return 36位UUID（去除横线后32位）
     */
    public String generateByUuid() {
        // 生成UUID并去除横线，32位
        return java.util.UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 随机字符串（16位）
     * @return 16位字母数字混合短ID
     */
    public String generateRandomShortId() {
        // 生成16位随机字符串（字母+数字）
        return RandomStringUtils.randomAlphanumeric(16);
    }
}
