-- Create syntax for TABLE 't_anchor_shop_info'
CREATE TABLE `t_anchor_shop_info` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `anchor_id` int unsigned NOT NULL DEFAULT '0' COMMENT '主播id',
  `sku_id` int unsigned NOT NULL DEFAULT '0' COMMENT '商品sku id',
  `status` tinyint unsigned NOT NULL DEFAULT '1' COMMENT '有效（0无效，1有效）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='带货主播权限配置表';

-- Create syntax for TABLE 't_category_info'
CREATE TABLE `t_category_info` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `level` int unsigned NOT NULL DEFAULT '0' COMMENT '类目级别',
  `parent_id` int unsigned NOT NULL DEFAULT '0' COMMENT '父类目id',
  `category_name` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '类目名称',
  `status` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '状态（0无效，1有效）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='类目表';

-- Create syntax for TABLE 't_gift_config'
CREATE TABLE `t_gift_config` (
  `gift_id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '礼物id',
  `price` int unsigned DEFAULT NULL COMMENT '虚拟货币价格',
  `gift_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '礼物名称',
  `status` tinyint unsigned DEFAULT NULL COMMENT '状态(0无效,1有效)',
  `cover_img_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '礼物封面地址',
  `svga_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'svga资源地址',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`gift_id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='礼物配置表';

-- Create syntax for TABLE 't_gift_record'
CREATE TABLE `t_gift_record` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `user_id` bigint DEFAULT NULL COMMENT '发送人',
  `object_id` bigint DEFAULT NULL COMMENT '收礼人',
  `gift_id` int DEFAULT NULL COMMENT '礼物id',
  `price` int DEFAULT NULL COMMENT '送礼金额',
  `price_unit` tinyint DEFAULT NULL COMMENT '送礼金额的单位',
  `source` tinyint DEFAULT NULL COMMENT '礼物来源',
  `send_time` datetime DEFAULT NULL COMMENT '发送时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `json` json DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='送礼记录表';

-- Create syntax for TABLE 't_red_packet_config'
CREATE TABLE `t_red_packet_config` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `anchor_id` int NOT NULL DEFAULT '0' COMMENT '主播id',
  `start_time` datetime DEFAULT NULL COMMENT '红包雨活动开始时间',
  `total_get` int NOT NULL DEFAULT '0' COMMENT '一共领取数量',
  `total_get_price` int NOT NULL DEFAULT '0' COMMENT '一共领取金额',
  `max_get_price` int NOT NULL DEFAULT '0' COMMENT '最大领取金额',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '(1 待准备，2已准备，3已发送)',
  `total_price` int NOT NULL DEFAULT '0' COMMENT '红包雨总金额数',
  `total_count` int unsigned NOT NULL DEFAULT '0' COMMENT '红包雨总红包数',
  `config_code` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '唯一code',
  `remark` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='直播间红包雨配置';

-- Create syntax for TABLE 't_sku_info'
CREATE TABLE `t_sku_info` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `sku_id` int unsigned NOT NULL DEFAULT '0' COMMENT 'sku id',
  `sku_price` int unsigned NOT NULL DEFAULT '0' COMMENT 'sku价格',
  `sku_code` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'sku编码',
  `name` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '商品名称',
  `icon_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '缩略图',
  `original_icon_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '原图',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品描述',
  `status` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '状态(0下架，1上架)',
  `category_id` int NOT NULL COMMENT '类目id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品sku信息表';

-- Create syntax for TABLE 't_sku_order_info'
CREATE TABLE `t_sku_order_info` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `sku_id_list` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `user_id` bigint unsigned NOT NULL DEFAULT '0' COMMENT '用户id',
  `room_id` int unsigned NOT NULL DEFAULT '0' COMMENT '直播id',
  `status` int unsigned NOT NULL DEFAULT '0' COMMENT '状态',
  `extra` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品订单表';

-- Create syntax for TABLE 't_sku_stock_info'
CREATE TABLE `t_sku_stock_info` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `sku_id` int unsigned NOT NULL DEFAULT '0' COMMENT 'sku id',
  `stock_num` int unsigned NOT NULL DEFAULT '0' COMMENT 'sku库存',
  `status` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '状态（0无效，1有效）',
  `version` int unsigned DEFAULT NULL COMMENT '乐观锁',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='sku库存表';