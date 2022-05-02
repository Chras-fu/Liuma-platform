CREATE TABLE `plan_schedule` (
    `id`       VARCHAR(50) NOT NULL COMMENT '定时任务id',
	`plan_id`	   VARCHAR(50) NOT NULL COMMENT '所属计划id',
	`start_time`   VARCHAR(50)  NOT NULL COMMENT '启动时间',
	`frequency`   VARCHAR(15)  NOT NULL COMMENT '执行频率',
	`next_run_time`	   bigint(13) NOT NULL COMMENT '下次执行时间',
    PRIMARY KEY (`id`), UNIQUE(`plan_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE utf8mb4_general_ci;
