ALTER TABLE `notification` DROP COLUMN `update_user`;
ALTER TABLE `notification` DROP COLUMN `create_user`;

CREATE TABLE `plan_notice` (
    `id`       VARCHAR(50) NOT NULL COMMENT '计划通知id',
    `plan_id`	   VARCHAR(50) NOT NULL COMMENT '所属计划id',
    `notification_id`   VARCHAR(50)  NOT NULL COMMENT '通知配置id',
    `condition`   VARCHAR(20)  NOT NULL COMMENT '触发条件',
    PRIMARY KEY (`id`), UNIQUE(`plan_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE utf8mb4_general_ci;
