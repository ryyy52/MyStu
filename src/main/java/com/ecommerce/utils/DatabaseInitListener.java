package com.ecommerce.utils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseInitListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        String hostUrl = "jdbc:mysql://localhost:3306/?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        String dbUrl = "jdbc:mysql://localhost:3306/ecommerce?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        String username = System.getProperty("db.user", "root");
        String password = System.getProperty("db.pass", "123456");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(hostUrl, username, password);
                 Statement st = conn.createStatement()) {
                st.execute("CREATE DATABASE IF NOT EXISTS ecommerce DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
            }
            try (Connection conn = DriverManager.getConnection(dbUrl, username, password);
                 Statement st = conn.createStatement()) {
                st.execute("CREATE TABLE IF NOT EXISTS user (\n" +
                        "  id INT AUTO_INCREMENT PRIMARY KEY,\n" +
                        "  username VARCHAR(64) NOT NULL UNIQUE,\n" +
                        "  password VARCHAR(128) NOT NULL,\n" +
                        "  email VARCHAR(128) UNIQUE,\n" +
                        "  phone VARCHAR(32),\n" +
                        "  address VARCHAR(255),\n" +
                        "  status INT DEFAULT 1,\n" +
                        "  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,\n" +
                        "  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP\n" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
                st.execute("CREATE TABLE IF NOT EXISTS category (\n" +
                        "  id INT AUTO_INCREMENT PRIMARY KEY,\n" +
                        "  name VARCHAR(64) NOT NULL,\n" +
                        "  parent_id INT DEFAULT 0,\n" +
                        "  level INT DEFAULT 1,\n" +
                        "  sort INT DEFAULT 0,\n" +
                        "  icon VARCHAR(255),\n" +
                        "  description TEXT,\n" +
                        "  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,\n" +
                        "  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP\n" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
                st.execute("CREATE TABLE IF NOT EXISTS product (\n" +
                        "  id INT AUTO_INCREMENT PRIMARY KEY,\n" +
                        "  name VARCHAR(128) NOT NULL,\n" +
                        "  category_id INT,\n" +
                        "  price DECIMAL(10,2) DEFAULT 0.00,\n" +
                        "  stock INT DEFAULT 0,\n" +
                        "  description TEXT,\n" +
                        "  image VARCHAR(255),\n" +
                        "  status INT DEFAULT 1,\n" +
                        "  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,\n" +
                        "  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,\n" +
                        "  FOREIGN KEY (category_id) REFERENCES category(id)\n" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
                st.execute("CREATE TABLE IF NOT EXISTS cart (\n" +
                        "  id INT AUTO_INCREMENT PRIMARY KEY,\n" +
                        "  user_id INT NOT NULL,\n" +
                        "  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,\n" +
                        "  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,\n" +
                        "  FOREIGN KEY (user_id) REFERENCES user(id)\n" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
                st.execute("CREATE TABLE IF NOT EXISTS cart_item (\n" +
                        "  id INT AUTO_INCREMENT PRIMARY KEY,\n" +
                        "  cart_id INT NOT NULL,\n" +
                        "  product_id INT NOT NULL,\n" +
                        "  quantity INT NOT NULL DEFAULT 1,\n" +
                        "  FOREIGN KEY (cart_id) REFERENCES cart(id) ON DELETE CASCADE,\n" +
                        "  FOREIGN KEY (product_id) REFERENCES product(id)\n" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
                st.execute("CREATE TABLE IF NOT EXISTS `order` (\n" +
                        "  id INT AUTO_INCREMENT PRIMARY KEY,\n" +
                        "  order_no VARCHAR(64) NOT NULL UNIQUE,\n" +
                        "  user_id INT NOT NULL,\n" +
                        "  total_amount DECIMAL(10,2) DEFAULT 0.00,\n" +
                        "  status INT DEFAULT 0,\n" +
                        "  receiver_name VARCHAR(64),\n" +
                        "  receiver_phone VARCHAR(32),\n" +
                        "  receiver_address VARCHAR(255),\n" +
                        "  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,\n" +
                        "  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,\n" +
                        "  FOREIGN KEY (user_id) REFERENCES user(id)\n" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
                st.execute("CREATE TABLE IF NOT EXISTS order_item (\n" +
                        "  id INT AUTO_INCREMENT PRIMARY KEY,\n" +
                        "  order_id INT NOT NULL,\n" +
                        "  product_id INT NOT NULL,\n" +
                        "  quantity INT NOT NULL DEFAULT 1,\n" +
                        "  price DECIMAL(10,2) DEFAULT 0.00,\n" +
                        "  FOREIGN KEY (order_id) REFERENCES `order`(id) ON DELETE CASCADE,\n" +
                        "  FOREIGN KEY (product_id) REFERENCES product(id)\n" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
            }
            System.out.println("数据库初始化完成");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}