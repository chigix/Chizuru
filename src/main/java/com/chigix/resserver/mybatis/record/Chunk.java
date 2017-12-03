package com.chigix.resserver.mybatis.record;

public class Chunk {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column CHUNK.ID
     *
     * @mbg.generated Thu Nov 23 23:18:14 JST 2017
     */
    private Integer id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column CHUNK.content_hash
     *
     * @mbg.generated Thu Nov 23 23:18:14 JST 2017
     */
    private String contentHash;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column CHUNK.size
     *
     * @mbg.generated Thu Nov 23 23:18:14 JST 2017
     */
    private Integer size;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column CHUNK.location_id
     *
     * @mbg.generated Thu Nov 23 23:18:14 JST 2017
     */
    private String locationId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column CHUNK.parent_version_id
     *
     * @mbg.generated Thu Nov 23 23:18:14 JST 2017
     */
    private String parentVersionId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column CHUNK.index_in_parent
     *
     * @mbg.generated Thu Nov 23 23:18:14 JST 2017
     */
    private String indexInParent;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column CHUNK.ID
     *
     * @return the value of CHUNK.ID
     *
     * @mbg.generated Thu Nov 23 23:18:14 JST 2017
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column CHUNK.ID
     *
     * @param id the value for CHUNK.ID
     *
     * @mbg.generated Thu Nov 23 23:18:14 JST 2017
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column CHUNK.content_hash
     *
     * @return the value of CHUNK.content_hash
     *
     * @mbg.generated Thu Nov 23 23:18:14 JST 2017
     */
    public String getContentHash() {
        return contentHash;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column CHUNK.content_hash
     *
     * @param contentHash the value for CHUNK.content_hash
     *
     * @mbg.generated Thu Nov 23 23:18:14 JST 2017
     */
    public void setContentHash(String contentHash) {
        this.contentHash = contentHash == null ? null : contentHash.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column CHUNK.size
     *
     * @return the value of CHUNK.size
     *
     * @mbg.generated Thu Nov 23 23:18:14 JST 2017
     */
    public Integer getSize() {
        return size;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column CHUNK.size
     *
     * @param size the value for CHUNK.size
     *
     * @mbg.generated Thu Nov 23 23:18:14 JST 2017
     */
    public void setSize(Integer size) {
        this.size = size;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column CHUNK.location_id
     *
     * @return the value of CHUNK.location_id
     *
     * @mbg.generated Thu Nov 23 23:18:14 JST 2017
     */
    public String getLocationId() {
        return locationId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column CHUNK.location_id
     *
     * @param locationId the value for CHUNK.location_id
     *
     * @mbg.generated Thu Nov 23 23:18:14 JST 2017
     */
    public void setLocationId(String locationId) {
        this.locationId = locationId == null ? null : locationId.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column CHUNK.parent_version_id
     *
     * @return the value of CHUNK.parent_version_id
     *
     * @mbg.generated Thu Nov 23 23:18:14 JST 2017
     */
    public String getParentVersionId() {
        return parentVersionId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column CHUNK.parent_version_id
     *
     * @param parentVersionId the value for CHUNK.parent_version_id
     *
     * @mbg.generated Thu Nov 23 23:18:14 JST 2017
     */
    public void setParentVersionId(String parentVersionId) {
        this.parentVersionId = parentVersionId == null ? null : parentVersionId.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column CHUNK.index_in_parent
     *
     * @return the value of CHUNK.index_in_parent
     *
     * @mbg.generated Thu Nov 23 23:18:14 JST 2017
     */
    public String getIndexInParent() {
        return indexInParent;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column CHUNK.index_in_parent
     *
     * @param indexInParent the value for CHUNK.index_in_parent
     *
     * @mbg.generated Thu Nov 23 23:18:14 JST 2017
     */
    public void setIndexInParent(String indexInParent) {
        this.indexInParent = indexInParent == null ? null : indexInParent.trim();
    }
}