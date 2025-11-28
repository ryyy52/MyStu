package com.ecommerce;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * 数据库连接测试类
 */
public class TestDBConnection {
    public static void main(String[] args) {
        String hostUrl = "jdbc:mysql://localhost:3306/?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        String dbUrl = "jdbc:mysql://localhost:3306/ecommerce?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        String username = "root";
        String password = "liuweifeng233";
        
        try {
            // 加载驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("1. 驱动加载成功");
            
            // 测试连接到MySQL服务器
            try (Connection conn = DriverManager.getConnection(hostUrl, username, password)) {
                System.out.println("2. 成功连接到MySQL服务器");
                
                // 创建数据库
                try (Statement st = conn.createStatement()) {
                    st.execute("CREATE DATABASE IF NOT EXISTS ecommerce DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
                    System.out.println("3. 成功创建数据库");
                }
            }
            
            // 测试连接到ecommerce数据库
            try (Connection conn = DriverManager.getConnection(dbUrl, username, password)) {
                System.out.println("4. 成功连接到ecommerce数据库");
                
                // 测试执行简单查询
                try (Statement st = conn.createStatement()) {
                    // 查询数据库中的表
                    st.executeQuery("SHOW TABLES");
                    System.out.println("5. 成功执行查询操作");
                }
            }
            
            System.out.println("\n数据库连接测试通过！");
        } catch (Exception e) {
            System.err.println("数据库连接测试失败：" + e.getMessage());
            e.printStackTrace();
        }
    }
}