package com.ecommerce;

import java.sql.Connection;
import com.ecommerce.utils.JDBCUtils;

public class TestDBConnection {
    public static void main(String[] args) {
        try {
            // 测试数据库连接
            Connection conn = JDBCUtils.getConnection();
            if (conn != null) {
                System.out.println("数据库连接成功!");
                conn.close();
            } else {
                System.out.println("数据库连接失败!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}