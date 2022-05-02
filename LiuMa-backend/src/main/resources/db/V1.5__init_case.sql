CREATE TABLE `case_module` (
  `id` varchar(50) NOT NULL COMMENT '模块id',
  `name` varchar(50) NOT NULL COMMENT '模块名称',
  `parent_id` varchar(50) NOT NULL COMMENT '父模块id',
  `project_id` varchar(50) NOT NULL COMMENT '所属项目id',
  `update_user` varchar(50) NOT NULL COMMENT '更新人',
  `create_user` varchar(50) NOT NULL COMMENT '创建人',
  `create_time` bigint(13) NOT NULL COMMENT '创建时间',
  `update_time` bigint(13) NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`), UNIQUE(`project_id`, `name`), INDEX(`project_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE utf8mb4_general_ci;

CREATE TABLE `case` (
  `id` varchar(50) NOT NULL COMMENT '用例id',
  `num` bigint(10) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `name` varchar(100) NOT NULL COMMENT '用例名称',
  `level` varchar(20) NOT NULL COMMENT '用例等级',
  `module_id` varchar(50) NOT NULL COMMENT '所属模块id',
  `project_id` varchar(50) NOT NULL COMMENT '所属项目id',
  `type` varchar(20) NOT NULL COMMENT '用例类型',
  `third_party` varchar(50) DEFAULT NULL COMMENT '第三方标识',
  `description` varchar(200) DEFAULT NULL COMMENT '用例描述',
  `environment_ids` varchar(1000) DEFAULT NULL COMMENT '环境标签',
  `common_param` json DEFAULT NULL COMMENT '公共参数',
  `update_user` varchar(50) NOT NULL COMMENT '更新人',
  `create_user` varchar(50) NOT NULL COMMENT '创建人',
  `create_time` bigint(13) NOT NULL COMMENT '创建时间',
  `update_time` bigint(13) NOT NULL COMMENT '更新时间',
  `status` varchar(20) DEFAULT NULL COMMENT '状态',
  PRIMARY KEY (`id`), UNIQUE(`num`), INDEX(`project_id`), INDEX(`module_id`)
) ENGINE = InnoDB AUTO_INCREMENT 10000 DEFAULT CHARSET = utf8mb4 COLLATE utf8mb4_general_ci;

CREATE TABLE `case_api` (
  `id` varchar(50) NOT NULL COMMENT '用例接口id',
  `index` int(8) NOT NULL COMMENT '接口序号',
  `case_id` varchar(50) NOT NULL COMMENT '用例id',
  `api_id` varchar(50) NOT NULL COMMENT '接口id',
  `header` json DEFAULT NULL COMMENT '请求头',
  `body` json DEFAULT NULL COMMENT '请求体',
  `query` json DEFAULT NULL COMMENT '查询参数',
  `rest` json DEFAULT NULL COMMENT '路径参数',
  `assertion` json DEFAULT NULL COMMENT '断言',
  `relation` json DEFAULT NULL COMMENT '关联参数',
  `controller` json DEFAULT NULL COMMENT '逻辑控制器',
  PRIMARY KEY (`id`), INDEX(`case_id`), INDEX(`api_id`), INDEX(`index`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE utf8mb4_general_ci;

CREATE TABLE `case_web` (
  `id` varchar(50) NOT NULL COMMENT '用例操作id',
  `index` int(8) NOT NULL COMMENT '操作序号',
  `case_id` varchar(100) NOT NULL COMMENT '用例id',
  `operation_id` varchar(50) NOT NULL COMMENT '操作id',
  `element` json DEFAULT NULL COMMENT '操作元素组',
  `data` json DEFAULT NULL COMMENT '操作数据组',
  PRIMARY KEY (`id`), INDEX(`case_id`), INDEX(`operation_id`), INDEX(`index`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE utf8mb4_general_ci;