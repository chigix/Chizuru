package com.chigix.resserver.mybatis.record;

import java.util.ArrayList;
import java.util.List;
import org.joda.time.DateTime;

public class MultipartUploadExample {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table MULTIPART_UPLOAD
     *
     * @mbg.generated Thu Nov 23 23:18:14 JST 2017
     */
    protected String orderByClause;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table MULTIPART_UPLOAD
     *
     * @mbg.generated Thu Nov 23 23:18:14 JST 2017
     */
    protected boolean distinct;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table MULTIPART_UPLOAD
     *
     * @mbg.generated Thu Nov 23 23:18:14 JST 2017
     */
    protected List<Criteria> oredCriteria;

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table MULTIPART_UPLOAD
     *
     * @mbg.generated Thu Nov 23 23:18:14 JST 2017
     */
    public MultipartUploadExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table MULTIPART_UPLOAD
     *
     * @mbg.generated Thu Nov 23 23:18:14 JST 2017
     */
    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table MULTIPART_UPLOAD
     *
     * @mbg.generated Thu Nov 23 23:18:14 JST 2017
     */
    public String getOrderByClause() {
        return orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table MULTIPART_UPLOAD
     *
     * @mbg.generated Thu Nov 23 23:18:14 JST 2017
     */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table MULTIPART_UPLOAD
     *
     * @mbg.generated Thu Nov 23 23:18:14 JST 2017
     */
    public boolean isDistinct() {
        return distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table MULTIPART_UPLOAD
     *
     * @mbg.generated Thu Nov 23 23:18:14 JST 2017
     */
    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table MULTIPART_UPLOAD
     *
     * @mbg.generated Thu Nov 23 23:18:14 JST 2017
     */
    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table MULTIPART_UPLOAD
     *
     * @mbg.generated Thu Nov 23 23:18:14 JST 2017
     */
    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table MULTIPART_UPLOAD
     *
     * @mbg.generated Thu Nov 23 23:18:14 JST 2017
     */
    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table MULTIPART_UPLOAD
     *
     * @mbg.generated Thu Nov 23 23:18:14 JST 2017
     */
    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table MULTIPART_UPLOAD
     *
     * @mbg.generated Thu Nov 23 23:18:14 JST 2017
     */
    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table MULTIPART_UPLOAD
     *
     * @mbg.generated Thu Nov 23 23:18:14 JST 2017
     */
    protected abstract static class GeneratedCriteria {
        protected List<Criterion> initiatedAtCriteria;

        protected List<Criterion> allCriteria;

        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
            initiatedAtCriteria = new ArrayList<Criterion>();
        }

        public List<Criterion> getInitiatedAtCriteria() {
            return initiatedAtCriteria;
        }

        protected void addInitiatedAtCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            initiatedAtCriteria.add(new Criterion(condition, value, "com.chigix.resserver.mybatis.type.DatetimeTypeHandler"));
            allCriteria = null;
        }

        protected void addInitiatedAtCriterion(String condition, DateTime value1, DateTime value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            initiatedAtCriteria.add(new Criterion(condition, value1, value2, "com.chigix.resserver.mybatis.type.DatetimeTypeHandler"));
            allCriteria = null;
        }

        public boolean isValid() {
            return criteria.size() > 0
                || initiatedAtCriteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            if (allCriteria == null) {
                allCriteria = new ArrayList<Criterion>();
                allCriteria.addAll(criteria);
                allCriteria.addAll(initiatedAtCriteria);
            }
            return allCriteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
            allCriteria = null;
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
            allCriteria = null;
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
            allCriteria = null;
        }

        public Criteria andIdIsNull() {
            addCriterion("table_a.\"ID\" is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("table_a.\"ID\" is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(Integer value) {
            addCriterion("table_a.\"ID\" =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Integer value) {
            addCriterion("table_a.\"ID\" <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Integer value) {
            addCriterion("table_a.\"ID\" >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("table_a.\"ID\" >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Integer value) {
            addCriterion("table_a.\"ID\" <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Integer value) {
            addCriterion("table_a.\"ID\" <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Integer> values) {
            addCriterion("table_a.\"ID\" in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Integer> values) {
            addCriterion("table_a.\"ID\" not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Integer value1, Integer value2) {
            addCriterion("table_a.\"ID\" between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Integer value1, Integer value2) {
            addCriterion("table_a.\"ID\" not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andUuidIsNull() {
            addCriterion("table_a.\"uuid\" is null");
            return (Criteria) this;
        }

        public Criteria andUuidIsNotNull() {
            addCriterion("table_a.\"uuid\" is not null");
            return (Criteria) this;
        }

        public Criteria andUuidEqualTo(String value) {
            addCriterion("table_a.\"uuid\" =", value, "uuid");
            return (Criteria) this;
        }

        public Criteria andUuidNotEqualTo(String value) {
            addCriterion("table_a.\"uuid\" <>", value, "uuid");
            return (Criteria) this;
        }

        public Criteria andUuidGreaterThan(String value) {
            addCriterion("table_a.\"uuid\" >", value, "uuid");
            return (Criteria) this;
        }

        public Criteria andUuidGreaterThanOrEqualTo(String value) {
            addCriterion("table_a.\"uuid\" >=", value, "uuid");
            return (Criteria) this;
        }

        public Criteria andUuidLessThan(String value) {
            addCriterion("table_a.\"uuid\" <", value, "uuid");
            return (Criteria) this;
        }

        public Criteria andUuidLessThanOrEqualTo(String value) {
            addCriterion("table_a.\"uuid\" <=", value, "uuid");
            return (Criteria) this;
        }

        public Criteria andUuidLike(String value) {
            addCriterion("table_a.\"uuid\" like", value, "uuid");
            return (Criteria) this;
        }

        public Criteria andUuidNotLike(String value) {
            addCriterion("table_a.\"uuid\" not like", value, "uuid");
            return (Criteria) this;
        }

        public Criteria andUuidIn(List<String> values) {
            addCriterion("table_a.\"uuid\" in", values, "uuid");
            return (Criteria) this;
        }

        public Criteria andUuidNotIn(List<String> values) {
            addCriterion("table_a.\"uuid\" not in", values, "uuid");
            return (Criteria) this;
        }

        public Criteria andUuidBetween(String value1, String value2) {
            addCriterion("table_a.\"uuid\" between", value1, value2, "uuid");
            return (Criteria) this;
        }

        public Criteria andUuidNotBetween(String value1, String value2) {
            addCriterion("table_a.\"uuid\" not between", value1, value2, "uuid");
            return (Criteria) this;
        }

        public Criteria andInitiatedAtIsNull() {
            addCriterion("table_a.\"initiated_at\" is null");
            return (Criteria) this;
        }

        public Criteria andInitiatedAtIsNotNull() {
            addCriterion("table_a.\"initiated_at\" is not null");
            return (Criteria) this;
        }

        public Criteria andInitiatedAtEqualTo(DateTime value) {
            addInitiatedAtCriterion("table_a.\"initiated_at\" =", value, "initiatedAt");
            return (Criteria) this;
        }

        public Criteria andInitiatedAtNotEqualTo(DateTime value) {
            addInitiatedAtCriterion("table_a.\"initiated_at\" <>", value, "initiatedAt");
            return (Criteria) this;
        }

        public Criteria andInitiatedAtGreaterThan(DateTime value) {
            addInitiatedAtCriterion("table_a.\"initiated_at\" >", value, "initiatedAt");
            return (Criteria) this;
        }

        public Criteria andInitiatedAtGreaterThanOrEqualTo(DateTime value) {
            addInitiatedAtCriterion("table_a.\"initiated_at\" >=", value, "initiatedAt");
            return (Criteria) this;
        }

        public Criteria andInitiatedAtLessThan(DateTime value) {
            addInitiatedAtCriterion("table_a.\"initiated_at\" <", value, "initiatedAt");
            return (Criteria) this;
        }

        public Criteria andInitiatedAtLessThanOrEqualTo(DateTime value) {
            addInitiatedAtCriterion("table_a.\"initiated_at\" <=", value, "initiatedAt");
            return (Criteria) this;
        }

        public Criteria andInitiatedAtLike(DateTime value) {
            addInitiatedAtCriterion("table_a.\"initiated_at\" like", value, "initiatedAt");
            return (Criteria) this;
        }

        public Criteria andInitiatedAtNotLike(DateTime value) {
            addInitiatedAtCriterion("table_a.\"initiated_at\" not like", value, "initiatedAt");
            return (Criteria) this;
        }

        public Criteria andInitiatedAtIn(List<DateTime> values) {
            addInitiatedAtCriterion("table_a.\"initiated_at\" in", values, "initiatedAt");
            return (Criteria) this;
        }

        public Criteria andInitiatedAtNotIn(List<DateTime> values) {
            addInitiatedAtCriterion("table_a.\"initiated_at\" not in", values, "initiatedAt");
            return (Criteria) this;
        }

        public Criteria andInitiatedAtBetween(DateTime value1, DateTime value2) {
            addInitiatedAtCriterion("table_a.\"initiated_at\" between", value1, value2, "initiatedAt");
            return (Criteria) this;
        }

        public Criteria andInitiatedAtNotBetween(DateTime value1, DateTime value2) {
            addInitiatedAtCriterion("table_a.\"initiated_at\" not between", value1, value2, "initiatedAt");
            return (Criteria) this;
        }

        public Criteria andResourceKeyhashIsNull() {
            addCriterion("table_a.\"resource_keyhash\" is null");
            return (Criteria) this;
        }

        public Criteria andResourceKeyhashIsNotNull() {
            addCriterion("table_a.\"resource_keyhash\" is not null");
            return (Criteria) this;
        }

        public Criteria andResourceKeyhashEqualTo(String value) {
            addCriterion("table_a.\"resource_keyhash\" =", value, "resourceKeyhash");
            return (Criteria) this;
        }

        public Criteria andResourceKeyhashNotEqualTo(String value) {
            addCriterion("table_a.\"resource_keyhash\" <>", value, "resourceKeyhash");
            return (Criteria) this;
        }

        public Criteria andResourceKeyhashGreaterThan(String value) {
            addCriterion("table_a.\"resource_keyhash\" >", value, "resourceKeyhash");
            return (Criteria) this;
        }

        public Criteria andResourceKeyhashGreaterThanOrEqualTo(String value) {
            addCriterion("table_a.\"resource_keyhash\" >=", value, "resourceKeyhash");
            return (Criteria) this;
        }

        public Criteria andResourceKeyhashLessThan(String value) {
            addCriterion("table_a.\"resource_keyhash\" <", value, "resourceKeyhash");
            return (Criteria) this;
        }

        public Criteria andResourceKeyhashLessThanOrEqualTo(String value) {
            addCriterion("table_a.\"resource_keyhash\" <=", value, "resourceKeyhash");
            return (Criteria) this;
        }

        public Criteria andResourceKeyhashLike(String value) {
            addCriterion("table_a.\"resource_keyhash\" like", value, "resourceKeyhash");
            return (Criteria) this;
        }

        public Criteria andResourceKeyhashNotLike(String value) {
            addCriterion("table_a.\"resource_keyhash\" not like", value, "resourceKeyhash");
            return (Criteria) this;
        }

        public Criteria andResourceKeyhashIn(List<String> values) {
            addCriterion("table_a.\"resource_keyhash\" in", values, "resourceKeyhash");
            return (Criteria) this;
        }

        public Criteria andResourceKeyhashNotIn(List<String> values) {
            addCriterion("table_a.\"resource_keyhash\" not in", values, "resourceKeyhash");
            return (Criteria) this;
        }

        public Criteria andResourceKeyhashBetween(String value1, String value2) {
            addCriterion("table_a.\"resource_keyhash\" between", value1, value2, "resourceKeyhash");
            return (Criteria) this;
        }

        public Criteria andResourceKeyhashNotBetween(String value1, String value2) {
            addCriterion("table_a.\"resource_keyhash\" not between", value1, value2, "resourceKeyhash");
            return (Criteria) this;
        }

        public Criteria andResourceVersionIsNull() {
            addCriterion("table_a.\"resource_version\" is null");
            return (Criteria) this;
        }

        public Criteria andResourceVersionIsNotNull() {
            addCriterion("table_a.\"resource_version\" is not null");
            return (Criteria) this;
        }

        public Criteria andResourceVersionEqualTo(String value) {
            addCriterion("table_a.\"resource_version\" =", value, "resourceVersion");
            return (Criteria) this;
        }

        public Criteria andResourceVersionNotEqualTo(String value) {
            addCriterion("table_a.\"resource_version\" <>", value, "resourceVersion");
            return (Criteria) this;
        }

        public Criteria andResourceVersionGreaterThan(String value) {
            addCriterion("table_a.\"resource_version\" >", value, "resourceVersion");
            return (Criteria) this;
        }

        public Criteria andResourceVersionGreaterThanOrEqualTo(String value) {
            addCriterion("table_a.\"resource_version\" >=", value, "resourceVersion");
            return (Criteria) this;
        }

        public Criteria andResourceVersionLessThan(String value) {
            addCriterion("table_a.\"resource_version\" <", value, "resourceVersion");
            return (Criteria) this;
        }

        public Criteria andResourceVersionLessThanOrEqualTo(String value) {
            addCriterion("table_a.\"resource_version\" <=", value, "resourceVersion");
            return (Criteria) this;
        }

        public Criteria andResourceVersionLike(String value) {
            addCriterion("table_a.\"resource_version\" like", value, "resourceVersion");
            return (Criteria) this;
        }

        public Criteria andResourceVersionNotLike(String value) {
            addCriterion("table_a.\"resource_version\" not like", value, "resourceVersion");
            return (Criteria) this;
        }

        public Criteria andResourceVersionIn(List<String> values) {
            addCriterion("table_a.\"resource_version\" in", values, "resourceVersion");
            return (Criteria) this;
        }

        public Criteria andResourceVersionNotIn(List<String> values) {
            addCriterion("table_a.\"resource_version\" not in", values, "resourceVersion");
            return (Criteria) this;
        }

        public Criteria andResourceVersionBetween(String value1, String value2) {
            addCriterion("table_a.\"resource_version\" between", value1, value2, "resourceVersion");
            return (Criteria) this;
        }

        public Criteria andResourceVersionNotBetween(String value1, String value2) {
            addCriterion("table_a.\"resource_version\" not between", value1, value2, "resourceVersion");
            return (Criteria) this;
        }

        public Criteria andBucketUuidIsNull() {
            addCriterion("table_a.\"bucket_uuid\" is null");
            return (Criteria) this;
        }

        public Criteria andBucketUuidIsNotNull() {
            addCriterion("table_a.\"bucket_uuid\" is not null");
            return (Criteria) this;
        }

        public Criteria andBucketUuidEqualTo(String value) {
            addCriterion("table_a.\"bucket_uuid\" =", value, "bucketUuid");
            return (Criteria) this;
        }

        public Criteria andBucketUuidNotEqualTo(String value) {
            addCriterion("table_a.\"bucket_uuid\" <>", value, "bucketUuid");
            return (Criteria) this;
        }

        public Criteria andBucketUuidGreaterThan(String value) {
            addCriterion("table_a.\"bucket_uuid\" >", value, "bucketUuid");
            return (Criteria) this;
        }

        public Criteria andBucketUuidGreaterThanOrEqualTo(String value) {
            addCriterion("table_a.\"bucket_uuid\" >=", value, "bucketUuid");
            return (Criteria) this;
        }

        public Criteria andBucketUuidLessThan(String value) {
            addCriterion("table_a.\"bucket_uuid\" <", value, "bucketUuid");
            return (Criteria) this;
        }

        public Criteria andBucketUuidLessThanOrEqualTo(String value) {
            addCriterion("table_a.\"bucket_uuid\" <=", value, "bucketUuid");
            return (Criteria) this;
        }

        public Criteria andBucketUuidLike(String value) {
            addCriterion("table_a.\"bucket_uuid\" like", value, "bucketUuid");
            return (Criteria) this;
        }

        public Criteria andBucketUuidNotLike(String value) {
            addCriterion("table_a.\"bucket_uuid\" not like", value, "bucketUuid");
            return (Criteria) this;
        }

        public Criteria andBucketUuidIn(List<String> values) {
            addCriterion("table_a.\"bucket_uuid\" in", values, "bucketUuid");
            return (Criteria) this;
        }

        public Criteria andBucketUuidNotIn(List<String> values) {
            addCriterion("table_a.\"bucket_uuid\" not in", values, "bucketUuid");
            return (Criteria) this;
        }

        public Criteria andBucketUuidBetween(String value1, String value2) {
            addCriterion("table_a.\"bucket_uuid\" between", value1, value2, "bucketUuid");
            return (Criteria) this;
        }

        public Criteria andBucketUuidNotBetween(String value1, String value2) {
            addCriterion("table_a.\"bucket_uuid\" not between", value1, value2, "bucketUuid");
            return (Criteria) this;
        }

        public Criteria andResourceKeyIsNull() {
            addCriterion("table_a.\"resource_key\" is null");
            return (Criteria) this;
        }

        public Criteria andResourceKeyIsNotNull() {
            addCriterion("table_a.\"resource_key\" is not null");
            return (Criteria) this;
        }

        public Criteria andResourceKeyEqualTo(String value) {
            addCriterion("table_a.\"resource_key\" =", value, "resourceKey");
            return (Criteria) this;
        }

        public Criteria andResourceKeyNotEqualTo(String value) {
            addCriterion("table_a.\"resource_key\" <>", value, "resourceKey");
            return (Criteria) this;
        }

        public Criteria andResourceKeyGreaterThan(String value) {
            addCriterion("table_a.\"resource_key\" >", value, "resourceKey");
            return (Criteria) this;
        }

        public Criteria andResourceKeyGreaterThanOrEqualTo(String value) {
            addCriterion("table_a.\"resource_key\" >=", value, "resourceKey");
            return (Criteria) this;
        }

        public Criteria andResourceKeyLessThan(String value) {
            addCriterion("table_a.\"resource_key\" <", value, "resourceKey");
            return (Criteria) this;
        }

        public Criteria andResourceKeyLessThanOrEqualTo(String value) {
            addCriterion("table_a.\"resource_key\" <=", value, "resourceKey");
            return (Criteria) this;
        }

        public Criteria andResourceKeyLike(String value) {
            addCriterion("table_a.\"resource_key\" like", value, "resourceKey");
            return (Criteria) this;
        }

        public Criteria andResourceKeyNotLike(String value) {
            addCriterion("table_a.\"resource_key\" not like", value, "resourceKey");
            return (Criteria) this;
        }

        public Criteria andResourceKeyIn(List<String> values) {
            addCriterion("table_a.\"resource_key\" in", values, "resourceKey");
            return (Criteria) this;
        }

        public Criteria andResourceKeyNotIn(List<String> values) {
            addCriterion("table_a.\"resource_key\" not in", values, "resourceKey");
            return (Criteria) this;
        }

        public Criteria andResourceKeyBetween(String value1, String value2) {
            addCriterion("table_a.\"resource_key\" between", value1, value2, "resourceKey");
            return (Criteria) this;
        }

        public Criteria andResourceKeyNotBetween(String value1, String value2) {
            addCriterion("table_a.\"resource_key\" not between", value1, value2, "resourceKey");
            return (Criteria) this;
        }

        public Criteria andBucketNameIsNull() {
            addCriterion("table_a.\"bucket_name\" is null");
            return (Criteria) this;
        }

        public Criteria andBucketNameIsNotNull() {
            addCriterion("table_a.\"bucket_name\" is not null");
            return (Criteria) this;
        }

        public Criteria andBucketNameEqualTo(String value) {
            addCriterion("table_a.\"bucket_name\" =", value, "bucketName");
            return (Criteria) this;
        }

        public Criteria andBucketNameNotEqualTo(String value) {
            addCriterion("table_a.\"bucket_name\" <>", value, "bucketName");
            return (Criteria) this;
        }

        public Criteria andBucketNameGreaterThan(String value) {
            addCriterion("table_a.\"bucket_name\" >", value, "bucketName");
            return (Criteria) this;
        }

        public Criteria andBucketNameGreaterThanOrEqualTo(String value) {
            addCriterion("table_a.\"bucket_name\" >=", value, "bucketName");
            return (Criteria) this;
        }

        public Criteria andBucketNameLessThan(String value) {
            addCriterion("table_a.\"bucket_name\" <", value, "bucketName");
            return (Criteria) this;
        }

        public Criteria andBucketNameLessThanOrEqualTo(String value) {
            addCriterion("table_a.\"bucket_name\" <=", value, "bucketName");
            return (Criteria) this;
        }

        public Criteria andBucketNameLike(String value) {
            addCriterion("table_a.\"bucket_name\" like", value, "bucketName");
            return (Criteria) this;
        }

        public Criteria andBucketNameNotLike(String value) {
            addCriterion("table_a.\"bucket_name\" not like", value, "bucketName");
            return (Criteria) this;
        }

        public Criteria andBucketNameIn(List<String> values) {
            addCriterion("table_a.\"bucket_name\" in", values, "bucketName");
            return (Criteria) this;
        }

        public Criteria andBucketNameNotIn(List<String> values) {
            addCriterion("table_a.\"bucket_name\" not in", values, "bucketName");
            return (Criteria) this;
        }

        public Criteria andBucketNameBetween(String value1, String value2) {
            addCriterion("table_a.\"bucket_name\" between", value1, value2, "bucketName");
            return (Criteria) this;
        }

        public Criteria andBucketNameNotBetween(String value1, String value2) {
            addCriterion("table_a.\"bucket_name\" not between", value1, value2, "bucketName");
            return (Criteria) this;
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table MULTIPART_UPLOAD
     *
     * @mbg.generated do_not_delete_during_merge Thu Nov 23 23:18:14 JST 2017
     */
    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table MULTIPART_UPLOAD
     *
     * @mbg.generated Thu Nov 23 23:18:14 JST 2017
     */
    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}