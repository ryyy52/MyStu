package com.ecommerce.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DBInit {
    public static void main(String[] args) throws Exception {
        String hostUrl = "jdbc:mysql://localhost:3306/?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        String dbUrl = "jdbc:mysql://localhost:3306/ecommerce?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        String username = System.getProperty("db.user", "root");
        String password = System.getProperty("db.pass", "liuweifeng233");

        Class.forName("com.mysql.cj.jdbc.Driver");

        // 创建数据库
        try (Connection conn = DriverManager.getConnection(hostUrl, username, password);
             Statement st = conn.createStatement()) {
            st.execute("CREATE DATABASE IF NOT EXISTS ecommerce DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
        }
        
        // 检查是否需要初始化（如果product表为空或不存在唯一约束）
        boolean needInit = false;
        try (Connection conn = DriverManager.getConnection(dbUrl, username, password);
             Statement st = conn.createStatement()) {
            
            // 检查product表是否存在
            ResultSet rs = st.executeQuery("SHOW TABLES LIKE 'product'");
            if (!rs.next()) {
                needInit = true;
            } else {
                // 检查表结构是否包含唯一约束
                rs = st.executeQuery("SHOW INDEX FROM product WHERE Key_name = 'uk_name_category'");
                if (!rs.next()) {
                    needInit = true;
                } else {
                    // 检查是否已有数据
                    rs = st.executeQuery("SELECT COUNT(*) FROM product");
                    rs.next();
                    int count = rs.getInt(1);
                    if (count == 0) {
                        needInit = true;
                    }
                }
            }
        }
        
        // 只有在需要时才执行初始化
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
            System.out.println("数据库已存在且包含数据，跳过初始化");
        }
    }

    /**
     * 读取SQL脚本文件
     */
    private static String readSqlScript(String scriptPath) throws Exception {
        StringBuilder sb = new StringBuilder();
        try (InputStream is = DBInit.class.getClassLoader().getResourceAsStream(scriptPath);
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