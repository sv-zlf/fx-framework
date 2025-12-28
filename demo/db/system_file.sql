-- 文件信息表
DROP TABLE IF EXISTS `system_file`;
CREATE TABLE `system_file` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `original_name` varchar(255) NOT NULL COMMENT '原始文件名',
  `file_path` varchar(500) NOT NULL COMMENT '存储路径（相对路径）',
  `file_extension` varchar(20) NOT NULL COMMENT '文件扩展名',
  `file_size` bigint NOT NULL COMMENT '文件大小（字节）',
  `file_type` varchar(20) DEFAULT NULL COMMENT '文件类型（image/document/video/audio等）',
  `mime_type` varchar(100) DEFAULT NULL COMMENT 'MIME类型',
  `md5` varchar(32) DEFAULT NULL COMMENT 'MD5校验值',
  `user_id` bigint DEFAULT NULL COMMENT '上传用户ID',
  `username` varchar(50) DEFAULT NULL COMMENT '上传用户名',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除：0-否，1-是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_md5` (`md5`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件信息表';
