ALTER TABLE `function` DROP INDEX `name`;
ALTER TABLE `function` ADD UNIQUE `name`( `project_id`, `name` );

ALTER TABLE `report_collection_case_api` MODIFY `exec_log` longtext;

