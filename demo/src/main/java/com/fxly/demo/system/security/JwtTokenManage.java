package com.fxly.demo.system.security;

import com.fxly.demo.system.redis.RedisCache;
import com.fxly.demo.system.redis.RedisConstants;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenManage {

    @Resource
    private RedisCache redisCache;

    private static String getTokenKey(String token) {
        return RedisConstants.TOKEN.concat(token);
    }

    // 获取并刷新
    public Object getToken(String token) {
        String key = getTokenKey(token);
        boolean hasKey = redisCache.hasKey(key);
        if (hasKey) {
            redisCache.expire(key,7200L); // update lastAccessedTime
            return redisCache.get(key);
        }
        return null;
    }

    // 设置
    public boolean setToken(String token, Object object) {
        return redisCache.set(getTokenKey(token), object,7200L); // set maxInactiveInterval
    }

    // 删除
    public void delToken(String token) {
        redisCache.del(getTokenKey(token));
    }

}
