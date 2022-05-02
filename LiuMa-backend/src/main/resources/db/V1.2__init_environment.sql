CREATE TABLE `environment` (
    `id`       VARCHAR(50) NOT NULL COMMENT '环境id',
	`name`	   VARCHAR(50) NOT NULL COMMENT '环境名称',
    `description` varchar(200) DEFAULT NULL COMMENT '环境描述',
	`project_id`   varchar(50) NOT NULL COMMENT '所属项目id',
	`update_user`    varchar(50) NOT NULL COMMENT '更新人',
	`create_user`    varchar(50) NOT NULL COMMENT '创建人',
    `create_time` bigint(13)  NOT NULL COMMENT '创建时间',
	`update_time` bigint(13)  NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`), UNIQUE(`project_id`, `name`), INDEX(`project_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE utf8mb4_general_ci;

CREATE TABLE `domain_sign` (
    `id`       VARCHAR(50) NOT NULL COMMENT '域名标识id',
	`name`	   VARCHAR(20) NOT NULL COMMENT '域名标识名称',
    `description`  VARCHAR(200) DEFAULT NULL COMMENT '域名标识描述',
    `project_id`  VARCHAR(50) NOT NULL COMMENT '所属项目id',
    `create_time` bigint(13)  NOT NULL COMMENT '创建时间',
	`update_time` bigint(13)  NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`), UNIQUE(`project_id`, `name`), INDEX(`project_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE utf8mb4_general_ci;

CREATE TABLE `domain` (
    `id`       VARCHAR(50) NOT NULL COMMENT '域名id',
    `domain_key_type`	   VARCHAR(50) NOT NULL COMMENT '域名标识类型',
	`domain_key`	   VARCHAR(100) NOT NULL COMMENT '域名标识',
    `domain_data`  VARCHAR(200) NOT NULL COMMENT '域名值',
    `environment_id`  VARCHAR(50) NOT NULL COMMENT '所属环境id',
	`update_user`    varchar(50) NOT NULL COMMENT '更新人',
    `create_user`    varchar(50) NOT NULL COMMENT '创建人',
    `create_time` bigint(13)  NOT NULL COMMENT '创建时间',
	`update_time` bigint(13)  NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`), UNIQUE(`environment_id`, `domain_key`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE utf8mb4_general_ci;

CREATE TABLE `engine` (
    `id`       VARCHAR(50) NOT NULL COMMENT '引擎id',
	`name`	   VARCHAR(100) NOT NULL COMMENT '引擎名称',
    `engine_type` varchar(20) NOT NULL COMMENT '引擎类型',
    `secret` varchar(50) NOT NULL COMMENT '引擎秘钥',
    `status` varchar(50) NOT NULL COMMENT '引擎状态',
    `last_heartbeat_time` bigint(13)  DEFAULT NULL COMMENT '上次心跳时间',
    `project_id` varchar(50) NOT NULL COMMENT '所属项目',
	`update_user`    varchar(50) NOT NULL COMMENT '更新人',
    `create_user`    varchar(50) NOT NULL COMMENT '创建人',
    `create_time` bigint(13)  NOT NULL COMMENT '创建时间',
	`update_time` bigint(13)  NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`), UNIQUE(`project_id`, `name`), INDEX(`project_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE utf8mb4_general_ci;

insert into `engine` (id, name, engine_type, secret, status, last_heartbeat_time, project_id, update_user, create_user, create_time, update_time)
VALUES (REPLACE(UUID(), '-', ''), '系统引擎4', 'system', REPLACE(UUID(), '-', ''), 'offline', NULL, 'system', 'system_admin_user', 'system_admin_user',
REPLACE(unix_timestamp(current_timestamp(3)),'.',''), REPLACE(unix_timestamp(current_timestamp(3)),'.',''));

insert into `engine` (id, name, engine_type, secret, status, last_heartbeat_time, project_id, update_user, create_user, create_time, update_time)
VALUES (REPLACE(UUID(), '-', ''), '系统引擎3', 'system', REPLACE(UUID(), '-', ''), 'offline', NULL, 'system', 'system_admin_user', 'system_admin_user',
REPLACE(unix_timestamp(current_timestamp(3)),'.',''), REPLACE(unix_timestamp(current_timestamp(3)),'.',''));

insert into `engine` (id, name, engine_type, secret, status, last_heartbeat_time, project_id, update_user, create_user, create_time, update_time)
VALUES (REPLACE(UUID(), '-', ''), '系统引擎2', 'system', REPLACE(UUID(), '-', ''), 'offline', NULL, 'system', 'system_admin_user', 'system_admin_user',
REPLACE(unix_timestamp(current_timestamp(3)),'.',''), REPLACE(unix_timestamp(current_timestamp(3)),'.',''));

insert into `engine` (id, name, engine_type, secret, status, last_heartbeat_time, project_id, update_user, create_user, create_time, update_time)
VALUES (REPLACE(UUID(), '-', ''), '系统引擎1', 'system', REPLACE(UUID(), '-', ''), 'offline', NULL, 'system', 'system_admin_user', 'system_admin_user',
REPLACE(unix_timestamp(current_timestamp(3)),'.',''), REPLACE(unix_timestamp(current_timestamp(3)),'.',''));
