CREATE TABLE `system_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_name` varchar(50) NOT NULL COMMENT '用户名（唯一）',
  `password` varchar(100) NOT NULL COMMENT '密码（BCrypt加密）',
  `nick_name` varchar(50) DEFAULT '' COMMENT '昵称',
  `email` varchar(100) DEFAULT '' COMMENT '邮箱',
  `phone` varchar(20) DEFAULT '' COMMENT '手机号',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态（1：启用，0：禁用）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_name` (`user_name`) COMMENT '用户名唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

CREATE TABLE `system_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_name` varchar(50) NOT NULL COMMENT '角色名称（如：管理员）',
  `role_code` varchar(50) NOT NULL COMMENT '角色编码（Spring Security规范：ROLE_XXX）',
  `description` varchar(200) DEFAULT '' COMMENT '角色描述',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态（1：启用，0：禁用）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_code` (`role_code`) COMMENT '角色编码唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统角色表';

CREATE TABLE `system_menu` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `parent_id` bigint NOT NULL DEFAULT 0 COMMENT '父菜单ID（0：顶级菜单）',
  `menu_name` varchar(50) NOT NULL COMMENT '菜单名称',
  `path` varchar(200) DEFAULT '' COMMENT '路由路径（前端用）',
  `component` varchar(200) DEFAULT '' COMMENT '前端组件路径（前端用）',
  `permission` varchar(100) DEFAULT '' COMMENT '权限标识（如：sys:user:list）',
  `type` tinyint NOT NULL COMMENT 'type 1目录 2菜单 3按钮',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序（升序）',
  `icon` varchar(50) DEFAULT '' COMMENT '菜单图标（前端用）',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态（1：启用，0：禁用）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统菜单表';

CREATE TABLE `system_user_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`,`role_id`) COMMENT '用户-角色唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户-角色关联表';

CREATE TABLE `system_role_menu` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `menu_id` bigint NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_menu` (`role_id`,`menu_id`) COMMENT '角色-菜单唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色-菜单关联表';

-- 给菜单表添加「是否叶子节点」字段（适配实体类默认值 true = 数据库 1）
ALTER TABLE system_menu
    ADD COLUMN is_leaf TINYINT(1) NOT NULL DEFAULT 1
        COMMENT '是否叶子节点（1=true=最后一级/无下级子菜单，0=false=非最后一级/有下级子菜单）'
        AFTER status; -- 放在 status 字段后（可根据实际需求调整位置，如 BEFORE create_time）;

-- 给菜单表添加「是否外链」字段（适配实体类默认值 false = 数据库 0）
ALTER TABLE system_menu
    ADD COLUMN is_external TINYINT(1) NOT NULL DEFAULT 0
        COMMENT '是否外链（1=true=外部链接，0=false=内部路由）'
        AFTER is_leaf; -- 放在 is_leaf 字段后，保持字段顺序和实体类一致;


ALTER TABLE `system_menu`
    ADD COLUMN `is_full` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否全屏路由：0=非全屏（默认），1=全屏（对应前端route.meta.isFull）';

-- 仅标记是否使用iframe（推荐）
ALTER TABLE system_menu
    ADD COLUMN `iframe` tinyint(1) NOT NULL DEFAULT 0 COMMENT '0=不使用iframe，1=使用iframe';

ALTER TABLE system_menu
    ADD COLUMN svg_icon VARCHAR(128)
        COMMENT 'SVG图标名称（关联前端SVG文件）'  -- 字段注释，便于维护
        NULL DEFAULT NULL  -- 允许为空，默认值为NULL
        AFTER `icon`;  -- 字段位置：添加在 `icon` 字段之后（可调整）

ALTER TABLE system_menu
-- 菜单标题（可用于显示别名、多语言标题等，非空时优先显示）
    ADD COLUMN title VARCHAR(100) COMMENT '菜单标题（用于显示别名/多语言）' AFTER menu_name,
-- 是否隐藏（true=隐藏，false=显示，默认不隐藏）
    ADD COLUMN is_hide TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否隐藏菜单：0-显示（默认），1-隐藏';