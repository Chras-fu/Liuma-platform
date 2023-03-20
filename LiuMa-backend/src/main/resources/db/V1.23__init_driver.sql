CREATE TABLE `driver` (
    `id`       VARCHAR(50) NOT NULL COMMENT '驱动配置 id',
	`name`	   VARCHAR(100) NOT NULL COMMENT '驱动配置名称',
	`setting` json NOT NULL COMMENT '驱动配置',
	`description` VARCHAR(200) DEFAULT NULL COMMENT '驱动描述',
    `project_id`  VARCHAR(50) NOT NULL COMMENT '所属项目id',
    `create_time` bigint(13)  NOT NULL COMMENT '创建时间',
	`update_time` bigint(13)  NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`), UNIQUE(`project_id`, `name`), INDEX(`project_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE utf8mb4_general_ci;