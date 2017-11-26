package com.chigix.resserver.mybatis.record;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class ChunkExampleExtending {

    public static class OffsetIndexInParent extends ChunkExample.Criterion {

        private final int value;

        private final String parentVersionId;

        public OffsetIndexInParent(int index, String parent_version_id, boolean include_this) {
            super("table_a.ID >" + (include_this ? "=" : "") + " (SELECT table_b.ID FROM CHUNK table_b "
                    + "WHERE table_b.\"index_in_parent\"= #{criterion.value} "
                    + "AND table_b.\"parent_version_id\"=#{criterion.parentVersionId})");
            this.value = index;
            this.parentVersionId = parent_version_id;
        }

        @Override
        public Object getValue() {
            return value;
        }

        public String getParentVersionId() {
            return parentVersionId;
        }

    }
}
