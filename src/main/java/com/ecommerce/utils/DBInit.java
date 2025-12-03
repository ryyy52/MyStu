package com.ecommerce.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * 数据库初始化工具类 - 创建数据库和初始化表结构及数据
 * 
 * 职责：
 * 1. 创建数据库（如果不存在）
 * 2. 初始化数据库表结构
 * 3. 插入初始化数据（分类、商品等）
 * 4. 运行自定义初始化脚本
 * 
 * 主要功能：
 * - 创建ecommerce_new数据库
 * - 检查表结构是否完整
 * - 自动执行SQL初始化脚本
 * - 支持增量初始化（避免重复导入）
 * 
 * 执行流程：
 * 1. 连接到MySQL服务器（默认端口）
 * 2. 创建数据库（如果不存在）
 * 3. 检查是否需要初始化：
 *    - product表是否存在
 *    - 表是否有唯一约束
 *    - 是否已有数据
 * 4. 如需初始化，执行ecommerce_init.sql脚本
 * 5. 验证初始化是否成功
 * 
 * 配置参数：
 * - 数据库地址: localhost:3306
 * - 数据库名称: ecommerce_new
 * - 字符集: utf8mb4 (支持表情符号等)
 * - 用户名: 通过 -Ddb.user 指定，默认 root
 * - 密码: 通过 -Ddb.pass 指定，默认 liuweifeng233
 * 
 * SQL脚本文件：
 * - 位置: src/main/resources/sql/ecommerce_init.sql
 * - 内容: 包含表创建、索引、初始数据等
 * 
 * 运行方式：
 * 1. 作为主程序运行：java -Ddb.user=root -Ddb.pass=password DBInit
 * 2. 项目启动时通过DatabaseInitListener自动运行
 * 
 * 注意事项：
 * - 只在第一次运行时执行初始化
 * - 不会覆盖已存在的数据
 * - 需要MySQL数据库可访问
 * - 需要有CREATE DATABASE权限
 */
public class DBInit {
    /**
     * 主程序入口 - 手动运行数据库初始化
     * 
     * 执行步骤：
     * 1. 定义数据库连接参数
     * 2. 加载MySQL JDBC驱动
     * 3. 创建ecommerce_new数据库
     * 4. 检查是否需要初始化表
     * 5. 执行初始化SQL脚本
     * 6. 输出初始化结果日志
     * 
     * 使用命令：
     * java -Ddb.user=root -Ddb.pass=password DBInit
     * 
     * @param args 命令行参数（未使用）
     * @throws Exception 数据库操作异常
     */
    public static void main(String[] args) throws Exception {
        // 步骤1：定义连接参数
        // hostUrl用于创建数据库（连接到MySQL服务器，不指定具体数据库）
        String hostUrl = "jdbc:mysql://localhost:3306/?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        // dbUrl用于初始化表和数据（连接到具体的ecommerce_new数据库）
        String dbUrl = "jdbc:mysql://localhost:3306/ecommerce_new?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        // 获取数据库用户名（从系统属性或使用默认值）
        String username = System.getProperty("db.user", "root");
        // 获取数据库密码（从系统属性或使用默认值）
        String password = System.getProperty("db.pass", "liuweifeng233");

        // 步骤2：加载MySQL JDBC驱动
        Class.forName("com.mysql.cj.jdbc.Driver");

        // 步骤3：创建数据库
        try (Connection conn = DriverManager.getConnection(hostUrl, username, password);
             Statement st = conn.createStatement()) {
            // 使用CREATE DATABASE IF NOT EXISTS防止错误
            // DEFAULT CHARACTER SET utf8mb4支持表情等特殊字符
            st.execute("CREATE DATABASE IF NOT EXISTS ecommerce_new DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
            System.out.println("数据库创建或已存在");
        }
        
        // 步骤4：检查是否需要初始化
        boolean needInit = false;
        try (Connection conn = DriverManager.getConnection(dbUrl, username, password);
             Statement st = conn.createStatement()) {
            
            // 检查product表是否存在
            ResultSet rs = st.executeQuery("SHOW TABLES LIKE 'product'");
            if (!rs.next()) {
                // 表不存在，需要初始化
                needInit = true;
                System.out.println("product表不存在，需要初始化");
            } else {
                // 表存在，检查是否有唯一约束（表示已初始化）
                rs = st.executeQuery("SHOW INDEX FROM product WHERE Key_name = 'uk_name_category'");
                if (!rs.next()) {
                    // 没有唯一约束，需要初始化
                    needInit = true;
                    System.out.println("product表缺少唯一约束，需要初始化");
                } else {
                    // 检查是否已有数据
                    rs = st.executeQuery("SELECT COUNT(*) FROM product");
                    rs.next();
                    int count = rs.getInt(1);
                    if (count == 0) {
                        // 表结构完整但数据为空，需要初始化
                        needInit = true;
                        System.out.println("product表为空，需要初始化数据");
                    }
                }
            }
        }
        
        // 步骤5：执行初始化
        if (needInit) {
            // 读取并执行SQL脚本
            try (Connection conn = DriverManager.getConnection(dbUrl, username, password);
                 Statement st = conn.createStatement()) {
                // 读取SQL脚本文件
                String sqlScript = readSqlScript("sql/ecommerce_init.sql");
                // 执行SQL脚本
                executeSqlScript(st, sqlScript);
                System.out.println("数据库初始化完成");
            }
        } else {
            // 数据库已存在且完整，跳过初始化
            System.out.println("数据库已存在且包含数据，跳过初始化");
        }
    }

    /**
     * 读取SQL脚本文件 - 从resources目录加载SQL脚本
     * 
     * 功能：
     * 1. 从classpath资源目录读取文件
     * 2. 逐行读取脚本
     * 3. 过滤注释和空行
     * 4. 返回清理后的SQL脚本
     * 
     * @param scriptPath 脚本文件路径（相对于classpath）
     * @return 清理后的SQL脚本字符串
     * @throws Exception 文件读取异常
     */
    private static String readSqlScript(String scriptPath) throws Exception {
        StringBuilder sb = new StringBuilder();
        // 使用try-with-resources确保资源自动关闭
        try (InputStream is = DBInit.class.getClassLoader().getResourceAsStream(scriptPath);
             BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
            String line;
            // 逐行读取脚本
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
     * 1. 按;分割SQL脚本
     * 2. 逐条执行SQL语句
     * 3. 忽略空语句
     * 
     * 注意：
     * - 只支持单行SQL语句或简单的多行语句
     * - 复杂的SQL（如存储过程）可能需要特殊处理
     * 
     * @param st Statement对象用于执行SQL
     * @param sqlScript 完整的SQL脚本
     * @throws Exception SQL执行异常
     */
    private static void executeSqlScript(Statement st, String sqlScript) throws Exception {
        // 分割SQL语句（每条语句以;结尾）
        String[] sqlStatements = sqlScript.split(";");
        // 逐条执行
        for (String sql : sqlStatements) {
            // 去除前后空白
            String trimmedSql = sql.trim();
            // 跳过空语句
            if (!trimmedSql.isEmpty()) {
                // 执行SQL语句
                st.execute(trimmedSql);
            }
        }
    }
}