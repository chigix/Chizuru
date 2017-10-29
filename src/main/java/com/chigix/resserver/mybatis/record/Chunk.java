package com.chigix.resserver.mybatis.record;

public class Chunk {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column CHUNK.ID
     *
     * @mbg.generated Thu Oct 19 19:38:31 JST 2017
     */
    private Integer id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column CHUNK.CONTENT_HASH
     *
     * @mbg.generated Thu Oct 19 19:38:31 JST 2017
     */
    private String contentHash;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column CHUNK.SIZE
     *
     * @mbg.generated Thu Oct 19 19:38:31 JST 2017
     */
    private Integer size;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column CHUNK.LOCATION_ID
     *
     * @mbg.generated Thu Oct 19 19:38:31 JST 2017
     */
    private String locationId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column CHUNK.PARENT_VERSION_ID
     *
     * @mbg.generated Thu Oct 19 19:38:31 JST 2017
     */
    private String parentVersionId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column CHUNK.INDEX_IN_PARENT
     *
     * @mbg.generated Thu Oct 19 19:38:31 JST 2017
     */
    private String indexInParent;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column CHUNK.ID
     *
     * @return the value of CHUNK.ID
     *
     * @mbg.generated Thu Oct 19 19:38:31 JST 2017
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
     * @mbg.generated Thu Oct 19 19:38:31 JST 2017
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column CHUNK.CONTENT_HASH
     *
     * @return the value of CHUNK.CONTENT_HASH
     *
     * @mbg.generated Thu Oct 19 19:38:31 JST 2017
     */
    public String getContentHash() {
        return contentHash;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column CHUNK.CONTENT_HASH
     *
     * @param contentHash the value for CHUNK.CONTENT_HASH
     *
     * @mbg.generated Thu Oct 19 19:38:31 JST 2017
     */
    public void setContentHash(String contentHash) {
        this.contentHash = contentHash == null ? null : contentHash.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column CHUNK.SIZE
     *
     * @return the value of CHUNK.SIZE
     *
     * @mbg.generated Thu Oct 19 19:38:31 JST 2017
     */
    public Integer getSize() {
        return size;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column CHUNK.SIZE
     *
     * @param size the value for CHUNK.SIZE
     *
     * @mbg.generated Thu Oct 19 19:38:31 JST 2017
     */
    public void setSize(Integer size) {
        this.size = size;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column CHUNK.LOCATION_ID
     *
     * @return the value of CHUNK.LOCATION_ID
     *
     * @mbg.generated Thu Oct 19 19:38:31 JST 2017
     */
    public String getLocationId() {
        return locationId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column CHUNK.LOCATION_ID
     *
     * @param locationId the value for CHUNK.LOCATION_ID
     *
     * @mbg.generated Thu Oct 19 19:38:31 JST 2017
     */
    public void setLocationId(String locationId) {
        this.locationId = locationId == null ? null : locationId.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column CHUNK.PARENT_VERSION_ID
     *
     * @return the value of CHUNK.PARENT_VERSION_ID
     *
     * @mbg.generated Thu Oct 19 19:38:31 JST 2017
     */
    public String getParentVersionId() {
        return parentVersionId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column CHUNK.PARENT_VERSION_ID
     *
     * @param parentVersionId the value for CHUNK.PARENT_VERSION_ID
     *
     * @mbg.generated Thu Oct 19 19:38:31 JST 2017
     */
    public void setParentVersionId(String parentVersionId) {
        this.parentVersionId = parentVersionId == null ? null : parentVersionId.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column CHUNK.INDEX_IN_PARENT
     *
     * @return the value of CHUNK.INDEX_IN_PARENT
     *
     * @mbg.generated Thu Oct 19 19:38:31 JST 2017
     */
    public String getIndexInParent() {
        return indexInParent;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column CHUNK.INDEX_IN_PARENT
     *
     * @param indexInParent the value for CHUNK.INDEX_IN_PARENT
     *
     * @mbg.generated Thu Oct 19 19:38:31 JST 2017
     */
    public void setIndexInParent(String indexInParent) {
        this.indexInParent = indexInParent == null ? null : indexInParent.trim();
    }
}