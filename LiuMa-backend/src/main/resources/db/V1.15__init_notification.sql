ALTER TABLE `function` DROP INDEX `name`;
ALTER TABLE `function` ADD UNIQUE `name`( `project_id`, `name` );

ALTER TABLE `report_collection_case_api` MODIFY `exec_log` longtext;

CREATE TABLE `notification` (
  `id` varchar(50) NOT NULL COMMENT '通知id',
  `name` varchar(100) NOT NULL COMMENT '通知名称',
  `type` varchar(20) NOT NULL COMMENT '通知类型',
  `params` json NOT NULL COMMENT '配置参数',
  `webhook_url` varchar(500) NOT NULL COMMENT '地址url',
  `project_id` varchar(50) NOT NULL COMMENT '所属项目id',
  `update_user` varchar(50) NOT NULL COMMENT '更新人',
  `create_user` varchar(50) NOT NULL COMMENT '创建人',
  `create_time` bigint(13) NOT NULL COMMENT '创建时间',
  `update_time` bigint(13) NOT NULL COMMENT '更新时间',
  `status` varchar(20) DEFAULT NULL COMMENT '状态',
  PRIMARY KEY (`id`), INDEX(`project_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE utf8mb4_general_ci;