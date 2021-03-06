package com.chigix.resserver.mybatis.dao;

import com.chigix.resserver.mybatis.record.Chizuru;
import com.chigix.resserver.mybatis.record.ChizuruExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface ChizuruMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table CHIZURU
     *
     * @mbg.generated Thu Nov 23 23:18:14 JST 2017
     */
    long countByExample(ChizuruExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table CHIZURU
     *
     * @mbg.generated Thu Nov 23 23:18:14 JST 2017
     */
    int deleteByExample(ChizuruExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table CHIZURU
     *
     * @mbg.generated Thu Nov 23 23:18:14 JST 2017
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table CHIZURU
     *
     * @mbg.generated Thu Nov 23 23:18:14 JST 2017
     */
    int insert(Chizuru record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table CHIZURU
     *
     * @mbg.generated Thu Nov 23 23:18:14 JST 2017
     */
    int insertSelective(Chizuru record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table CHIZURU
     *
     * @mbg.generated Thu Nov 23 23:18:14 JST 2017
     */
    List<Chizuru> selectByExampleWithRowbounds(ChizuruExample example, RowBounds rowBounds);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table CHIZURU
     *
     * @mbg.generated Thu Nov 23 23:18:14 JST 2017
     */
    List<Chizuru> selectByExample(ChizuruExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table CHIZURU
     *
     * @mbg.generated Thu Nov 23 23:18:14 JST 2017
     */
    int updateByExampleSelective(@Param("record") Chizuru record, @Param("example") ChizuruExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table CHIZURU
     *
     * @mbg.generated Thu Nov 23 23:18:14 JST 2017
     */
    int updateByExample(@Param("record") Chizuru record, @Param("example") ChizuruExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table CHIZURU
     *
     * @mbg.generated Thu Nov 23 23:18:14 JST 2017
     */
    int updateByPrimaryKeySelective(Chizuru record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table CHIZURU
     *
     * @mbg.generated Thu Nov 23 23:18:14 JST 2017
     */
    int updateByPrimaryKey(Chizuru record);
}