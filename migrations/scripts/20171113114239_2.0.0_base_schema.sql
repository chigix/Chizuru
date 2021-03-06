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

-- // 2.0.0 base schema
-- Migration SQL that makes the change goes here.

CREATE TABLE BUCKET (
    ID INTEGER AUTO_INCREMENT NOT NULL PRIMARY KEY,
    "uuid" CHAR(32) NOT NULL UNIQUE,
    "name" VARCHAR(64) NOT NULL UNIQUE, 
    "creation_time" CHAR(24) NOT NULL
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
INSERT INTO CHIZURU ("key", "value") 
    VALUES ('TRANSFER_BUFFERSIZE', NULL);
INSERT INTO CHIZURU ("key", "value") 
    VALUES ('SERVER_VERSION', '2.0.0');

CREATE TABLE CHUNK (
    ID INTEGER AUTO_INCREMENT NOT NULL PRIMARY KEY,
    "content_hash" CHAR(64) NOT NULL,
    "size" INTEGER DEFAULT 0  NOT NULL,
    "location_id" CHAR(36) NOT NULL,
    "parent_version_id" CHAR(36) DEFAULT '0'  NOT NULL,
    "index_in_parent" VARCHAR(12) NOT NULL
);
CREATE INDEX CHUNK_KEY_IDX ON CHUNK("content_hash");
CREATE INDEX VERSION_IDX ON CHUNK("parent_version_id");
CREATE INDEX CHUNK_IN_PARENT_IDX ON CHUNK("parent_version_id", "index_in_parent");

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
CREATE INDEX RESOURCE_KEYHASH_IDX ON RESOURCE("keyhash");

CREATE TABLE SUBRESOURCE (
    ID INTEGER AUTO_INCREMENT NOT NULL PRIMARY KEY,
    "parent_version_id" CHAR(36) DEFAULT '0000000000000000000000000000000' NOT NULL, 
    "type" VARCHAR(50), 
    "etag" char(32) DEFAULT 'd41d8cd98f00b204e9800998ecf8427e'  NOT NULL,
    "last_modified" CHAR(24) DEFAULT '1970-01-01T00:00:00.000Z' NOT NULL,
    "size" VARCHAR(255) DEFAULT '0' NOT NULL,
    "storage_class" VARCHAR(255) DEFAULT 'STANDARD' NOT NULL,
    "version_id" CHAR(36) DEFAULT '0' NOT NULL,
    "index_in_parent" VARCHAR(12) NOT NULL,
    "range_start_byte" INTEGER DEFAULT 0 NOT NULL,
    "range_end_byte" INTEGER DEFAULT 0 NOT NULL,
    "range_start_4byte" INTEGER DEFAULT 0 NOT NULL,
    "range_end_4byte" INTEGER DEFAULT 0 NOT NULL
);
CREATE INDEX SUBRESOURCE_PARENT_IDX ON SUBRESOURCE("parent_version_id");
CREATE INDEX SUBRESOURCE_ETAG_IDX ON SUBRESOURCE("etag");
CREATE INDEX SUBRESOURCE_VERSION_IDX ON SUBRESOURCE("version_id");
CREATE INDEX SUBRESOURCE_IN_PARENT_IDX ON SUBRESOURCE("index_in_parent");
CREATE INDEX AMASSRES_RANGE_BYTE_IDX ON SUBRESOURCE("range_start_byte", "range_end_byte");
CREATE INDEX AMASSRES_RANGE_4BYTE_IDX ON SUBRESOURCE("range_start_4byte", "range_end_4byte");

-- //@UNDO
-- SQL to undo the change goes here.

DROP TABLE SUBRESOURCE;
DROP TABLE RESOURCE;
DROP TABLE CHUNK;
DROP TABLE CHIZURU;
DROP TABLE BUCKET;
