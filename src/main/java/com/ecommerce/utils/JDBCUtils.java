package com.ecommerce.utils;

import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * 数据库连接工具类
 */
public class JDBCUtils {

    private static DataSource dataSource;
    private static Properties properties;

    static {
        // 加载数据库配置文件
        properties = new Properties();
        try (InputStream is = JDBCUtils.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (is != null) {
                properties.load(is);
            } else {
                throw new RuntimeException("db.properties file not found");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load db.properties", e);
        }
    }

    /**
     * 获取数据库连接
     *
     * @return 数据库连接
     * @throws SQLException SQL异常
     */
    public static Connection getConnection() throws SQLException {
        // 如果数据源尚未初始化，则进行初始化
        if (dataSource == null) {
            synchronized (JDBCUtils.class) {
                if (dataSource == null) {
                    try {
                        // 使用配置文件初始化数据源
                        BasicDataSource ds = new BasicDataSource();
                        ds.setDriverClassName(properties.getProperty("jdbc.driver"));
                        ds.setUrl(properties.getProperty("jdbc.url"));
                        ds.setUsername(properties.getProperty("jdbc.username"));
                        ds.setPassword(properties.getProperty("jdbc.password"));
                        ds.setInitialSize(Integer.parseInt(properties.getProperty("initialSize", "5")));
                        ds.setMaxTotal(Integer.parseInt(properties.getProperty("maxTotal", "10")));
                        ds.setMaxIdle(Integer.parseInt(properties.getProperty("maxIdle", "5")));
                        ds.setMinIdle(Integer.parseInt(properties.getProperty("minIdle", "1")));
                        ds.setMaxWaitMillis(Long.parseLong(properties.getProperty("maxWaitMillis", "3000")));
                        dataSource = ds;
                    } catch (Exception e) {
                        throw new SQLException("Failed to initialize data source", e);
                    }
                }
            }
        }
        return dataSource.getConnection();
    }

    /**
     * 获取数据源
     *
     * @return 数据源
     */
    public static DataSource getDataSource() {
        return dataSource;
    }
    
    /**
     * 关闭资源
     *
     * @param conn 数据库连接
     * @param ps   PreparedStatement对象
     * @param rs   ResultSet对象
     */
    public static void close(Connection conn, PreparedStatement ps, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (ps != null) {
            try {
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}