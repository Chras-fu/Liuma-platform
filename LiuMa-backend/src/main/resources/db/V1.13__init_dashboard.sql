CREATE TABLE `daily_statistics` (
    `id`       VARCHAR(50) NOT NULL COMMENT '主键id',
	`stat_date` date NOT NULL COMMENT '统计日期',
    `project_id`  VARCHAR(50) NOT NULL COMMENT '所属项目id',
    `api_case_new` int(8)  NOT NULL COMMENT 'API用例当日新增',
    `web_case_new` int(8)  NOT NULL COMMENT 'WEB用例当日新增',
    `api_case_sum` int(8)  NOT NULL COMMENT 'API用例当日总数',
    `web_case_sum` int(8)  NOT NULL COMMENT 'WEB用例当日总数',
    `api_case_run` int(8)  NOT NULL COMMENT 'API用例当日执行',
    `web_case_run` int(8)  NOT NULL COMMENT 'WEB用例当日执行',
    `api_case_pass_rate` float  NOT NULL COMMENT 'API用例当日通过率',
    `web_case_pass_rate` float  NOT NULL COMMENT 'WEB用例当日通过率',
    PRIMARY KEY(`stat_date`, `project_id`), KEY (`id`), INDEX(`stat_date`), INDEX(`project_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE utf8mb4_general_ci;

CREATE TABLE `sum_statistics` (
    `id`       VARCHAR(50) NOT NULL COMMENT '主键id',
    `project_id`  VARCHAR(50) NOT NULL COMMENT '所属项目id',
    `api_case_total` int(8)  NOT NULL COMMENT 'API用例总数',
    `api_case_new_week` int(8)  NOT NULL COMMENT 'API用例当周新增',
    `web_case_total` int(8)  NOT NULL COMMENT 'WEB用例总数',
    `web_case_new_week` int(8)  NOT NULL COMMENT 'WEB用例当周新增',
    `plan_run_total` int(8)  NOT NULL COMMENT '计划执行总数',
    `plan_run_today` int(8)  NOT NULL COMMENT '计划当日执行数',
    `case_run_total` int(8)  NOT NULL COMMENT '用例执行总数',
    `case_run_today` int(8)  NOT NULL COMMENT '用例当日执行数',
    `plan_run_week_top` json  NOT NULL COMMENT '计划执行top10',
    `case_fail_week_top` json  NOT NULL COMMENT '用例失败top10',
    PRIMARY KEY(`project_id`), KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE utf8mb4_general_ci;