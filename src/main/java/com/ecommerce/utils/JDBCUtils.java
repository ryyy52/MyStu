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
    // 使用ThreadLocal存储当前线程的Connection，用于事务管理
    private static ThreadLocal<Connection> connectionThreadLocal = new ThreadLocal<>();

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
        // 先从ThreadLocal中获取Connection，如果存在则直接返回
        Connection conn = connectionThreadLocal.get();
        if (conn != null) {
            return conn;
        }
        
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
        // 从数据源获取新的Connection
        conn = dataSource.getConnection();
        return conn;
    }

    /**
     * 开启事务
     *
     * @throws SQLException SQL异常
     */
    public static void beginTransaction() throws SQLException {
        // 从ThreadLocal中获取Connection，如果不存在则创建
        Connection conn = connectionThreadLocal.get();
        if (conn != null) {
            throw new SQLException("事务已经开启，不能重复开启");
        }
        // 获取新的Connection
        conn = getConnection();
        // 设置自动提交为false
        conn.setAutoCommit(false);
        // 将Connection存储到ThreadLocal中
        connectionThreadLocal.set(conn);
    }

    /**
     * 提交事务
     *
     * @throws SQLException SQL异常
     */
    public static void commitTransaction() throws SQLException {
        // 从ThreadLocal中获取Connection
        Connection conn = connectionThreadLocal.get();
        if (conn == null) {
            throw new SQLException("事务尚未开启，不能提交");
        }
        // 提交事务
        conn.commit();
        // 关闭Connection
        conn.close();
        // 从ThreadLocal中移除Connection
        connectionThreadLocal.remove();
    }

    /**
     * 回滚事务
     *
     * @throws SQLException SQL异常
     */
    public static void rollbackTransaction() throws SQLException {
        // 从ThreadLocal中获取Connection
        Connection conn = connectionThreadLocal.get();
        if (conn == null) {
            throw new SQLException("事务尚未开启，不能回滚");
        }
        // 回滚事务
        conn.rollback();
        // 关闭Connection
        conn.close();
        // 从ThreadLocal中移除Connection
        connectionThreadLocal.remove();
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
        // 如果当前线程有事务连接，则不关闭传入的连接
        if (conn != null && connectionThreadLocal.get() == null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}