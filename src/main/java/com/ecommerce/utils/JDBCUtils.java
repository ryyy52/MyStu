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
 * 数据库连接工具类 - 数据库连接和资源管理的工具库
 * 
 * 职责：
 * 1. 管理数据库连接池（使用Apache Commons DBCP2）
 * 2. 提供获取数据库连接的方法
 * 3. 支持事务管理（通过ThreadLocal）
 * 4. 提供SQL执行和资源释放的便利方法
 * 5. 管理PreparedStatement和ResultSet资源
 * 
 * 主要功能：
 * - getConnection(): 获取数据库连接
 * - getConnectionWithoutPool(): 不使用连接池获取连接
 * - beginTransaction(): 开始事务
 * - commit(): 提交事务
 * - rollback(): 回滚事务
 * - closeConnection(): 关闭连接释放资源
 * - closePreparedStatement(): 关闭PreparedStatement
 * - closeResultSet(): 关闭ResultSet
 * 
 * 特点：
 * - 使用数据库连接池提高性能
 * - 使用ThreadLocal支持事务管理
 * - 从db.properties配置文件加载数据库连接信息
 * - 支持自动释放资源防止连接泄漏
 * - 线程安全的连接获取
 * 
 * 配置文件（db.properties）：
 * - jdbc.driver: 数据库驱动类名
 * - jdbc.url: 数据库连接URL
 * - jdbc.username: 数据库用户名
 * - jdbc.password: 数据库密码
 * - initialSize: 初始连接数
 * - maxTotal: 最大连接数
 * 
 * 使用示例：
 * 1. 普通查询：Connection conn = JDBCUtils.getConnection();
 * 2. 事务操作：
 *    JDBCUtils.beginTransaction();
 *    try {
 *        // 执行数据库操作
 *        JDBCUtils.commit();
 *    } catch(Exception e) {
 *        JDBCUtils.rollback();
 *    }
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
     * 获取数据库连接 - 支持从连接池获取或直接获取
     * 
     * 实现策略：
     * 1. 首先检查ThreadLocal中是否存在事务连接
     * 2. 如果存在事务连接，直接返回（用于事务处理）
     * 3. 如果没有，检查数据源是否已初始化
     * 4. 如果数据源未初始化，进行双重检查锁定初始化
     * 5. 从数据源获取新的连接
     * 
     * 连接池优势：
     * - 复用连接，减少创建/销毁的开销
     * - 自动管理空闲连接
     * - 提供连接超时控制
     * - 支持最大连接数限制
     * 
     * ThreadLocal优势：
     * - 为每个线程提供独立的连接
     * - 支持事务管理
     * - 线程安全
     *
     * @return 数据库连接对象
     * @throws SQLException 数据库连接失败时抛出
     */
    public static Connection getConnection() throws SQLException {
        // 步骤1：从ThreadLocal中获取当前线程的Connection
        Connection conn = connectionThreadLocal.get();
        // 步骤2：如果存在事务连接，直接返回
        // 这确保同一事务中的所有操作使用相同的连接
        if (conn != null) {
            return conn;
        }
        
        // 步骤3：检查数据源是否已初始化
        if (dataSource == null) {
            // 步骤4：使用synchronized和双重检查锁定模式初始化数据源
            synchronized (JDBCUtils.class) {
                // 再次检查dataSource是否为null
                if (dataSource == null) {
                    try {
                        // 创建BasicDataSource实例（Apache Commons DBCP2的实现）
                        BasicDataSource ds = new BasicDataSource();
                        // 设置数据库驱动类名（如：com.mysql.jdbc.Driver）
                        ds.setDriverClassName(properties.getProperty("jdbc.driver"));
                        // 设置数据库连接URL（如：jdbc:mysql://localhost:3306/ecommerce）
                        ds.setUrl(properties.getProperty("jdbc.url"));
                        // 设置数据库用户名
                        ds.setUsername(properties.getProperty("jdbc.username"));
                        // 设置数据库密码
                        ds.setPassword(properties.getProperty("jdbc.password"));
                        // 设置初始连接池大小（默认5个连接）
                        ds.setInitialSize(Integer.parseInt(properties.getProperty("initialSize", "5")));
                        // 设置最大连接数（默认10个连接）
                        ds.setMaxTotal(Integer.parseInt(properties.getProperty("maxTotal", "10")));
                        // 设置最大空闲连接数（默认5个）
                        ds.setMaxIdle(Integer.parseInt(properties.getProperty("maxIdle", "5")));
                        // 设置最小空闲连接数（默认1个）
                        ds.setMinIdle(Integer.parseInt(properties.getProperty("minIdle", "1")));
                        // 设置获取连接的最长等待时间（毫秒，默认3000毫秒）
                        ds.setMaxWaitMillis(Long.parseLong(properties.getProperty("maxWaitMillis", "3000")));
                        // 保存初始化的数据源
                        dataSource = ds;
                    } catch (Exception e) {
                        // 初始化失败，抛出SQLException
                        throw new SQLException("Failed to initialize data source", e);
                    }
                }
            }
        }
        // 步骤5：从数据源获取新的连接
        conn = dataSource.getConnection();
        // 返回连接
        return conn;
    }

    /**
     * 开启事务 - 为当前线程开启一个数据库事务
     * 
     * 事务特性：
     * 1. 获取新的数据库连接
     * 2. 禁用自动提交
     * 3. 将连接存储到ThreadLocal中
     * 4. 后续操作将使用这个连接
     * 
     * 事务ACID特性：
     * - Atomicity（原子性）：操作要么全部成功，要么全部失败
     * - Consistency（一致性）：数据库从一个一致的状态转移到另一个一致的状态
     * - Isolation（隔离性）：并发事务不会相互影响
     * - Durability（持久性）：提交后的数据永久保存
     * 
     * 使用示例：
     * try {
     *     JDBCUtils.beginTransaction();
     *     // 执行多个数据库操作
     *     JDBCUtils.commitTransaction();
     * } catch (Exception e) {
     *     JDBCUtils.rollbackTransaction();
     * }
     *
     * @throws SQLException 开启事务失败时抛出
     */
    public static void beginTransaction() throws SQLException {
        // 步骤1：从ThreadLocal中获取当前线程的Connection
        Connection conn = connectionThreadLocal.get();
        // 步骤2：检查是否已经开启事务
        if (conn != null) {
            // 已经存在事务连接，不能重复开启
            throw new SQLException("事务已经开启，不能重复开启");
        }
        // 步骤3：获取新的连接（不从ThreadLocal中获取）
        conn = getConnection();
        // 步骤4：禁用自动提交（默认情况下JDBC自动提交每个SQL语句）
        // 设置为false后，需要手动调用commit()才能提交事务
        conn.setAutoCommit(false);
        // 步骤5：将连接保存到ThreadLocal中，供本线程的其他操作使用
        connectionThreadLocal.set(conn);
    }

    /**
     * 提交事务 - 提交当前线程的事务
     * 
     * 实现流程：
     * 1. 从ThreadLocal获取事务连接
     * 2. 检查事务是否已开启
     * 3. 调用commit()提交所有更改
     * 4. 关闭连接
     * 5. 移除ThreadLocal中的连接
     * 
     * 提交后：
     * - 所有未提交的更改将被持久化到数据库
     * - 所有锁都会被释放
     * - 连接会回到连接池
     *
     * @throws SQLException 提交失败时抛出
     */
    public static void commitTransaction() throws SQLException {
        // 从ThreadLocal中获取事务连接
        Connection conn = connectionThreadLocal.get();
        if (conn == null) {
            // 事务未开启，不能提交
            throw new SQLException("事务尚未开启，不能提交");
        }
        try {
            // 提交事务（将所有未提交的更改写入数据库）
            conn.commit();
        } finally {
            // 步骤4：关闭连接（返回到连接池）
            conn.close();
            // 步骤5：从ThreadLocal中移除连接（清除线程本地变量）
            connectionThreadLocal.remove();
        }
    }

    /**
     * 回滚事务 - 撤销当前线程事务中的所有操作
     * 
     * 实现流程：
     * 1. 从ThreadLocal获取事务连接
     * 2. 检查事务是否已开启
     * 3. 调用rollback()撤销所有更改
     * 4. 关闭连接
     * 5. 移除ThreadLocal中的连接
     * 
     * 回滚后：
     * - 自上次提交以来的所有更改都被撤销
     * - 数据库恢复到事务开始前的状态
     * - 所有锁都会被释放
     * - 连接会回到连接池
     * 
     * 使用场景：
     * - 数据操作出错时回滚
     * - 业务规则验证失败时回滚
     * - 其他事务操作异常时回滚
     *
     * @throws SQLException 回滚失败时抛出
     */
    public static void rollbackTransaction() throws SQLException {
        // 从ThreadLocal中获取事务连接
        Connection conn = connectionThreadLocal.get();
        if (conn == null) {
            // 事务未开启，不能回滚
            throw new SQLException("事务尚未开启，不能回滚");
        }
        try {
            // 回滚事务（撤销所有未提交的更改）
            conn.rollback();
        } finally {
            // 关闭连接（返回到连接池）
            conn.close();
            // 从ThreadLocal中移除连接（清除线程本地变量）
            connectionThreadLocal.remove();
        }
    }

    /**
     * 获取数据源 - 返回配置的数据源实例
     * 
     * @return 数据源对象（Apache Commons DBCP2的BasicDataSource）
     */
    public static DataSource getDataSource() {
        return dataSource;
    }
    
    /**
     * 关闭资源 - 关闭Connection、PreparedStatement和ResultSet
     * 
     * 资源管理非常重要：
     * - 未关闭的连接会导致连接泄漏
     * - 数据库连接是有限的资源，泄漏会耗尽连接池
     * - 未关闭的ResultSet会占用内存
     * - 未关闭的PreparedStatement会占用内存
     * 
     * 实现细节：
     * - 按照ResultSet -> PreparedStatement -> Connection的顺序关闭
     * - 这个顺序很重要，因为PreparedStatement依赖Connection
     * - 在finally块中调用确保资源一定会被释放
     * - 不关闭事务连接（由事务管理器负责）
     * 
     * 使用示例：
     * Connection conn = null;
     * PreparedStatement ps = null;
     * ResultSet rs = null;
     * try {
     *     conn = JDBCUtils.getConnection();
     *     ps = conn.prepareStatement("SELECT * FROM users");
     *     rs = ps.executeQuery();
     * } finally {
     *     JDBCUtils.close(conn, ps, rs);
     * }
     *
     * @param conn Connection对象（可能为null）
     * @param ps   PreparedStatement对象（可能为null）
     * @param rs   ResultSet对象（可能为null）
     */
    public static void close(Connection conn, PreparedStatement ps, ResultSet rs) {
        // 步骤1：关闭ResultSet（首先释放查询结果集）
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                // 关闭异常，记录日志但继续关闭其他资源
                e.printStackTrace();
            }
        }
        // 步骤2：关闭PreparedStatement（然后释放预处理语句）
        if (ps != null) {
            try {
                ps.close();
            } catch (SQLException e) {
                // 关闭异常，记录日志但继续关闭其他资源
                e.printStackTrace();
            }
        }
        // 步骤3：关闭Connection（最后释放连接）
        // 重要：检查当前线程是否有事务在进行
        // 如果有事务，则不关闭此连接（事务管理器会处理）
        // 如果没有事务，则关闭连接以回到连接池
        if (conn != null && connectionThreadLocal.get() == null) {
            try {
                conn.close();
            } catch (SQLException e) {
                // 关闭异常，记录日志
                e.printStackTrace();
            }
        }
    }
}