DELIMITER ;;
CREATE DEFINER=`root`@`%` PROCEDURE `create_t_user_phone_100`()
BEGIN

         DECLARE i INT;
         DECLARE table_name VARCHAR(30);
         DECLARE table_pre VARCHAR(30);
         DECLARE sql_text VARCHAR(3000);
         DECLARE table_body VARCHAR(2000);
         SET i=0;
         SET table_name='';

         SET sql_text='';
         SET table_body = ' (
  id bigint unsigned NOT NULL AUTO_INCREMENT COMMENT \'主键id\',
  phone varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL DEFAULT \'\' COMMENT \'手机号\',
  user_id bigint DEFAULT -1 COMMENT \'用户id\',
  status tinyint DEFAULT -1 COMMENT \'状态(0无效，1有效)\',
  create_time datetime DEFAULT CURRENT_TIMESTAMP COMMENT \'创建时间\',
  update_time datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT \'更新时间\',
  PRIMARY KEY (id),
  UNIQUE KEY `udx_phone` (`phone`),
  KEY idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;';

            WHILE i<100 DO
                IF i<10 THEN
                    SET table_name = CONCAT('t_user_phone_0',i);
                ELSE
                    SET table_name = CONCAT('t_user_phone_',i);
                END IF;

                SET sql_text=CONCAT('CREATE TABLE ',table_name, table_body);
            SELECT sql_text;
            SET @sql_text=sql_text;
            PREPARE stmt FROM @sql_text;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;
            SET i=i+1;
        END WHILE;


    END;;
DELIMITER ;