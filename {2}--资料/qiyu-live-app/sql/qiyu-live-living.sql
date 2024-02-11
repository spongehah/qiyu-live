-- Create syntax for TABLE 't_living_room'
CREATE TABLE `t_living_room` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `anchor_id` bigint DEFAULT NULL COMMENT '主播id',
  `type` tinyint NOT NULL DEFAULT '0' COMMENT '直播间类型（1普通直播间，2pk直播间）',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态（0无效1有效）',
  `room_name` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT 'EMPTY_STR' COMMENT '直播间名称',
  `covert_img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '直播间封面',
  `watch_num` int DEFAULT '0' COMMENT '观看数量',
  `good_num` int DEFAULT '0' COMMENT '点赞数量',
  `start_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '开播时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Create syntax for TABLE 't_living_room_record'
CREATE TABLE `t_living_room_record` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `anchor_id` bigint DEFAULT NULL COMMENT '主播id',
  `type` tinyint NOT NULL DEFAULT '0' COMMENT '直播间类型（0默认类型）',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态（0无效1有效）',
  `room_name` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT 'EMPTY_STR' COMMENT '直播间名称',
  `covert_img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '直播间封面',
  `watch_num` int DEFAULT '0' COMMENT '观看数量',
  `good_num` int DEFAULT '0' COMMENT '点赞数量',
  `start_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '开播时间',
  `end_time` datetime DEFAULT NULL COMMENT '关播时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;