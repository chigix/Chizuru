package com.chigix.resserver.mybatis;

import java.io.IOException;
import java.io.InputStream;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class TestUtils {

    public static final SqlSession setUpDatabase() throws IOException {
        InputStream in = Resources.getResourceAsStream("com/chigix/resserver/mybatis/mybatis-config.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(in);
        SqlSession session = sqlSessionFactory.openSession();
        return session;
    }
}
