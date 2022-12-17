CREATE TABLE `param_group` (
    `id`       VARCHAR(50) NOT NULL COMMENT '参数组id',
	`name`	   VARCHAR(50) NOT NULL COMMENT '参数组名',
    `param_type`  varchar(20) NOT NULL COMMENT '参数组类型',
    `description` varchar(200) DEFAULT NULL COMMENT '参数组描述',
	`project_id`   varchar(50) NOT NULL COMMENT '所属项目id',
	`update_user`    varchar(50) NOT NULL COMMENT '更新人',
	`create_user`    varchar(50) NOT NULL COMMENT '创建人',
    `create_time` bigint(13)  NOT NULL COMMENT '创建时间',
	`update_time` bigint(13)  NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`), UNIQUE(`project_id`, `name`), INDEX(`project_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE utf8mb4_general_ci;

CREATE TABLE `param_data` (
    `id`       VARCHAR(50) NOT NULL COMMENT '参数id',
	`name`	   VARCHAR(50) NOT NULL COMMENT '参数名',
    `param_data`  text NOT NULL COMMENT '参数值',
    `group_id`  VARCHAR(50) NOT NULL COMMENT '所属参数组',
    `data_type` varchar(20) NOT NULL COMMENT '数据类型',
    `description` varchar(200) DEFAULT NULL COMMENT '参数描述',
	`update_user`    varchar(50) NOT NULL COMMENT '更新人',
    `create_user`    varchar(50) NOT NULL COMMENT '创建人',
    `create_time` bigint(13)  NOT NULL COMMENT '创建时间',
	`update_time` bigint(13)  NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`), UNIQUE(`group_id`, `name`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE utf8mb4_general_ci;

CREATE TABLE `test_file` (
    `id`       VARCHAR(50) NOT NULL COMMENT '测试文件id',
	`name`	   VARCHAR(100) NOT NULL COMMENT '文件名称',
    `file_path` varchar(500) NOT NULL COMMENT '文件路径',
    `description` varchar(200) DEFAULT NULL COMMENT '文件描述',
    `project_id` varchar(50) NOT NULL COMMENT '所属项目id',
	`update_user`    varchar(50) NOT NULL COMMENT '更新人',
    `create_user`    varchar(50) NOT NULL COMMENT '创建人',
    `create_time` bigint(13)  NOT NULL COMMENT '创建时间',
	`update_time` bigint(13)  NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`), INDEX(`project_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE utf8mb4_general_ci;

CREATE TABLE `function` (
    `id`       VARCHAR(50) NOT NULL COMMENT '函数id',
	`name`	   VARCHAR(50) NOT NULL COMMENT '函数名',
    `from` varchar(20) NOT NULL COMMENT '函数来源',
    `param` json DEFAULT NULL COMMENT '入参定义',
    `code`  text DEFAULT NULL COMMENT '函数代码',
    `expression` varchar(150) NOT NULL COMMENT '调用表达式',
    `description` varchar(200) DEFAULT NULL COMMENT '函数描述',
    `project_id` varchar(50) NOT NULL COMMENT '所属项目id',
	`update_user`    varchar(50) NOT NULL COMMENT '更新人',
    `create_user`    varchar(50) NOT NULL COMMENT '创建人',
    `create_time` bigint(13)  NOT NULL COMMENT '创建时间',
	`update_time` bigint(13)  NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`), UNIQUE(`name`), INDEX(`project_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE utf8mb4_general_ci;

CREATE TABLE `operation` (
    `id`       VARCHAR(50) NOT NULL COMMENT '操作id',
	`name`	   VARCHAR(50) NOT NULL COMMENT '操作名',
	`type`	   VARCHAR(20) NOT NULL COMMENT '操作分类',
    `from`     VARCHAR(20) NOT NULL COMMENT '操作来源',
    `element` json DEFAULT NULL COMMENT '元素定义',
    `data` json DEFAULT NULL COMMENT '数据定义',
    `code`  text DEFAULT NULL COMMENT '操作代码',
    `description` varchar(200) DEFAULT NULL COMMENT '操作描述',
    `project_id` varchar(50) NOT NULL COMMENT '所属项目id',
	`update_user`    varchar(50) NOT NULL COMMENT '更新人',
    `create_user`    varchar(50) NOT NULL COMMENT '创建人',
    `create_time` bigint(13)  NOT NULL COMMENT '创建时间',
	`update_time` bigint(13)  NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`), UNIQUE(`project_id`,`name`), INDEX(`project_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE utf8mb4_general_ci;

insert into `param_group`(id, name, param_type, description, project_id, update_user, create_user, create_time, update_time)
VALUES (UUID(), 'Header', 'system', '接口请求头参数组', (select id from project where name = '演示项目'), 'system_admin_user', 'system_admin_user',
REPLACE(unix_timestamp(current_timestamp(3)),'.',''), REPLACE(unix_timestamp(current_timestamp(3)),'.',''));

insert into `param_group`(id, name, param_type, description, project_id, update_user, create_user, create_time, update_time)
VALUES (UUID(), 'Proxy', 'system', '接口请求代理参数组', (select id from project where name = '演示项目'), 'system_admin_user', 'system_admin_user',
REPLACE(unix_timestamp(current_timestamp(3)),'.',''), REPLACE(unix_timestamp(current_timestamp(3)),'.',''));

insert into `param_group`(id, name, param_type, description, project_id, update_user, create_user, create_time, update_time)
VALUES (UUID(), 'Custom', 'custom', '自定义参数组', (select id from project where name = '演示项目'), 'system_admin_user', 'system_admin_user',
REPLACE(unix_timestamp(current_timestamp(3)),'.',''), REPLACE(unix_timestamp(current_timestamp(3)),'.',''));
