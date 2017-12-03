package com.chigix.resserver.mybatis.record;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class ResourceExampleExtending {

    public static class VersionIdOffset extends ResourceExample.Criterion {

        private final String value;

        public VersionIdOffset(String version_id, boolean include_this) {
            super("table_a.ID >" + (include_this ? "=" : "") + " (SELECT table_b.ID FROM resource table_b WHERE table_b.\"version_id\"= #{criterion.value} LIMIT 1)");
            value = version_id;
        }

        @Override
        public Object getValue() {
            return value;
        }

    }

    public static class KeyhashOffset extends ResourceExample.Criterion {

        private final String value;

        public KeyhashOffset(String keyhash, boolean include_this) {
            super("table_a.ID >" + (include_this ? "=" : "") + " (SELECT table_b.ID FROM resource table_b WHERE table_b.\"keyhash\"= #{criterion.value} LIMIT 1)");
            value = keyhash;
        }

        @Override
        public String getValue() {
            return value;
        }

    }

}
