package com.ecommerce.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;

/**
 * 清理数据库中的冗余数据
 */
public class CleanupDatabase {
    public static void main(String[] args) {
        String dbUrl = "jdbc:mysql://localhost:3306/ecommerce?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        String username = "root";
        String password = "liuweifeng233";
        
        try {
            // 加载驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("1. 驱动加载成功");
            
            // 连接数据库
            try (Connection conn = DriverManager.getConnection(dbUrl, username, password)) {
                System.out.println("2. 成功连接到数据库");
                
                // 清理冗余分类数据
                cleanupRedundantCategories(conn);
                
                // 验证清理结果
                verifyCleanupResult(conn);
                
            }
            
        } catch (Exception e) {
            System.err.println("数据库操作失败：" + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 清理冗余分类数据
     */
    private static void cleanupRedundantCategories(Connection conn) throws Exception {
        System.out.println("\n=== 开始清理冗余分类数据 ===");
        
        try (Statement st = conn.createStatement()) {
            // 1. 先确保分类表有唯一约束
            System.out.println("1. 检查并添加分类表唯一约束");
            try {
                st.execute("ALTER TABLE category ADD UNIQUE KEY uk_name_parent (name, parent_id)");
                System.out.println("   ✓ 成功添加唯一约束");
            } catch (Exception e) {
                System.out.println("   ✓ 唯一约束已存在，跳过");
            }
            
            // 2. 清理冗余的一级分类
            System.out.println("2. 清理冗余的一级分类");
            String[] mainCategories = {"电子产品", "服饰鞋包", "家居用品", "服装鞋帽", "食品饮料"};
            
            for (String categoryName : mainCategories) {
                // 保留ID最小的那个分类，删除其他重复的
                String sql = "DELETE FROM category WHERE name = '" + categoryName + "' AND parent_id = 0 AND id NOT IN (SELECT MIN(id) FROM category WHERE name = '" + categoryName + "' AND parent_id = 0)";
                int affectedRows = st.executeUpdate(sql);
                System.out.println("   ✓ 清理了 " + affectedRows + " 个重复的 '" + categoryName + "' 一级分类");
            }
            
            // 3. 清理冗余的二级分类
            System.out.println("3. 清理冗余的二级分类");
            // 先获取所有一级分类
            ResultSet rs = st.executeQuery("SELECT id, name FROM category WHERE parent_id = 0");
            
            while (rs.next()) {
                int parentId = rs.getInt("id");
                String parentName = rs.getString("name");
                
                // 对于每个一级分类，清理其下的冗余二级分类
                String cleanupSql = "DELETE c FROM category c JOIN (" +
                    "SELECT name, parent_id, MIN(id) as min_id " +
                    "FROM category " +
                    "WHERE parent_id = " + parentId + " " +
                    "GROUP BY name, parent_id " +
                    "HAVING COUNT(*) > 1" +
                ") t ON c.name = t.name AND c.parent_id = t.parent_id AND c.id != t.min_id";
                
                int affectedRows = st.executeUpdate(cleanupSql);
                if (affectedRows > 0) {
                    System.out.println("   ✓ 清理了 " + affectedRows + " 个重复的 '" + parentName + "' 下的二级分类");
                }
            }
            
            // 4. 清理冗余的三级分类
            System.out.println("4. 清理冗余的三级分类");
            // 先获取所有二级分类
            rs = st.executeQuery("SELECT id, name FROM category WHERE parent_id IN (SELECT id FROM category WHERE parent_id = 0)");
            
            while (rs.next()) {
                int parentId = rs.getInt("id");
                String parentName = rs.getString("name");
                
                // 对于每个二级分类，清理其下的冗余三级分类
                String cleanupSql = "DELETE c FROM category c JOIN (" +
                    "SELECT name, parent_id, MIN(id) as min_id " +
                    "FROM category " +
                    "WHERE parent_id = " + parentId + " " +
                    "GROUP BY name, parent_id " +
                    "HAVING COUNT(*) > 1" +
                ") t ON c.name = t.name AND c.parent_id = t.parent_id AND c.id != t.min_id";
                
                int affectedRows = st.executeUpdate(cleanupSql);
                if (affectedRows > 0) {
                    System.out.println("   ✓ 清理了 " + affectedRows + " 个重复的 '" + parentName + "' 下的三级分类");
                }
            }
            
            System.out.println("\n=== 冗余分类数据清理完成 ===");
        }
    }
    
    /**
     * 验证清理结果
     */
    private static void verifyCleanupResult(Connection conn) throws Exception {
        System.out.println("\n=== 验证清理结果 ===");
        
        try (Statement st = conn.createStatement()) {
            // 检查一级分类是否有重复
            System.out.println("1. 检查一级分类重复情况：");
            ResultSet rs = st.executeQuery(
                "SELECT name, COUNT(*) as count FROM category WHERE parent_id = 0 GROUP BY name HAVING COUNT(*) > 1"
            );
            
            if (rs.next()) {
                System.out.println("   ✗ 仍存在重复的一级分类：");
                do {
                    System.out.println("     - " + rs.getString("name") + " (" + rs.getInt("count") + "个)");
                } while (rs.next());
            } else {
                System.out.println("   ✓ 一级分类无重复");
            }
            
            // 检查二级分类是否有重复
            System.out.println("2. 检查二级分类重复情况：");
            rs = st.executeQuery(
                "SELECT c1.name as parent_name, c2.name as child_name, COUNT(*) as count " +
                "FROM category c1 JOIN category c2 ON c1.id = c2.parent_id " +
                "WHERE c1.parent_id = 0 " +
                "GROUP BY c1.name, c2.name HAVING COUNT(*) > 1"
            );
            
            if (rs.next()) {
                System.out.println("   ✗ 仍存在重复的二级分类：");
                do {
                    System.out.println("     - " + rs.getString("parent_name") + "/" + rs.getString("child_name") + " (" + rs.getInt("count") + "个)");
                } while (rs.next());
            } else {
                System.out.println("   ✓ 二级分类无重复");
            }
            
            // 打印清理后的分类树
            System.out.println("\n3. 清理后的分类树：");
            printCategoryTree(conn);
            
            System.out.println("\n=== 验证完成 ===");
        }
    }
    
    /**
     * 打印分类树
     */
    private static void printCategoryTree(Connection conn) throws Exception {
        try (Statement st = conn.createStatement()) {
            // 获取一级分类
            ResultSet rs = st.executeQuery("SELECT id, name FROM category WHERE parent_id = 0 ORDER BY id");
            
            while (rs.next()) {
                int parentId = rs.getInt("id");
                String parentName = rs.getString("name");
                System.out.println("   - " + parentName);
                
                // 获取二级分类
                ResultSet rs2 = st.executeQuery(
                    "SELECT id, name FROM category WHERE parent_id = " + parentId + " ORDER BY id"
                );
                
                while (rs2.next()) {
                    int childId = rs2.getInt("id");
                    String childName = rs2.getString("name");
                    System.out.println("     - " + childName);
                    
                    // 获取三级分类
                    ResultSet rs3 = st.executeQuery(
                        "SELECT id, name FROM category WHERE parent_id = " + childId + " ORDER BY id"
                    );
                    
                    while (rs3.next()) {
                        String grandChildName = rs3.getString("name");
                        System.out.println("         - " + grandChildName);
                    }
                    rs3.close();
                }
                rs2.close();
            }
        }
    }
}