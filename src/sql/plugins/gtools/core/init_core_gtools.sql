-- liquibase formatted sql
-- changeset gtools:init_core_gtools.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM core_datastore WHERE entity_key IN ('gtools.site_property.analytics.code','gtools.site_property.webmaster_tools.code')
INSERT INTO core_datastore ( entity_key, entity_value ) VALUES ('gtools.site_property.analytics.code', '<Your Google Analytics Code>');
INSERT INTO core_datastore ( entity_key, entity_value ) VALUES ('gtools.site_property.webmaster_tools.code', '<Your Webmaster Tools Code>');

