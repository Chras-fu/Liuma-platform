CREATE TABLE `page_module` (
  `id` varchar(50) NOT NULL COMMENT '页面模块id',
  `name` varchar(50) NOT NULL COMMENT '页面模块名称',
  `parent_id` varchar(50) NOT NULL COMMENT '父模块id',
  `project_id` varchar(50) NOT NULL COMMENT '所属项目id',
  `update_user`    varchar(50) NOT NULL COMMENT '更新人',
  `create_user` varchar(50) NOT NULL COMMENT '创建人',
  `create_time` bigint(13) NOT NULL COMMENT '创建时间',
  `update_time` bigint(13) NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`), UNIQUE(`project_id`, `name`), INDEX(`project_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE utf8mb4_general_ci;

CREATE TABLE `element` (
  `id` varchar(50) NOT NULL COMMENT '元素id',
  `num` bigint(10) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `name` varchar(100) NOT NULL COMMENT '元素名称',
  `module_id` varchar(50) NOT NULL COMMENT '所属模块id',
  `project_id` varchar(50) NOT NULL COMMENT '所属项目id',
  `by` varchar(20) NOT NULL COMMENT '定位方式',
  `expression` varchar(200) NOT NULL COMMENT '表达式',
  `description` varchar(200) DEFAULT NULL COMMENT '元素描述',
  `update_user` varchar(50) NOT NULL COMMENT '更新人',
  `create_user` varchar(50) NOT NULL COMMENT '创建人',
  `create_time` bigint(13) NOT NULL COMMENT '创建时间',
  `update_time` bigint(13) NOT NULL COMMENT '更新时间',
  `status` varchar(20) DEFAULT NULL COMMENT '状态',
  PRIMARY KEY (`id`), UNIQUE(`module_id`, `name`), UNIQUE(`num`), INDEX(`project_id`), INDEX(`module_id`)
) ENGINE = InnoDB AUTO_INCREMENT 10000 DEFAULT CHARSET = utf8mb4 COLLATE utf8mb4_general_ci;