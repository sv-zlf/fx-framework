package com.fxly.demo.system.mybatisplus;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * MyBatis-Plus 字段自动填充处理器
 */
@Component // 必须注入Spring容器，否则不会生效
public class MyMetaObjectHandler implements MetaObjectHandler {

    // 插入操作时填充
    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Shanghai"));
        strictInsertFill(metaObject, "createTime", LocalDateTime.class,now);
         strictInsertFill(metaObject, "updateTime", LocalDateTime.class, now);
    }

    // 更新操作时填充（如果需要）
    @Override
    public void updateFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Shanghai"));
        strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, now);
    }
}