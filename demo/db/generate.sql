CREATE TABLE  `column_info` (
                                             `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                             `table_id` BIGINT NOT NULL  COMMENT '数据库表ID',
                                             `column_name` VARCHAR(64) NOT NULL COMMENT '数据库字段名（原始）：如user_name',
                                             `property_name` VARCHAR(64) NOT NULL COMMENT 'Java属性名（驼峰）：如userName',
                                             `column_type` VARCHAR(32) NOT NULL COMMENT '数据库字段类型：如varchar、bigint',
                                             `java_type` VARCHAR(32) NOT NULL COMMENT 'Java类型：如String、Long',
                                             `comment` VARCHAR(255) DEFAULT NULL COMMENT '字段注释',
                                             `primary_key` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否主键（0：否，1：是）',
                                             `not_null` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否非空（0：否，1：是）',
                                             `auto_increment` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否自增（0：否，1：是）',
                                             PRIMARY KEY (`id`),
                                             KEY `idx_table_id` (`table_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据库列信息';

CREATE TABLE `table_info` (
                              `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                              `table_name` varchar(64) NOT NULL COMMENT '数据库表名（原始）：如sys_user',
                              `class_name` varchar(64) NOT NULL COMMENT 'Java类名（驼峰首字母大写）：如SysUser',
                              `table_comment` varchar(255) DEFAULT NULL COMMENT '表注释',
                              `author` varchar(64) DEFAULT NULL COMMENT '作者',
                              `remark` varchar(512) DEFAULT NULL COMMENT '备注',
                              PRIMARY KEY (`id`),
                              KEY `idx_table_name` (`table_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据表信息';

ALTER TABLE table_info ADD moudle_name varchar(32) NULL;