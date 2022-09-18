ALTER TABLE `case_api` ADD `description` varchar(200) DEFAULT NULL after `api_id`;
ALTER TABLE `collection` ADD `device_id` VARCHAR(50) DEFAULT NULL after `name`;
ALTER TABLE `plan` ADD `max_thread` int(8) DEFAULT NULL after `environment_id`;
ALTER TABLE `report` ADD `device_id` VARCHAR(50) DEFAULT NULL after `environment_id`;
ALTER TABLE `case` ADD `system` VARCHAR(20) DEFAULT NULL after `environment_ids`;

ALTER TABLE `function` DROP INDEX `name`;
ALTER TABLE `function` ADD UNIQUE `name`( `project_id`, `name` );

ALTER TABLE `case` ADD INDEX app_system(`system`);

ALTER TABLE `plan` MODIFY `environment_id` VARCHAR(50) DEFAULT NULL;
ALTER TABLE `report` MODIFY `environment_id` VARCHAR(50) DEFAULT NULL;

ALTER TABLE `element` MODIFY `expression` text;
ALTER TABLE `report_collection_case_api` MODIFY `exec_log` longtext;

UPDATE `plan` SET  `max_thread` = 5;
