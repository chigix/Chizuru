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

-- // First migration.
-- Migration SQL that makes the change goes here.

CREATE TABLE BUCKET (
    ID INTEGER AUTO_INCREMENT NOT NULL PRIMARY KEY,
    UUID CHAR(32) NOT NULL UNIQUE,
    "NAME" VARCHAR(64) NOT NULL UNIQUE, 
    CREATION_TIME CHAR(24) NOT NULL
);

CREATE TABLE CHIZURU (
    ID INTEGER AUTO_INCREMENT NOT NULL PRIMARY KEY,
    "key" VARCHAR(255) NOT NULL UNIQUE,
    "value" VARCHAR(255)
);

INSERT INTO CHIZURU ("key", "value") 
	VALUES ('NODE_ID', NULL);
INSERT INTO CHIZURU ("key", "value") 
	VALUES ('CREATION_DATE', NULL);
INSERT INTO CHIZURU ("key", "value") 
	VALUES ('MAX_CHUNKSIZE', NULL);

CREATE TABLE CHUNK (
    ID INTEGER AUTO_INCREMENT NOT NULL PRIMARY KEY,
    CONTENT_HASH CHAR(64) NOT NULL,
    "SIZE" INTEGER DEFAULT 0  NOT NULL,
    LOCATION_ID CHAR(36) NOT NULL,
    PARENT_VERSION_ID CHAR(36) DEFAULT '0'  NOT NULL
);
CREATE INDEX CHUNK_KEY_IDX ON CHUNK(CONTENT_HASH);
CREATE INDEX VERSION_IDX ON CHUNK(PARENT_VERSION_ID);

CREATE TABLE RESOURCE (
    ID INTEGER AUTO_INCREMENT NOT NULL PRIMARY KEY,
    "key" VARCHAR(1024), 
    "keyhash" CHAR(32) DEFAULT '0000000000000000000000000000000'  NOT NULL UNIQUE,
    "bucket_uuid" CHAR(32) DEFAULT '0000000000000000000000000000000'  NOT NULL,
    "type" VARCHAR(50), 
    "etag" char(32) DEFAULT 'd41d8cd98f00b204e9800998ecf8427e'  NOT NULL,
    "last_modified" CHAR(24) DEFAULT '1970-01-01T00:00:00.000Z' NOT NULL,
    "size" VARCHAR(255) DEFAULT '0' NOT NULL,
    "storage_class" VARCHAR(255) DEFAULT 'STANDARD' NOT NULL,
    "version_id" CHAR(36) DEFAULT '0' NOT NULL,
    "meta_data" CLOB NOT NULL
);
CREATE INDEX RESOURCE_KEY_IDX ON RESOURCE("key");

-- //@UNDO
-- SQL to undo the change goes here.
DROP TABLE RESOURCE;
DROP TABLE CHUNK;
DROP TABLE CHIZURU;
DROP TABLE BUCKET;
