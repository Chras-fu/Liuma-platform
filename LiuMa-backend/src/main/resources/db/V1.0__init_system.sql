CREATE TABLE `user` (
    `id`       VARCHAR(50) NOT NULL COMMENT '用户id',
	`username`	   VARCHAR(50) NOT NULL COMMENT '用户名',
    `account`  varchar(50) NOT NULL COMMENT '登录账号',
    `password` varchar(50) NOT NULL COMMENT '登录密码',
	`mobile`   bigint(11) NOT NULL COMMENT '手机号',
	`email`    varchar(50) NOT NULL COMMENT '邮箱',
	`last_project` varchar(50) DEFAULT NULL COMMENT '最后登录项目id',
    `create_time` bigint(13)  NOT NULL COMMENT '创建时间',
	`update_time` bigint(13)  NOT NULL COMMENT '更新时间',
    `status`   varchar(10) DEFAULT NULL COMMENT '状态',
    PRIMARY KEY (`id`), UNIQUE(`account`), UNIQUE(`mobile`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE utf8mb4_general_ci;

CREATE TABLE `project` (
    `id`       VARCHAR(50) NOT NULL COMMENT '项目id',
	`name`	   VARCHAR(100) NOT NULL COMMENT '项目名',
    `description`  varchar(200) DEFAULT NULL COMMENT '项目描述',
    `project_admin` varchar(50) NOT NULL COMMENT '项目管理员',
    `create_time` bigint(13)  NOT NULL COMMENT '创建时间',
	`update_time` bigint(13)  NOT NULL COMMENT '更新时间',
    `status`   varchar(10) DEFAULT NULL COMMENT '状态',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE utf8mb4_general_ci;

CREATE TABLE `permission` (
    `id`       VARCHAR(50) NOT NULL COMMENT '权限id',
    `name`  varchar(100) NOT NULL COMMENT '权限名称',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE utf8mb4_general_ci;

CREATE TABLE `role` (
    `id`       VARCHAR(50) NOT NULL COMMENT '角色id',
	`name`	   VARCHAR(50) NOT NULL COMMENT '角色名',
    `project_id`  varchar(50) NOT NULL COMMENT '项目id',
    `create_time` bigint(13)  NOT NULL COMMENT '创建时间',
	`update_time` bigint(13)  NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`), UNIQUE(`name`, `project_id`), INDEX(project_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE utf8mb4_general_ci;

CREATE TABLE `role_permission` (
    `id`       VARCHAR(50) NOT NULL COMMENT '角色权限id',
	`role_id`	   VARCHAR(50) NOT NULL COMMENT '角色id',
    `permission_id`  varchar(50) NOT NULL COMMENT '权限id',
    `create_time` bigint(13)  NOT NULL COMMENT '创建时间',
	`update_time` bigint(13)  NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`), UNIQUE(`role_id`, `permission_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE utf8mb4_general_ci;

CREATE TABLE `user_role` (
    `id`       VARCHAR(50) NOT NULL COMMENT '用户角色id',
	`user_id`	   VARCHAR(50) NOT NULL COMMENT '用户id',
    `role_id`  varchar(50) NOT NULL COMMENT '角色id',
    `create_time` bigint(13)  NOT NULL COMMENT '创建时间',
	`update_time` bigint(13)  NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`), UNIQUE(`user_id`, `role_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE utf8mb4_general_ci;

CREATE TABLE `user_project` (
    `id`       VARCHAR(50) NOT NULL COMMENT '用户项目id',
	`user_id`	   VARCHAR(50) NOT NULL COMMENT '用户id',
    `project_id`  varchar(50) NOT NULL COMMENT '项目id',
    `create_time` bigint(13)  NOT NULL COMMENT '创建时间',
	`update_time` bigint(13)  NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`), UNIQUE(`user_id`, `project_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE utf8mb4_general_ci;

insert into `permission` (id, name) values
('NORMAL_MENU', '平台常规菜单'),
('USER_MENU', '用户管理菜单'),
('ROLE_MENU', '角色管理菜单'),
('PROJECT_MENU', '项目管理菜单'),
('SETTING_MENU', '配置中心菜单'),
('SETTING_OPT', '配置中心操作');

insert into `user`(id, username, account, password, mobile, email, create_time, update_time)
values
("system_admin_user", '系统管理员', 'LMadmin', 'TGl1bWFAMTIzNDU2', '10000000000', 'test@163.com', REPLACE(unix_timestamp(current_timestamp(3)),'.',''),REPLACE(unix_timestamp(current_timestamp(3)),'.','')),
(UUID(), '演示用户', 'demo', 'MTIzNDU2', '10000000001', 'test@163.com', REPLACE(unix_timestamp(current_timestamp(3)),'.',''),REPLACE(unix_timestamp(current_timestamp(3)),'.',''));

insert into `project`(id, name, description, project_admin, create_time, update_time, `status`)
VALUES
(UUID(), '演示项目', '演示项目', (select id from `user` where account = 'demo'), REPLACE(unix_timestamp(current_timestamp(3)),'.',''), REPLACE(unix_timestamp(current_timestamp(3)),'.',''), 'Normal');

insert into `role`(id, name, project_id, create_time, update_time)
values
('system_admin_role', '系统管理员', 'system', REPLACE(unix_timestamp(current_timestamp(3)),'.',''), REPLACE(unix_timestamp(current_timestamp(3)),'.','')),
(UUID(), '项目管理员', (select id from project where name = '演示项目'), REPLACE(unix_timestamp(current_timestamp(3)),'.',''), REPLACE(unix_timestamp(current_timestamp(3)),'.','')),
(UUID(), '项目普通用户', (select id from project where name = '演示项目'), REPLACE(unix_timestamp(current_timestamp(3)),'.',''), REPLACE(unix_timestamp(current_timestamp(3)),'.',''));

insert into `role_permission`(id, role_id, permission_id, create_time, update_time)
values
(UUID(), 'system_admin_role', 'NORMAL_MENU', REPLACE(unix_timestamp(current_timestamp(3)),'.',''), REPLACE(unix_timestamp(current_timestamp(3)),'.','')),
(UUID(), 'system_admin_role', 'USER_MENU', REPLACE(unix_timestamp(current_timestamp(3)),'.',''), REPLACE(unix_timestamp(current_timestamp(3)),'.','')),
(UUID(), 'system_admin_role', 'ROLE_MENU', REPLACE(unix_timestamp(current_timestamp(3)),'.',''), REPLACE(unix_timestamp(current_timestamp(3)),'.','')),
(UUID(), 'system_admin_role', 'PROJECT_MENU', REPLACE(unix_timestamp(current_timestamp(3)),'.',''), REPLACE(unix_timestamp(current_timestamp(3)),'.','')),
(UUID(), 'system_admin_role', 'SETTING_MENU', REPLACE(unix_timestamp(current_timestamp(3)),'.',''), REPLACE(unix_timestamp(current_timestamp(3)),'.','')),
(UUID(), 'system_admin_role', 'SETTING_OPT', REPLACE(unix_timestamp(current_timestamp(3)),'.',''), REPLACE(unix_timestamp(current_timestamp(3)),'.','')),
(UUID(), (select id from role where name = '项目管理员'), 'NORMAL_MENU', REPLACE(unix_timestamp(current_timestamp(3)),'.',''), REPLACE(unix_timestamp(current_timestamp(3)),'.','')),
(UUID(), (select id from role where name = '项目管理员'), 'USER_MENU', REPLACE(unix_timestamp(current_timestamp(3)),'.',''), REPLACE(unix_timestamp(current_timestamp(3)),'.','')),
(UUID(), (select id from role where name = '项目管理员'), 'ROLE_MENU', REPLACE(unix_timestamp(current_timestamp(3)),'.',''), REPLACE(unix_timestamp(current_timestamp(3)),'.','')),
(UUID(), (select id from role where name = '项目管理员'), 'SETTING_MENU', REPLACE(unix_timestamp(current_timestamp(3)),'.',''), REPLACE(unix_timestamp(current_timestamp(3)),'.','')),
(UUID(), (select id from role where name = '项目管理员'), 'SETTING_OPT', REPLACE(unix_timestamp(current_timestamp(3)),'.',''), REPLACE(unix_timestamp(current_timestamp(3)),'.','')),
(UUID(), (select id from role where name = '项目普通用户'), 'NORMAL_MENU', REPLACE(unix_timestamp(current_timestamp(3)),'.',''), REPLACE(unix_timestamp(current_timestamp(3)),'.','')),
(UUID(), (select id from role where name = '项目普通用户'), 'SETTING_MENU', REPLACE(unix_timestamp(current_timestamp(3)),'.',''), REPLACE(unix_timestamp(current_timestamp(3)),'.',''));

insert into `user_role`(id, user_id, role_id, create_time, update_time)
VALUES
(UUID(), 'system_admin_user', 'system_admin_role', REPLACE(unix_timestamp(current_timestamp(3)),'.',''), REPLACE(unix_timestamp(current_timestamp(3)),'.','')),
(UUID(), (select id from `user` where account = 'demo'), (select id from `role` where name = '项目管理员'), REPLACE(unix_timestamp(current_timestamp(3)),'.',''), REPLACE(unix_timestamp(current_timestamp(3)),'.',''));

insert into `user_project`(id, user_id, project_id, create_time, update_time)
VALUES
(UUID(), (select id from `user` where account = 'demo'), (select id from project where name = '演示项目'), REPLACE(unix_timestamp(current_timestamp(3)),'.',''), REPLACE(unix_timestamp(current_timestamp(3)),'.',''));

update `user` set last_project=(select id from project where name = '演示项目') where account in ('LMadmin', 'demo');