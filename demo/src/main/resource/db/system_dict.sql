CREATE TABLE `system_dict_type` (
                                 `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                 `dict_type_code` varchar(64) NOT NULL COMMENT '字典类型编码',
                                 `dict_type_name` varchar(128) NOT NULL COMMENT '字典类型名称',
                                 `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态（0：禁用；1：启用）',
                                 `sort` int NOT NULL DEFAULT '0' COMMENT '排序号',
                                 `remark` varchar(512) DEFAULT NULL COMMENT '备注',
                                 `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                 PRIMARY KEY (`id`),
                                 UNIQUE KEY `idx_dict_type_code` (`dict_type_code`) COMMENT '全局唯一索引：字典类型编码'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='字典类型表';

CREATE TABLE `system_dict_item` (
                                 `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                 `dict_item_code` varchar(64) NOT NULL COMMENT '字典项编码',
                                 `dict_item_name` varchar(128) NOT NULL COMMENT '字典项名称',
                                 `dict_type_code` varchar(64) NOT NULL COMMENT '关联字典类型编码（关联sys_dict_type.dict_type_code）',
                                 `sort` int NOT NULL DEFAULT '0' COMMENT '排序号（越小越靠前）',
                                 `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态（0：禁用；1：启用）',
                                 `style` varchar(32) DEFAULT NULL COMMENT '样式',
                                 `remark` varchar(512) DEFAULT NULL COMMENT '备注（如「默认性别选项」）',
                                 `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                 PRIMARY KEY (`id`),
                                 UNIQUE KEY `idx_dict_type_item` (`dict_type_code`,`dict_item_code`) COMMENT '唯一索引：同一字典类型下字典项编码唯一',
                                 KEY `idx_dict_type_status` (`dict_type_code`,`status`) COMMENT '联合索引：优化「按类型+状态」查询'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='字典项表（单级，存储某类字典的具体选项）';


-- 插入字典类型
INSERT INTO `system_dict_type` (`dict_type_code`, `dict_type_name`, `status`, `sort`, `remark`)
VALUES
    ('USER_GENDER', '用户性别', 1, 10, '系统基础性别字典'),
    ('ORDER_STATUS', '订单状态', 1, 20, '订单流程状态字典'),
    ('PRODUCT_CATEGORY', '商品分类', 1, 30, '商城商品分类字典'),
    ('COMMON_STATUS', '通用状态', 1, 40, '全局通用启用/禁用状态');

-- 插入字典项（对应上面的字典类型）
INSERT INTO `system_dict_item` (`dict_item_code`, `dict_item_name`, `dict_type_code`, `sort`, `status`, `style`, `remark`)
VALUES
-- 用户性别
('MALE', '男', 'USER_GENDER', 1, 1, '#1989fa', '男性用户标识'),
('FEMALE', '女', 'USER_GENDER', 2, 1, '#ff4d4f', '女性用户标识'),
('UNKNOWN', '未知', 'USER_GENDER', 3, 1, '#8c8c8c', '未知性别用户'),
-- 订单状态
('PENDING_PAY', '待支付', 'ORDER_STATUS', 1, 1, '#faad14', '订单创建未支付'),
('PAID', '已支付', 'ORDER_STATUS', 2, 1, '#52c41a', '订单已支付待发货'),
('SHIPPED', '已发货', 'ORDER_STATUS', 3, 1, '#40a9ff', '订单已发货待收货'),
('COMPLETED', '已完成', 'ORDER_STATUS', 4, 1, '#722ed1', '订单已完成'),
('CANCELLED', '已取消', 'ORDER_STATUS', 5, 1, '#ff7a45', '订单已取消'),
-- 商品分类
('ELECTRONICS', '电子产品', 'PRODUCT_CATEGORY', 1, 1, '#1890ff', '手机、电脑等电子产品'),
('CLOTHING', '服装鞋帽', 'PRODUCT_CATEGORY', 2, 1, '#9254de', '服装、鞋子、帽子等'),
('FOOD', '食品零食', 'PRODUCT_CATEGORY', 3, 1, '#fa8c16', '零食、饮料、生鲜等'),
-- 通用状态
('ENABLE', '启用', 'COMMON_STATUS', 1, 1, '#52c41a', '通用启用状态'),
('DISABLE', '禁用', 'COMMON_STATUS', 2, 1, '#ff4d4f', '通用禁用状态');