package com.chigix.resserver.mybatis.record;

import java.util.ArrayList;
import java.util.List;
import org.joda.time.DateTime;

public class ResourceExample {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table RESOURCE
     *
     * @mbg.generated Thu Oct 19 19:38:31 JST 2017
     */
    protected String orderByClause;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table RESOURCE
     *
     * @mbg.generated Thu Oct 19 19:38:31 JST 2017
     */
    protected boolean distinct;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table RESOURCE
     *
     * @mbg.generated Thu Oct 19 19:38:31 JST 2017
     */
    protected List<Criteria> oredCriteria;

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table RESOURCE
     *
     * @mbg.generated Thu Oct 19 19:38:31 JST 2017
     */
    public ResourceExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table RESOURCE
     *
     * @mbg.generated Thu Oct 19 19:38:31 JST 2017
     */
    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table RESOURCE
     *
     * @mbg.generated Thu Oct 19 19:38:31 JST 2017
     */
    public String getOrderByClause() {
        return orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table RESOURCE
     *
     * @mbg.generated Thu Oct 19 19:38:31 JST 2017
     */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table RESOURCE
     *
     * @mbg.generated Thu Oct 19 19:38:31 JST 2017
     */
    public boolean isDistinct() {
        return distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table RESOURCE
     *
     * @mbg.generated Thu Oct 19 19:38:31 JST 2017
     */
    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table RESOURCE
     *
     * @mbg.generated Thu Oct 19 19:38:31 JST 2017
     */
    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table RESOURCE
     *
     * @mbg.generated Thu Oct 19 19:38:31 JST 2017
     */
    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table RESOURCE
     *
     * @mbg.generated Thu Oct 19 19:38:31 JST 2017
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
     * This method corresponds to the database table RESOURCE
     *
     * @mbg.generated Thu Oct 19 19:38:31 JST 2017
     */
    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table RESOURCE
     *
     * @mbg.generated Thu Oct 19 19:38:31 JST 2017
     */
    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table RESOURCE
     *
     * @mbg.generated Thu Oct 19 19:38:31 JST 2017
     */
    protected abstract static class GeneratedCriteria {
        protected List<Criterion> lastModifiedCriteria;

        protected List<Criterion> allCriteria;

        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
            lastModifiedCriteria = new ArrayList<Criterion>();
        }

        public List<Criterion> getLastModifiedCriteria() {
            return lastModifiedCriteria;
        }

        protected void addLastModifiedCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            lastModifiedCriteria.add(new Criterion(condition, value, "com.chigix.resserver.mybatis.type.DatetimeTypeHandler"));
            allCriteria = null;
        }

        protected void addLastModifiedCriterion(String condition, DateTime value1, DateTime value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            lastModifiedCriteria.add(new Criterion(condition, value1, value2, "com.chigix.resserver.mybatis.type.DatetimeTypeHandler"));
            allCriteria = null;
        }

        public boolean isValid() {
            return criteria.size() > 0
                || lastModifiedCriteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            if (allCriteria == null) {
                allCriteria = new ArrayList<Criterion>();
                allCriteria.addAll(criteria);
                allCriteria.addAll(lastModifiedCriteria);
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

        public Criteria andKeyIsNull() {
            addCriterion("table_a.\"key\" is null");
            return (Criteria) this;
        }

        public Criteria andKeyIsNotNull() {
            addCriterion("table_a.\"key\" is not null");
            return (Criteria) this;
        }

        public Criteria andKeyEqualTo(String value) {
            addCriterion("table_a.\"key\" =", value, "key");
            return (Criteria) this;
        }

        public Criteria andKeyNotEqualTo(String value) {
            addCriterion("table_a.\"key\" <>", value, "key");
            return (Criteria) this;
        }

        public Criteria andKeyGreaterThan(String value) {
            addCriterion("table_a.\"key\" >", value, "key");
            return (Criteria) this;
        }

        public Criteria andKeyGreaterThanOrEqualTo(String value) {
            addCriterion("table_a.\"key\" >=", value, "key");
            return (Criteria) this;
        }

        public Criteria andKeyLessThan(String value) {
            addCriterion("table_a.\"key\" <", value, "key");
            return (Criteria) this;
        }

        public Criteria andKeyLessThanOrEqualTo(String value) {
            addCriterion("table_a.\"key\" <=", value, "key");
            return (Criteria) this;
        }

        public Criteria andKeyLike(String value) {
            addCriterion("table_a.\"key\" like", value, "key");
            return (Criteria) this;
        }

        public Criteria andKeyNotLike(String value) {
            addCriterion("table_a.\"key\" not like", value, "key");
            return (Criteria) this;
        }

        public Criteria andKeyIn(List<String> values) {
            addCriterion("table_a.\"key\" in", values, "key");
            return (Criteria) this;
        }

        public Criteria andKeyNotIn(List<String> values) {
            addCriterion("table_a.\"key\" not in", values, "key");
            return (Criteria) this;
        }

        public Criteria andKeyBetween(String value1, String value2) {
            addCriterion("table_a.\"key\" between", value1, value2, "key");
            return (Criteria) this;
        }

        public Criteria andKeyNotBetween(String value1, String value2) {
            addCriterion("table_a.\"key\" not between", value1, value2, "key");
            return (Criteria) this;
        }

        public Criteria andKeyhashIsNull() {
            addCriterion("table_a.\"keyhash\" is null");
            return (Criteria) this;
        }

        public Criteria andKeyhashIsNotNull() {
            addCriterion("table_a.\"keyhash\" is not null");
            return (Criteria) this;
        }

        public Criteria andKeyhashEqualTo(String value) {
            addCriterion("table_a.\"keyhash\" =", value, "keyhash");
            return (Criteria) this;
        }

        public Criteria andKeyhashNotEqualTo(String value) {
            addCriterion("table_a.\"keyhash\" <>", value, "keyhash");
            return (Criteria) this;
        }

        public Criteria andKeyhashGreaterThan(String value) {
            addCriterion("table_a.\"keyhash\" >", value, "keyhash");
            return (Criteria) this;
        }

        public Criteria andKeyhashGreaterThanOrEqualTo(String value) {
            addCriterion("table_a.\"keyhash\" >=", value, "keyhash");
            return (Criteria) this;
        }

        public Criteria andKeyhashLessThan(String value) {
            addCriterion("table_a.\"keyhash\" <", value, "keyhash");
            return (Criteria) this;
        }

        public Criteria andKeyhashLessThanOrEqualTo(String value) {
            addCriterion("table_a.\"keyhash\" <=", value, "keyhash");
            return (Criteria) this;
        }

        public Criteria andKeyhashLike(String value) {
            addCriterion("table_a.\"keyhash\" like", value, "keyhash");
            return (Criteria) this;
        }

        public Criteria andKeyhashNotLike(String value) {
            addCriterion("table_a.\"keyhash\" not like", value, "keyhash");
            return (Criteria) this;
        }

        public Criteria andKeyhashIn(List<String> values) {
            addCriterion("table_a.\"keyhash\" in", values, "keyhash");
            return (Criteria) this;
        }

        public Criteria andKeyhashNotIn(List<String> values) {
            addCriterion("table_a.\"keyhash\" not in", values, "keyhash");
            return (Criteria) this;
        }

        public Criteria andKeyhashBetween(String value1, String value2) {
            addCriterion("table_a.\"keyhash\" between", value1, value2, "keyhash");
            return (Criteria) this;
        }

        public Criteria andKeyhashNotBetween(String value1, String value2) {
            addCriterion("table_a.\"keyhash\" not between", value1, value2, "keyhash");
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

        public Criteria andTypeIsNull() {
            addCriterion("table_a.\"type\" is null");
            return (Criteria) this;
        }

        public Criteria andTypeIsNotNull() {
            addCriterion("table_a.\"type\" is not null");
            return (Criteria) this;
        }

        public Criteria andTypeEqualTo(String value) {
            addCriterion("table_a.\"type\" =", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeNotEqualTo(String value) {
            addCriterion("table_a.\"type\" <>", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeGreaterThan(String value) {
            addCriterion("table_a.\"type\" >", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeGreaterThanOrEqualTo(String value) {
            addCriterion("table_a.\"type\" >=", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeLessThan(String value) {
            addCriterion("table_a.\"type\" <", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeLessThanOrEqualTo(String value) {
            addCriterion("table_a.\"type\" <=", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeLike(String value) {
            addCriterion("table_a.\"type\" like", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeNotLike(String value) {
            addCriterion("table_a.\"type\" not like", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeIn(List<String> values) {
            addCriterion("table_a.\"type\" in", values, "type");
            return (Criteria) this;
        }

        public Criteria andTypeNotIn(List<String> values) {
            addCriterion("table_a.\"type\" not in", values, "type");
            return (Criteria) this;
        }

        public Criteria andTypeBetween(String value1, String value2) {
            addCriterion("table_a.\"type\" between", value1, value2, "type");
            return (Criteria) this;
        }

        public Criteria andTypeNotBetween(String value1, String value2) {
            addCriterion("table_a.\"type\" not between", value1, value2, "type");
            return (Criteria) this;
        }

        public Criteria andEtagIsNull() {
            addCriterion("table_a.\"etag\" is null");
            return (Criteria) this;
        }

        public Criteria andEtagIsNotNull() {
            addCriterion("table_a.\"etag\" is not null");
            return (Criteria) this;
        }

        public Criteria andEtagEqualTo(String value) {
            addCriterion("table_a.\"etag\" =", value, "etag");
            return (Criteria) this;
        }

        public Criteria andEtagNotEqualTo(String value) {
            addCriterion("table_a.\"etag\" <>", value, "etag");
            return (Criteria) this;
        }

        public Criteria andEtagGreaterThan(String value) {
            addCriterion("table_a.\"etag\" >", value, "etag");
            return (Criteria) this;
        }

        public Criteria andEtagGreaterThanOrEqualTo(String value) {
            addCriterion("table_a.\"etag\" >=", value, "etag");
            return (Criteria) this;
        }

        public Criteria andEtagLessThan(String value) {
            addCriterion("table_a.\"etag\" <", value, "etag");
            return (Criteria) this;
        }

        public Criteria andEtagLessThanOrEqualTo(String value) {
            addCriterion("table_a.\"etag\" <=", value, "etag");
            return (Criteria) this;
        }

        public Criteria andEtagLike(String value) {
            addCriterion("table_a.\"etag\" like", value, "etag");
            return (Criteria) this;
        }

        public Criteria andEtagNotLike(String value) {
            addCriterion("table_a.\"etag\" not like", value, "etag");
            return (Criteria) this;
        }

        public Criteria andEtagIn(List<String> values) {
            addCriterion("table_a.\"etag\" in", values, "etag");
            return (Criteria) this;
        }

        public Criteria andEtagNotIn(List<String> values) {
            addCriterion("table_a.\"etag\" not in", values, "etag");
            return (Criteria) this;
        }

        public Criteria andEtagBetween(String value1, String value2) {
            addCriterion("table_a.\"etag\" between", value1, value2, "etag");
            return (Criteria) this;
        }

        public Criteria andEtagNotBetween(String value1, String value2) {
            addCriterion("table_a.\"etag\" not between", value1, value2, "etag");
            return (Criteria) this;
        }

        public Criteria andLastModifiedIsNull() {
            addCriterion("table_a.\"last_modified\" is null");
            return (Criteria) this;
        }

        public Criteria andLastModifiedIsNotNull() {
            addCriterion("table_a.\"last_modified\" is not null");
            return (Criteria) this;
        }

        public Criteria andLastModifiedEqualTo(DateTime value) {
            addLastModifiedCriterion("table_a.\"last_modified\" =", value, "lastModified");
            return (Criteria) this;
        }

        public Criteria andLastModifiedNotEqualTo(DateTime value) {
            addLastModifiedCriterion("table_a.\"last_modified\" <>", value, "lastModified");
            return (Criteria) this;
        }

        public Criteria andLastModifiedGreaterThan(DateTime value) {
            addLastModifiedCriterion("table_a.\"last_modified\" >", value, "lastModified");
            return (Criteria) this;
        }

        public Criteria andLastModifiedGreaterThanOrEqualTo(DateTime value) {
            addLastModifiedCriterion("table_a.\"last_modified\" >=", value, "lastModified");
            return (Criteria) this;
        }

        public Criteria andLastModifiedLessThan(DateTime value) {
            addLastModifiedCriterion("table_a.\"last_modified\" <", value, "lastModified");
            return (Criteria) this;
        }

        public Criteria andLastModifiedLessThanOrEqualTo(DateTime value) {
            addLastModifiedCriterion("table_a.\"last_modified\" <=", value, "lastModified");
            return (Criteria) this;
        }

        public Criteria andLastModifiedLike(DateTime value) {
            addLastModifiedCriterion("table_a.\"last_modified\" like", value, "lastModified");
            return (Criteria) this;
        }

        public Criteria andLastModifiedNotLike(DateTime value) {
            addLastModifiedCriterion("table_a.\"last_modified\" not like", value, "lastModified");
            return (Criteria) this;
        }

        public Criteria andLastModifiedIn(List<DateTime> values) {
            addLastModifiedCriterion("table_a.\"last_modified\" in", values, "lastModified");
            return (Criteria) this;
        }

        public Criteria andLastModifiedNotIn(List<DateTime> values) {
            addLastModifiedCriterion("table_a.\"last_modified\" not in", values, "lastModified");
            return (Criteria) this;
        }

        public Criteria andLastModifiedBetween(DateTime value1, DateTime value2) {
            addLastModifiedCriterion("table_a.\"last_modified\" between", value1, value2, "lastModified");
            return (Criteria) this;
        }

        public Criteria andLastModifiedNotBetween(DateTime value1, DateTime value2) {
            addLastModifiedCriterion("table_a.\"last_modified\" not between", value1, value2, "lastModified");
            return (Criteria) this;
        }

        public Criteria andSizeIsNull() {
            addCriterion("table_a.\"size\" is null");
            return (Criteria) this;
        }

        public Criteria andSizeIsNotNull() {
            addCriterion("table_a.\"size\" is not null");
            return (Criteria) this;
        }

        public Criteria andSizeEqualTo(String value) {
            addCriterion("table_a.\"size\" =", value, "size");
            return (Criteria) this;
        }

        public Criteria andSizeNotEqualTo(String value) {
            addCriterion("table_a.\"size\" <>", value, "size");
            return (Criteria) this;
        }

        public Criteria andSizeGreaterThan(String value) {
            addCriterion("table_a.\"size\" >", value, "size");
            return (Criteria) this;
        }

        public Criteria andSizeGreaterThanOrEqualTo(String value) {
            addCriterion("table_a.\"size\" >=", value, "size");
            return (Criteria) this;
        }

        public Criteria andSizeLessThan(String value) {
            addCriterion("table_a.\"size\" <", value, "size");
            return (Criteria) this;
        }

        public Criteria andSizeLessThanOrEqualTo(String value) {
            addCriterion("table_a.\"size\" <=", value, "size");
            return (Criteria) this;
        }

        public Criteria andSizeLike(String value) {
            addCriterion("table_a.\"size\" like", value, "size");
            return (Criteria) this;
        }

        public Criteria andSizeNotLike(String value) {
            addCriterion("table_a.\"size\" not like", value, "size");
            return (Criteria) this;
        }

        public Criteria andSizeIn(List<String> values) {
            addCriterion("table_a.\"size\" in", values, "size");
            return (Criteria) this;
        }

        public Criteria andSizeNotIn(List<String> values) {
            addCriterion("table_a.\"size\" not in", values, "size");
            return (Criteria) this;
        }

        public Criteria andSizeBetween(String value1, String value2) {
            addCriterion("table_a.\"size\" between", value1, value2, "size");
            return (Criteria) this;
        }

        public Criteria andSizeNotBetween(String value1, String value2) {
            addCriterion("table_a.\"size\" not between", value1, value2, "size");
            return (Criteria) this;
        }

        public Criteria andStorageClassIsNull() {
            addCriterion("table_a.\"storage_class\" is null");
            return (Criteria) this;
        }

        public Criteria andStorageClassIsNotNull() {
            addCriterion("table_a.\"storage_class\" is not null");
            return (Criteria) this;
        }

        public Criteria andStorageClassEqualTo(String value) {
            addCriterion("table_a.\"storage_class\" =", value, "storageClass");
            return (Criteria) this;
        }

        public Criteria andStorageClassNotEqualTo(String value) {
            addCriterion("table_a.\"storage_class\" <>", value, "storageClass");
            return (Criteria) this;
        }

        public Criteria andStorageClassGreaterThan(String value) {
            addCriterion("table_a.\"storage_class\" >", value, "storageClass");
            return (Criteria) this;
        }

        public Criteria andStorageClassGreaterThanOrEqualTo(String value) {
            addCriterion("table_a.\"storage_class\" >=", value, "storageClass");
            return (Criteria) this;
        }

        public Criteria andStorageClassLessThan(String value) {
            addCriterion("table_a.\"storage_class\" <", value, "storageClass");
            return (Criteria) this;
        }

        public Criteria andStorageClassLessThanOrEqualTo(String value) {
            addCriterion("table_a.\"storage_class\" <=", value, "storageClass");
            return (Criteria) this;
        }

        public Criteria andStorageClassLike(String value) {
            addCriterion("table_a.\"storage_class\" like", value, "storageClass");
            return (Criteria) this;
        }

        public Criteria andStorageClassNotLike(String value) {
            addCriterion("table_a.\"storage_class\" not like", value, "storageClass");
            return (Criteria) this;
        }

        public Criteria andStorageClassIn(List<String> values) {
            addCriterion("table_a.\"storage_class\" in", values, "storageClass");
            return (Criteria) this;
        }

        public Criteria andStorageClassNotIn(List<String> values) {
            addCriterion("table_a.\"storage_class\" not in", values, "storageClass");
            return (Criteria) this;
        }

        public Criteria andStorageClassBetween(String value1, String value2) {
            addCriterion("table_a.\"storage_class\" between", value1, value2, "storageClass");
            return (Criteria) this;
        }

        public Criteria andStorageClassNotBetween(String value1, String value2) {
            addCriterion("table_a.\"storage_class\" not between", value1, value2, "storageClass");
            return (Criteria) this;
        }

        public Criteria andVersionIdIsNull() {
            addCriterion("table_a.\"version_id\" is null");
            return (Criteria) this;
        }

        public Criteria andVersionIdIsNotNull() {
            addCriterion("table_a.\"version_id\" is not null");
            return (Criteria) this;
        }

        public Criteria andVersionIdEqualTo(String value) {
            addCriterion("table_a.\"version_id\" =", value, "versionId");
            return (Criteria) this;
        }

        public Criteria andVersionIdNotEqualTo(String value) {
            addCriterion("table_a.\"version_id\" <>", value, "versionId");
            return (Criteria) this;
        }

        public Criteria andVersionIdGreaterThan(String value) {
            addCriterion("table_a.\"version_id\" >", value, "versionId");
            return (Criteria) this;
        }

        public Criteria andVersionIdGreaterThanOrEqualTo(String value) {
            addCriterion("table_a.\"version_id\" >=", value, "versionId");
            return (Criteria) this;
        }

        public Criteria andVersionIdLessThan(String value) {
            addCriterion("table_a.\"version_id\" <", value, "versionId");
            return (Criteria) this;
        }

        public Criteria andVersionIdLessThanOrEqualTo(String value) {
            addCriterion("table_a.\"version_id\" <=", value, "versionId");
            return (Criteria) this;
        }

        public Criteria andVersionIdLike(String value) {
            addCriterion("table_a.\"version_id\" like", value, "versionId");
            return (Criteria) this;
        }

        public Criteria andVersionIdNotLike(String value) {
            addCriterion("table_a.\"version_id\" not like", value, "versionId");
            return (Criteria) this;
        }

        public Criteria andVersionIdIn(List<String> values) {
            addCriterion("table_a.\"version_id\" in", values, "versionId");
            return (Criteria) this;
        }

        public Criteria andVersionIdNotIn(List<String> values) {
            addCriterion("table_a.\"version_id\" not in", values, "versionId");
            return (Criteria) this;
        }

        public Criteria andVersionIdBetween(String value1, String value2) {
            addCriterion("table_a.\"version_id\" between", value1, value2, "versionId");
            return (Criteria) this;
        }

        public Criteria andVersionIdNotBetween(String value1, String value2) {
            addCriterion("table_a.\"version_id\" not between", value1, value2, "versionId");
            return (Criteria) this;
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table RESOURCE
     *
     * @mbg.generated do_not_delete_during_merge Thu Oct 19 19:38:31 JST 2017
     */
    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table RESOURCE
     *
     * @mbg.generated Thu Oct 19 19:38:31 JST 2017
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