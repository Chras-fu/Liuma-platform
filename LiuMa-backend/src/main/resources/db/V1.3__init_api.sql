CREATE TABLE `api_module` (
  `id` varchar(50) NOT NULL COMMENT '模块id',
  `name` varchar(50) NOT NULL COMMENT '模块名称',
  `parent_id` varchar(50) NOT NULL COMMENT '父模块id',
  `project_id` varchar(50) NOT NULL COMMENT '所属项目id',
  `update_user`    varchar(50) NOT NULL COMMENT '更新人',
  `create_user` varchar(50) NOT NULL COMMENT '创建人',
  `create_time` bigint(13) NOT NULL COMMENT '创建时间',
  `update_time` bigint(13) NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`), UNIQUE(`project_id`, `name`), INDEX(`project_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE utf8mb4_general_ci;

CREATE TABLE `api` (
  `id` varchar(50) NOT NULL COMMENT '接口id',
  `num` bigint(10) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `name` varchar(100) NOT NULL COMMENT '接口名称',
  `level` varchar(20) NOT NULL COMMENT '接口等级',
  `module_id` varchar(50) NOT NULL COMMENT '所属模块id',
  `project_id` varchar(50) NOT NULL COMMENT '所属项目id',
  `method` varchar(20) NOT NULL COMMENT '请求方法',
  `path` varchar(200) NOT NULL COMMENT '接口路径',
  `protocol` varchar(20) NOT NULL COMMENT '请求协议',
  `domain_sign` varchar(50) DEFAULT NULL COMMENT '域名标识',
  `description` varchar(200) DEFAULT NULL COMMENT '接口描述',
  `header` json DEFAULT NULL COMMENT '请求头',
  `body` json DEFAULT NULL COMMENT '请求体',
  `query` json DEFAULT NULL COMMENT '查询参数',
  `rest` json DEFAULT NULL COMMENT '路径参数',
  `update_user`    varchar(50) NOT NULL COMMENT '更新人',
  `create_user` varchar(50) NOT NULL COMMENT '创建人',
  `create_time` bigint(13) NOT NULL COMMENT '创建时间',
  `update_time` bigint(13) NOT NULL COMMENT '更新时间',
  `status` varchar(20) DEFAULT NULL COMMENT '状态',
  PRIMARY KEY (`id`), INDEX(`project_id`), INDEX(`module_id`), UNIQUE(`num`)
) ENGINE = InnoDB AUTO_INCREMENT 10000 DEFAULT CHARSET = utf8mb4 COLLATE utf8mb4_general_ci;