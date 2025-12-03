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

public class DatabaseInitListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        String hostUrl = "jdbc:mysql://localhost:3306/?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        String dbUrl = "jdbc:mysql://localhost:3306/ecommerce_new?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        String username = System.getProperty("db.user", "root");
        String password = System.getProperty("db.pass", "liuweifeng233");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            // 创建数据库
            try (Connection conn = DriverManager.getConnection(hostUrl, username, password);
                 Statement st = conn.createStatement()) {
                st.execute("CREATE DATABASE IF NOT EXISTS ecommerce_new DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
            }
            
            // 检查是否需要初始化数据库
            boolean needInit = false;
            
            // 检查user表是否有记录，如果没有记录则需要初始化
            try (Connection conn = DriverManager.getConnection(dbUrl, username, password);
                 Statement st = conn.createStatement()) {
                // 检查表是否存在
                ResultSet rs = st.executeQuery("SHOW TABLES LIKE 'user'");
                if (rs.next()) {
                    // 表存在，检查是否有记录
                    rs = st.executeQuery("SELECT COUNT(*) FROM user");
                    if (rs.next() && rs.getInt(1) == 0) {
                        // 表存在但没有记录，需要初始化
                        needInit = true;
                    }
                } else {
                    // 表不存在，需要初始化
                    needInit = true;
                }
            }
            
            // 只有在需要时才执行初始化
            if (needInit) {
                // 读取并执行SQL脚本
                try (Connection conn = DriverManager.getConnection(dbUrl, username, password);
                     Statement st = conn.createStatement()) {
                    // 先切换到正确的数据库
                    st.execute("USE ecommerce_new");
                    // 读取SQL脚本文件
                    String sqlScript = readSqlScript("sql/ecommerce_init.sql");
                    // 移除脚本中的USE语句，避免切换到错误的数据库
                    sqlScript = sqlScript.replace("USE ecommerce_new;", "");
                    // 执行SQL脚本
                    executeSqlScript(st, sqlScript);
                    System.out.println("数据库初始化完成");
                }
            } else {
                System.out.println("数据库已存在且包含数据，跳过初始化");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }

    /**
     * 读取SQL脚本文件
     */
    private static String readSqlScript(String scriptPath) throws Exception {
        StringBuilder sb = new StringBuilder();
        try (InputStream is = DatabaseInitListener.class.getClassLoader().getResourceAsStream(scriptPath);
             BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
            String line;
            while ((line = br.readLine()) != null) {
                // 跳过注释和空行
                if (!line.trim().startsWith("--") && !line.trim().isEmpty()) {
                    sb.append(line).append("\n");
                }
            }
        }
        return sb.toString();
    }

    /**
     * 执行SQL脚本
     */
    private static void executeSqlScript(Statement st, String sqlScript) throws Exception {
        // 分割SQL语句
        String[] sqlStatements = sqlScript.split(";");
        for (String sql : sqlStatements) {
            String trimmedSql = sql.trim();
            if (!trimmedSql.isEmpty()) {
                st.execute(trimmedSql);
            }
        }
    }
}