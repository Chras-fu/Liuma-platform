CREATE TABLE `debug_data` (
    `id`       VARCHAR(50) NOT NULL COMMENT '调试数据id',
	`data`	   json NOT NULL COMMENT '调试数据',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE utf8mb4_general_ci;