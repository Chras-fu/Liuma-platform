ALTER TABLE api_module DROP INDEX project_id;
CREATE INDEX parent_id ON api_module(parent_id, `name`);

ALTER TABLE case_module DROP INDEX project_id;
CREATE INDEX parent_id ON case_module(parent_id, `name`);

ALTER TABLE page_module DROP INDEX project_id;
CREATE INDEX parent_id ON page_module(parent_id, `name`);