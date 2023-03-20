CREATE TABLE `database` (
    `id`       VARCHAR(50) NOT NULL COMMENT '数据库id',
    `database_type`	   VARCHAR(20) NOT NULL COMMENT '数据库类型',
    `database_key`	   VARCHAR(100) NOT NULL COMMENT '数据库名称',
    `connect_info`  json NOT NULL COMMENT '数据库连接信息',
    `environment_id`  VARCHAR(50) NOT NULL COMMENT '所属环境id',
	`update_user`    varchar(50) NOT NULL COMMENT '更新人',
    `create_user`    varchar(50) NOT NULL COMMENT '创建人',
    `create_time` bigint(13)  NOT NULL COMMENT '创建时间',
	`update_time` bigint(13)  NOT NULL COMMENT '更新时间',
	`status` int(1) DEFAULT 1 COMMENT '状态 0: 删除 1: 正常',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE utf8mb4_general_ci;
