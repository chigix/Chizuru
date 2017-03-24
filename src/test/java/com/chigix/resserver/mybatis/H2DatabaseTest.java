package com.chigix.resserver.mybatis;

import java.sql.Connection;
import java.sql.SQLException;
import org.apache.ibatis.jdbc.SQL;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.Test;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class H2DatabaseTest {

    @Test
    public void testH2Start() throws SQLException {
        System.out.println("testH2Start");
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:./data/test");
        ds.setUser("Chizuru");
        Connection conn = ds.getConnection();
        conn.createStatement().execute("CREATE TABLE bankai;");
        conn.commit();
        conn.close();
        new SQL() {
            {
                SELECT("BANKAI");
            }
        };
    }

}
