CREATE TABLE `view_module` (
  `id` varchar(50) NOT NULL COMMENT '视图模块id',
  `name` varchar(50) NOT NULL COMMENT '视图模块名称',
  `parent_id` varchar(50) NOT NULL COMMENT '父模块id',
  `project_id` varchar(50) NOT NULL COMMENT '所属项目id',
  `update_user`    varchar(50) NOT NULL COMMENT '更新人',
  `create_user` varchar(50) NOT NULL COMMENT '创建人',
  `create_time` bigint(13) NOT NULL COMMENT '创建时间',
  `update_time` bigint(13) NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`), INDEX(`parent_id`, `name`), INDEX(`project_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE utf8mb4_general_ci;

CREATE TABLE `control` (
  `id` varchar(50) NOT NULL COMMENT '控件id',
  `num` bigint(10) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `name` varchar(100) NOT NULL COMMENT '控件名称',
  `system` varchar(20) NOT NULL COMMENT '所属系统',
  `module_id` varchar(50) NOT NULL COMMENT '所属模块id',
  `project_id` varchar(50) NOT NULL COMMENT '所属项目id',
  `by` varchar(20) NOT NULL COMMENT '定位方式',
  `expression` text NOT NULL COMMENT '表达式',
  `description` varchar(200) DEFAULT NULL COMMENT '控件描述',
  `update_user` varchar(50) NOT NULL COMMENT '更新人',
  `create_user` varchar(50) NOT NULL COMMENT '创建人',
  `create_time` bigint(13) NOT NULL COMMENT '创建时间',
  `update_time` bigint(13) NOT NULL COMMENT '更新时间',
  `status` varchar(20) DEFAULT NULL COMMENT '状态',
  PRIMARY KEY (`id`), UNIQUE(`module_id`, `name`, `system`), UNIQUE(`num`), INDEX(`project_id`), INDEX(`module_id`), INDEX(`system`)
) ENGINE = InnoDB AUTO_INCREMENT 10000 DEFAULT CHARSET = utf8mb4 COLLATE utf8mb4_general_ci;

CREATE TABLE `case_app` (
  `id` varchar(50) NOT NULL COMMENT '用例操作id',
  `index` int(8) NOT NULL COMMENT '操作序号',
  `case_id` varchar(100) NOT NULL COMMENT '用例id',
  `operation_id` varchar(50) NOT NULL COMMENT '操作id',
  `element` json DEFAULT NULL COMMENT '操作元素组',
  `data` json DEFAULT NULL COMMENT '操作数据组',
  PRIMARY KEY (`id`), INDEX(`case_id`), INDEX(`operation_id`), INDEX(`index`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE utf8mb4_general_ci;

CREATE TABLE `report_collection_case_app` (
    `id`       VARCHAR(50) NOT NULL COMMENT '报告集合用例操作id',
    `report_collection_case_id` VARCHAR(50) NOT NULL COMMENT '报告集合用例id',
    `case_app_index` int(8) NOT NULL COMMENT '用例操作序号',
    `operation_id` VARCHAR(50) NOT NULL COMMENT '操作id',
    `operation_name` VARCHAR(100) NOT NULL COMMENT '操作名称',
    `operation_element` text DEFAULT NULL COMMENT '操作元素',
	`screen_shot` json DEFAULT NULL COMMENT '操作截图地址',
	`exec_log` text DEFAULT NULL COMMENT '执行日志',
    `status` VARCHAR(10) NOT NULL COMMENT '执行结果',
    PRIMARY KEY (`id`), INDEX(`report_collection_case_id`), INDEX(`operation_id`), INDEX(`case_app_index`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE utf8mb4_general_ci;
