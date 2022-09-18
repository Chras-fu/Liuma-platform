ALTER TABLE `operation` RENAME TO `operation_web`;

CREATE TABLE `operation_app` (
    `id`       VARCHAR(50) NOT NULL COMMENT '操作id',
	`name`	   VARCHAR(20) NOT NULL COMMENT '操作名',
	`type`	   VARCHAR(20) NOT NULL COMMENT '操作分类',
    `from`     VARCHAR(20) NOT NULL COMMENT '操作来源',
    `system`   VARCHAR(20) NOT NULL COMMENT '所属系统',
    `element` json DEFAULT NULL COMMENT '元素定义',
    `data` json DEFAULT NULL COMMENT '数据定义',
    `code`  text DEFAULT NULL COMMENT '操作代码',
    `description` varchar(200) DEFAULT NULL COMMENT '操作描述',
    `project_id` varchar(50) NOT NULL COMMENT '所属项目id',
	`update_user`    varchar(50) NOT NULL COMMENT '更新人',
    `create_user`    varchar(50) NOT NULL COMMENT '创建人',
    `create_time` bigint(13)  NOT NULL COMMENT '创建时间',
	`update_time` bigint(13)  NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`), UNIQUE(`project_id`,`name`, `system`), INDEX(`project_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE utf8mb4_general_ci;

