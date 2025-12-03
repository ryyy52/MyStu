package com.ecommerce.utils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * 数据库初始化监听器 - 应用启动时自动初始化数据库
 * 
 * 职责：
 * 1. 监听Servlet容器启动事件
 * 2. 创建数据库（如果不存在）
 * 3. 初始化数据库表结构
 * 4. 插入初始化数据
 * 5. 输出初始化日志
 * 
 * 工作原理：
 * - 实现ServletContextListener接口
 * - 在应用启动时自动被调用
 * - 在应用关闭时执行清理操作
 * 
 * 执行流程：
 * 1. contextInitialized() 被调用
 * 2. 获取数据库连接参数
 * 3. 加载MySQL驱动
 * 4. 创建ecommerce_new数据库
 * 5. 检查是否需要初始化
 * 6. 执行SQL初始化脚本
 * 7. 验证初始化结果
 * 8. 输出初始化日志
 * 
 * 配置注册：
 * 方式一: 在web.xml中声明
 * <listener>
 *     <listener-class>com.ecommerce.utils.DatabaseInitListener</listener-class>
 * </listener>
 * 
 * 方式二: 使用@WebListener注解
 * 
 * 配置参数：
 * - 数据库地址: localhost:3306
 * - 数据库名称: ecommerce_new
 * - 用户名: 通过-Ddb.user系统属性指定，默认root
 * - 密码: 通过-Ddb.pass系统属性指定，默认liuweifeng233
 * 
 * 初始化脚本：
 * - 路径: classpath:sql/ecommerce_init.sql
 * - 包含: 表创建、索引、初始数据等
 * 
 * 日志输出：
 * 初始化成功/失败信息输出到控制台
 * 便于开发人员调试
 * 
 * 特点：
 * - 自动化初始化，无需手动执行
 * - 只在第一次运行时执行初始化
 * - 不会覆盖已存在的数据
 * - 应用启动过程中执行
 * - 支持自定义数据库连接参数
 */
public class DatabaseInitListener implements ServletContextListener {
    /**
     * 应用启动时调用 - 初始化数据库和表结构
     * 
     * 执行时机：
     * - 应用部署到服务器时自动调用
     * - Servlet容器完成启动时调用
     * - 在任何Servlet初始化之前调用
     * 
     * 执行步骤：
     * 1. 定义数据库连接参数
     * 2. 加载MySQL JDBC驱动
     * 3. 创建ecommerce_new数据库
     * 4. 检查是否需要初始化表
     * 5. 执行初始化SQL脚本
     * 6. 输出初始化结果
     * 
     * @param sce Servlet容器事件，包含应用上下文
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // 定义连接参数
        String hostUrl = "jdbc:mysql://localhost:3306/?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        String dbUrl = "jdbc:mysql://localhost:3306/ecommerce_new?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        // 获取数据库用户名和密码（从系统属性或使用默认值）
        String username = System.getProperty("db.user", "root");
        String password = System.getProperty("db.pass", "liuweifeng233");
        try {
            // 加载MySQL JDBC驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // 创建数据库
            try (Connection conn = DriverManager.getConnection(hostUrl, username, password);
                 Statement st = conn.createStatement()) {
                // 创建数据库（如果不存在）
                st.execute("CREATE DATABASE IF NOT EXISTS ecommerce_new DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
                System.out.println("数据库创建或已存在");
            }
            
            // 检查是否需要初始化数据库表和数据
            boolean needInit = false;
            
            // 检查user表是否有记录，用来判断是否初始化过
            try (Connection conn = DriverManager.getConnection(dbUrl, username, password);
                 Statement st = conn.createStatement()) {
                // 检查user表是否存在
                ResultSet rs = st.executeQuery("SHOW TABLES LIKE 'user'");
                if (rs.next()) {
                    // user表存在，检查是否有数据
                    rs = st.executeQuery("SELECT COUNT(*) FROM user");
                    if (rs.next() && rs.getInt(1) == 0) {
                        // 表存在但数据为空，需要初始化
                        needInit = true;
                        System.out.println("user表为空，需要初始化数据");
                    }
                } else {
                    // user表不存在，需要初始化
                    needInit = true;
                    System.out.println("user表不存在，需要初始化");
                }
            }
            
            // 执行初始化
            if (needInit) {
                // 读取并执行SQL脚本
                try (Connection conn = DriverManager.getConnection(dbUrl, username, password);
                     Statement st = conn.createStatement()) {
                    // 切换到目标数据库
                    st.execute("USE ecommerce_new");
                    // 读取SQL初始化脚本
                    String sqlScript = readSqlScript("sql/ecommerce_init.sql");
                    // 移除脚本中可能包含的USE语句，避免重复或切换错误
                    sqlScript = sqlScript.replace("USE ecommerce_new;", "");
                    // 执行SQL脚本初始化表和数据
                    executeSqlScript(st, sqlScript);
                    System.out.println("数据库初始化完成");
                }
            } else {
                // 数据库已初始化，跳过
                System.out.println("数据库已存在且包含数据，跳过初始化");
            }
        } catch (Exception e) {
            // 初始化异常，打印堆栈追踪便于调试
            System.err.println("数据库初始化失败:");
            e.printStackTrace();
        }
    }

    /**
     * 应用关闭时调用 - 清理资源
     * @param sce Servlet容器事件
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // 应用关闭时调用，可用于清理数据库连接等资源
        System.out.println("应用关闭，清理资源");
    }

    /**
     * 读取SQL脚本文件 - 从classpath资源加载SQL脚本
     * 
     * 功能：
     * 1. 从classpath目录读取SQL脚本文件
     * 2. 逐行读取，过滤注释和空行
     * 3. 返回清理后的SQL脚本
     * 
     * @param scriptPath 脚本文件路径（相对于classpath）
     * @return 清理后的SQL脚本字符串
     * @throws Exception 文件读取异常
     */
    private static String readSqlScript(String scriptPath) throws Exception {
        StringBuilder sb = new StringBuilder();
        // try-with-resources确保流自动关闭
        try (InputStream is = DatabaseInitListener.class.getClassLoader().getResourceAsStream(scriptPath);
             BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
            String line;
            // 逐行读取文件
            while ((line = br.readLine()) != null) {
                // 过滤SQL注释（--开头）和空行
                if (!line.trim().startsWith("--") && !line.trim().isEmpty()) {
                    // 保留有效的SQL语句
                    sb.append(line).append("\n");
                }
            }
        }
        return sb.toString();
    }

    /**
     * 执行SQL脚本 - 分割并执行多条SQL语句
     * 
     * 功能：
     * 1. 按;分割SQL脚本为多条语句
     * 2. 逐条执行SQL
     * 3. 忽略空语句
     * 
     * @param st Statement对象用于执行SQL
     * @param sqlScript 完整的SQL脚本（可能包含多条语句）
     * @throws Exception SQL执行异常
     */
    private static void executeSqlScript(Statement st, String sqlScript) throws Exception {
        // 按;分割SQL语句
        String[] sqlStatements = sqlScript.split(";");
        // 逐条执行
        for (String sql : sqlStatements) {
            // 去除前后空白
            String trimmedSql = sql.trim();
            // 只执行非空语句
            if (!trimmedSql.isEmpty()) {
                // 执行SQL语句
                st.execute(trimmedSql);
            }
        }
    }
}