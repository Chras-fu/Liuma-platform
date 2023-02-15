ALTER TABLE `case_api` ADD `description` varchar(200) DEFAULT NULL after `api_id`;
ALTER TABLE `case_web` ADD `description` varchar(200) DEFAULT NULL after `operation_id`;
ALTER TABLE `case_app` ADD `description` varchar(200) DEFAULT NULL after `operation_id`;
ALTER TABLE `report_collection_case_api` ADD `description` varchar(200) DEFAULT NULL after `api_path`;
ALTER TABLE `report_collection_case_web` ADD `description` varchar(200) DEFAULT NULL after `operation_element`;
ALTER TABLE `report_collection_case_app` ADD `description` varchar(200) DEFAULT NULL after `operation_element`;

ALTER TABLE `collection` ADD `device_id` VARCHAR(50) DEFAULT NULL after `name`;
ALTER TABLE `plan` ADD `max_thread` int(8) DEFAULT NULL after `environment_id`;
ALTER TABLE `report` ADD `device_id` VARCHAR(50) DEFAULT NULL after `environment_id`;
ALTER TABLE `case` ADD `system` VARCHAR(20) DEFAULT NULL after `environment_ids`;
ALTER TABLE `sum_statistics` ADD `app_case_total` int(8) DEFAULT 0 after `web_case_new_week`;
ALTER TABLE `sum_statistics` ADD `app_case_new_week` int(8) DEFAULT 0 after `app_case_total`;
ALTER TABLE `daily_statistics` ADD `app_case_new` int(8) DEFAULT 0 after `web_case_new`;
ALTER TABLE `daily_statistics` ADD `app_case_sum` int(8) DEFAULT 0 after `web_case_sum`;
ALTER TABLE `daily_statistics` ADD `app_case_run` int(8) DEFAULT 0 after `web_case_run`;
ALTER TABLE `daily_statistics` ADD `app_case_pass_rate` float DEFAULT 0 after `web_case_pass_rate`;


ALTER TABLE `sum_statistics` DROP `plan_run_total`;
ALTER TABLE `sum_statistics` DROP `plan_run_today`;

ALTER TABLE `function` DROP INDEX `name`;
ALTER TABLE `function` ADD UNIQUE `name`( `project_id`, `name` );

ALTER TABLE `project` ADD UNIQUE `name`( `name` );

ALTER TABLE `case` ADD INDEX app_system(`system`);

ALTER TABLE `plan` MODIFY `environment_id` VARCHAR(50) DEFAULT NULL;
ALTER TABLE `report` MODIFY `environment_id` VARCHAR(50) DEFAULT NULL;

ALTER TABLE `element` MODIFY `expression` text;
ALTER TABLE `report_collection_case_api` MODIFY `exec_log` longtext;

UPDATE `plan` SET  `max_thread` = 5;
