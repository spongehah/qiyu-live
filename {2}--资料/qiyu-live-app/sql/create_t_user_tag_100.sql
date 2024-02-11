DELIMITER ;;
CREATE DEFINER=`root`@`%` PROCEDURE `create_t_user_tag_100`()
BEGIN

         DECLARE i INT;
         DECLARE table_name VARCHAR(100);
         DECLARE sql_text VARCHAR(3000);
         DECLARE table_body VARCHAR(2000);
         SET i=0;
         SET sql_text='';
         SET table_body='(
   user_id bigint NOT NULL DEFAULT -1 COMMENT \'用户id\',
   tag_info_01 bigint NOT NULL DEFAULT 0 COMMENT \'标签记录字段\',
   tag_info_02 bigint NOT NULL DEFAULT 0 COMMENT \'标签记录字段\',
   tag_info_03 bigint NOT NULL DEFAULT 0 COMMENT \'标签记录字段\',
   create_time datetime DEFAULT CURRENT_TIMESTAMP COMMENT \'创建时间\',
   update_time datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT \'更新时间\',
  PRIMARY KEY (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT=\'用户标签记录\';';


            WHILE i<100 DO
                IF i<10 THEN
                    SET table_name = CONCAT('t_user_tag_0',i);
                ELSE
                    SET table_name = CONCAT('t_user_tag_',i);
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