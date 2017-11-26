package com.chigix.resserver.mybatis.record;

import org.joda.time.DateTime;

public class Bucket {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column BUCKET.ID
     *
     * @mbg.generated Thu Nov 23 23:18:14 JST 2017
     */
    private Integer id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column BUCKET.uuid
     *
     * @mbg.generated Thu Nov 23 23:18:14 JST 2017
     */
    private String uuid;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column BUCKET.name
     *
     * @mbg.generated Thu Nov 23 23:18:14 JST 2017
     */
    private String name;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column BUCKET.creation_time
     *
     * @mbg.generated Thu Nov 23 23:18:14 JST 2017
     */
    private DateTime creationTime;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column BUCKET.ID
     *
     * @return the value of BUCKET.ID
     *
     * @mbg.generated Thu Nov 23 23:18:14 JST 2017
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column BUCKET.ID
     *
     * @param id the value for BUCKET.ID
     *
     * @mbg.generated Thu Nov 23 23:18:14 JST 2017
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column BUCKET.uuid
     *
     * @return the value of BUCKET.uuid
     *
     * @mbg.generated Thu Nov 23 23:18:14 JST 2017
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column BUCKET.uuid
     *
     * @param uuid the value for BUCKET.uuid
     *
     * @mbg.generated Thu Nov 23 23:18:14 JST 2017
     */
    public void setUuid(String uuid) {
        this.uuid = uuid == null ? null : uuid.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column BUCKET.name
     *
     * @return the value of BUCKET.name
     *
     * @mbg.generated Thu Nov 23 23:18:14 JST 2017
     */
    public String getName() {
        return name;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column BUCKET.name
     *
     * @param name the value for BUCKET.name
     *
     * @mbg.generated Thu Nov 23 23:18:14 JST 2017
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column BUCKET.creation_time
     *
     * @return the value of BUCKET.creation_time
     *
     * @mbg.generated Thu Nov 23 23:18:14 JST 2017
     */
    public DateTime getCreationTime() {
        return creationTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column BUCKET.creation_time
     *
     * @param creationTime the value for BUCKET.creation_time
     *
     * @mbg.generated Thu Nov 23 23:18:14 JST 2017
     */
    public void setCreationTime(DateTime creationTime) {
        this.creationTime = creationTime;
    }
}