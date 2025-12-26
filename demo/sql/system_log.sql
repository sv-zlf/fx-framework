-- 系统日志表
DROP TABLE IF EXISTS `system_log`;
CREATE TABLE `system_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `module_name` varchar(100) DEFAULT NULL COMMENT '模块名称',
  `operation_type` varchar(50) DEFAULT NULL COMMENT '操作类型',
  `description` varchar(500) DEFAULT NULL COMMENT '操作描述',
  `user_id` bigint(20) DEFAULT NULL COMMENT '操作人ID',
  `user_name` varchar(100) DEFAULT NULL COMMENT '操作人名称',
  `ip_address` varchar(100) DEFAULT NULL COMMENT 'IP地址',
  `request_url` varchar(500) DEFAULT NULL COMMENT '请求URL',
  `request_method` varchar(10) DEFAULT NULL COMMENT '请求方法',
  `request_params` text COMMENT '请求参数',
  `response_data` text COMMENT '响应结果',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态(0-失败,1-成功)',
  `error_msg` varchar(2000) DEFAULT NULL COMMENT '错误信息',
  `execution_time` int(11) DEFAULT NULL COMMENT '执行时间(ms)',
  `browser` varchar(100) DEFAULT NULL COMMENT '浏览器',
  `os` varchar(100) DEFAULT NULL COMMENT '操作系统',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_operation_type` (`operation_type`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统日志表';
