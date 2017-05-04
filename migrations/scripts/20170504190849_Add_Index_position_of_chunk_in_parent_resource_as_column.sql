--
--    Copyright 2010-2016 the original author or authors.
--
--    Licensed under the Apache License, Version 2.0 (the "License");
--    you may not use this file except in compliance with the License.
--    You may obtain a copy of the License at
--
--       http://www.apache.org/licenses/LICENSE-2.0
--
--    Unless required by applicable law or agreed to in writing, software
--    distributed under the License is distributed on an "AS IS" BASIS,
--    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
--    See the License for the specific language governing permissions and
--    limitations under the License.
--

-- // Add Index position of chunk in parent resource as column
-- Migration SQL that makes the change goes here.

ALTER TABLE CHUNK ADD INDEX_IN_PARENT VARCHAR(12) NOT NULL;
CREATE INDEX CHUNK_IN_PARENT_IDX ON CHUNK(PARENT_VERSION_ID, INDEX_IN_PARENT);
UPDATE CHIZURU SET "value" = '1.0.0-beta4' WHERE "key" = 'SERVER_VERSION';

-- //@UNDO
-- SQL to undo the change goes here.

ALTER TABLE CHUNK DROP INDEX CHUNK_IN_PARENT_IDX;
ALTER TABLE CHUNK DROP COLUMN INDEX_IN_PARENT;
UPDATE CHIZURU SET value = '1.0.0-beta3' WHERE key = 'SERVER_VERSION';
