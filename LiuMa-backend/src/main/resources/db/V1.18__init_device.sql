CREATE TABLE `application` (
    `id`       VARCHAR(50) NOT NULL COMMENT '应用id',
	`name`	   VARCHAR(100) NOT NULL COMMENT '应用名称',
	`system` VARCHAR(20) NOT NULL COMMENT '所属系统',
	`app_id` VARCHAR(200) NOT NULL COMMENT '应用标识 安卓的包名 苹果的bundleId',
	`main_activity` VARCHAR(200) DEFAULT NULL COMMENT '应用主页',
	`description` VARCHAR(200) DEFAULT NULL COMMENT '应用描述',
    `project_id`  VARCHAR(50) NOT NULL COMMENT '所属项目id',
    `create_time` bigint(13)  NOT NULL COMMENT '创建时间',
	`update_time` bigint(13)  NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`), UNIQUE(`project_id`, `name`), INDEX(`project_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE utf8mb4_general_ci;

CREATE TABLE `device` (
  `id` varchar(50) NOT NULL COMMENT '设备id',
  `serial` varchar(50) NOT NULL COMMENT '设备序列号',
  `name` varchar(100) NOT NULL COMMENT '设备名称',
  `system` varchar(20) NOT NULL COMMENT '设备系统',
  `brand` varchar(20) NOT NULL COMMENT '设备品牌',
  `model` varchar(50) NOT NULL COMMENT '设备型号',
  `version` varchar(20) NOT NULL COMMENT '设备系统版本',
  `size` varchar(50) DEFAULT NULL COMMENT '设备屏幕尺寸',
  `sources` json DEFAULT NULL COMMENT '设备资源信息',
  `owner` varchar(50) NOT NULL COMMENT '设备拥有者',
  `user` varchar(50) DEFAULT NULL COMMENT '设备使用者',
  `agent` varchar(50) NOT NULL COMMENT '设备代理id',
  `timeout` int(8) DEFAULT 0 COMMENT '使用超时时间',
  `project_id` varchar(50) DEFAULT NULL COMMENT '设备使用者',
  `create_time` bigint(13) NOT NULL COMMENT '创建时间',
  `update_time` bigint(13) NOT NULL COMMENT '更新时间',
  `status` varchar(20) NOT NULL COMMENT '状态',
  PRIMARY KEY (`id`), UNIQUE(`serial`), INDEX(`system`), INDEX(`agent`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE utf8mb4_general_ci;


