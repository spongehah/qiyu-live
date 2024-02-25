-- Create syntax for TABLE 't_pay_order'
CREATE TABLE `t_pay_order` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `order_id` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单id',
  `product_id` int unsigned NOT NULL COMMENT '产品id',
  `status` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '状态（0待支付,1支付中,2已支付,3撤销,4无效）',
  `user_id` bigint unsigned NOT NULL COMMENT '用户id',
  `pay_channel` tinyint unsigned NOT NULL COMMENT '支付渠道（0支付宝 1微信 2银联 3收银台）',
  `source` tinyint unsigned NOT NULL COMMENT '来源',
  `pay_time` datetime DEFAULT NULL COMMENT '支付成功时间（以回调处理为准）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_id` (`order_id`)
) ENGINE=InnoDB AUTO_INCREMENT=242 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Create syntax for TABLE 't_pay_product'
CREATE TABLE `t_pay_product` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `name` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '产品名称',
  `price` int DEFAULT '0' COMMENT '产品价格（单位分）',
  `extra` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '扩展字段',
  `type` tinyint DEFAULT '0' COMMENT '类型（0旗鱼直播间产品）',
  `valid_status` tinyint DEFAULT '0' COMMENT '状态（0无效，1有效）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='付费产品表';

-- Create syntax for TABLE 't_pay_topic'
CREATE TABLE `t_pay_topic` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `topic` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'mq主题',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否有效',
  `biz_code` int NOT NULL COMMENT '业务code',
  `remark` varchar(200) NOT NULL COMMENT '描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='支付主题配置表';

-- Create syntax for TABLE 't_qiyu_currency_account'
CREATE TABLE `t_qiyu_currency_account` (
  `user_id` bigint unsigned NOT NULL COMMENT '用户id',
  `current_balance` int DEFAULT NULL COMMENT '当前余额',
  `total_charged` int DEFAULT NULL COMMENT '累计充值',
  `status` tinyint DEFAULT '1' COMMENT '账户状态(0无效1有效 2冻结）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='账户余额表';

-- Create syntax for TABLE 't_qiyu_currency_trade'
CREATE TABLE `t_qiyu_currency_trade` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `user_id` bigint DEFAULT NULL COMMENT '用户id',
  `num` int DEFAULT NULL COMMENT '流水金额（单位：分）',
  `type` tinyint DEFAULT NULL COMMENT '流水类型',
  `status` tinyint DEFAULT '1' COMMENT '状态0无效1有效',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=869 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='流水记录表';