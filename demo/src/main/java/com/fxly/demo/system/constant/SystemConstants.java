package com.fxly.demo.system.constant;

/**
 * 系统通用常量类
 * 按业务模块拆分内部静态类，统一管理所有硬编码常量，避免散落在代码中
 * 命名规范：全大写 + 下划线分隔，静态常量用 public static final 修饰
 */
public class SystemConstants {

    // ====================== Redis 相关常量 ======================
    public static class RedisKey {
        /**
         * 用户权限码缓存Key前缀：user:permissions:{userId}
         */
        public static final String USER_PERMISSIONS_PREFIX = "user:permissions:";

        /**
         * 用户角色缓存Key前缀：user:roles:{userId}
         */
        public static final String USER_ROLES_PREFIX = "user:roles:";

        /**
         * Token缓存Key前缀：token:{username}
         */
        public static final String TOKEN_PREFIX = "token:";

        /**
         * 菜单全量缓存Key（用于菜单管理模块）
         */
        public static final String MENU_ALL_LIST = "sys:menu:all";

        /**
         * 默认缓存过期时间（秒）：2小时（与Token过期时间一致）
         */
        public static final Long DEFAULT_EXPIRE_SECONDS = 7200L;

        /**
         * Token缓存过期时间（毫秒）：兼容原有RedisUtil逻辑
         */
        public static final Long TOKEN_EXPIRE_MILLIS = 7200000L;
    }

    // ====================== 菜单相关常量 ======================
    public static class Menu {
        /**
         * 菜单类型：目录（0）
         */
        public static final Integer TYPE_DIR = 0;

        /**
         * 菜单类型：页面（1）
         */
        public static final Integer TYPE_PAGE = 1;

        /**
         * 菜单类型：按钮（2）
         */
        public static final Integer TYPE_BUTTON = 2;

        /**
         * 菜单状态：启用（1）
         */
        public static final Integer STATUS_ENABLE = 1;

        /**
         * 菜单状态：禁用（0）
         */
        public static final Integer STATUS_DISABLE = 0;

        /**
         * 顶级菜单父ID（根节点）
         */
        public static final Long TOP_PARENT_ID = 0L;
    }

    // ====================== 权限校验相关常量 ======================
    public static class Permission {
        /**
         * 权限校验逻辑：AND（需拥有所有权限码）
         */
        public static final String LOGICAL_AND = "AND";

        /**
         * 权限校验逻辑：OR（拥有任意一个权限码）
         */
        public static final String LOGICAL_OR = "OR";

        /**
         * 超级管理员角色编码（无需校验权限）
         */
        public static final String SUPER_ADMIN_ROLE_CODE = "SUPER_ADMIN";

        /**
         * 权限码分隔符（用于批量拼接）
         */
        public static final String PERMISSION_SEPARATOR = ",";

        /**
         * 权限码格式前缀（资源:操作）
         */
        public static final String PERMISSION_FORMAT = "%s:%s";
    }

    // ====================== HTTP 响应相关常量 ======================
    public static class HttpStatus {
        /**
         * 成功状态码
         */
        public static final Integer SUCCESS = 200;

        /**
         * 权限不足状态码
         */
        public static final Integer FORBIDDEN = 403;

        /**
         * 未登录/Token失效状态码
         */
        public static final Integer UNAUTHORIZED = 401;

        /**
         * 服务器内部错误
         */
        public static final Integer ERROR = 500;
    }

    // ====================== Token 相关常量 ======================
    public static class Token {
        /**
         * Token请求头名称
         */
        public static final String TOKEN_HEADER = "Authorization";

        /**
         * Token前缀（Bearer ）
         */
        public static final String TOKEN_PREFIX = "Bearer ";

        /**
         * Token密钥（实际项目建议配置在yml中，此处仅示例）
         */
        public static final String TOKEN_SECRET = "fxly_demo_2025_secret";
    }

    // ====================== 通用状态常量 ======================
    public static class Status {
        /**
         * 启用/正常
         */
        public static final Integer ENABLE = 1;

        /**
         * 禁用/删除
         */
        public static final Integer DISABLE = 0;
    }

    // ====================== 自定义一些相关常量 ======================
    public static class Common {
        /**
         * 默认用户角色
         */
        public static final String DEFAULT_ROLE_CODE = "ROLE_USER";
    }

    public class RedisConstants {

        public static final String TOKEN = "authinfo:token:"
                , TIPS = "authinfo:tips:";

    }
}