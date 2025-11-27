-- Encoding and DB setup
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP DATABASE IF EXISTS `ecommerce`;
CREATE DATABASE IF NOT EXISTS `ecommerce` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `ecommerce`;

-- Drop tables in safe order
DROP TABLE IF EXISTS `order_item`;
DROP TABLE IF EXISTS `order`;
DROP TABLE IF EXISTS `cart_item`;
DROP TABLE IF EXISTS `cart`;
DROP TABLE IF EXISTS `product`;
DROP TABLE IF EXISTS `category`;
DROP TABLE IF EXISTS `user`;

SET FOREIGN_KEY_CHECKS = 1;

-- Users
CREATE TABLE `user` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `username` VARCHAR(32) NOT NULL UNIQUE,
  `password` CHAR(32) NOT NULL,
  `email` VARCHAR(100),
  `phone` VARCHAR(20),
  `address` VARCHAR(255),
  `status` TINYINT NOT NULL DEFAULT 1,
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Categories (3-level)
CREATE TABLE `category` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `name` VARCHAR(60) NOT NULL,
  `parent_id` INT DEFAULT 0,
  `level` TINYINT NOT NULL,
  `sort` INT NOT NULL DEFAULT 0,
  `icon` VARCHAR(255),
  `description` VARCHAR(255),
  `status` TINYINT NOT NULL DEFAULT 1,
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX `idx_parent` (`parent_id`),
  INDEX `idx_level` (`level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Products
CREATE TABLE `product` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `name` VARCHAR(100) NOT NULL,
  `category_id` INT NOT NULL,
  `price` DECIMAL(10,2) NOT NULL,
  `stock` INT NOT NULL DEFAULT 0,
  `description` TEXT,
  `image` VARCHAR(255),
  `status` TINYINT NOT NULL DEFAULT 1,
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX `idx_category` (`category_id`),
  INDEX `idx_name` (`name`),
  CONSTRAINT `fk_product_category` FOREIGN KEY (`category_id`) REFERENCES `category`(`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Cart (one per user when logged in)
CREATE TABLE `cart` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `user_id` INT NOT NULL,
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_cart_user` (`user_id`),
  CONSTRAINT `fk_cart_user` FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Cart items (price derived from product table in code)
CREATE TABLE `cart_item` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `cart_id` INT NOT NULL,
  `product_id` INT NOT NULL,
  `quantity` INT NOT NULL,
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX `idx_cart` (`cart_id`),
  INDEX `idx_product` (`product_id`),
  CONSTRAINT `fk_cart_item_cart` FOREIGN KEY (`cart_id`) REFERENCES `cart`(`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_cart_item_product` FOREIGN KEY (`product_id`) REFERENCES `product`(`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Orders (table name is reserved: use backticks)
CREATE TABLE `order` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `order_no` VARCHAR(64) NOT NULL UNIQUE,
  `user_id` INT NOT NULL,
  `total_amount` DECIMAL(12,2) NOT NULL,
  `status` TINYINT NOT NULL DEFAULT 1,
  `receiver_name` VARCHAR(50) NOT NULL,
  `receiver_phone` VARCHAR(20) NOT NULL,
  `receiver_address` VARCHAR(255) NOT NULL,
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX `idx_order_user` (`user_id`),
  INDEX `idx_order_no` (`order_no`),
  CONSTRAINT `fk_order_user` FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Order items
CREATE TABLE `order_item` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `order_id` INT NOT NULL,
  `product_id` INT NOT NULL,
  `quantity` INT NOT NULL,
  `price` DECIMAL(10,2) NOT NULL,
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX `idx_order_item_order` (`order_id`),
  INDEX `idx_order_item_product` (`product_id`),
  CONSTRAINT `fk_order_item_order` FOREIGN KEY (`order_id`) REFERENCES `order`(`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_order_item_product` FOREIGN KEY (`product_id`) REFERENCES `product`(`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Seed data: users
INSERT INTO `user` (`username`,`password`,`email`,`phone`,`address`,`status`) VALUES
('admin','e10adc3949ba59abbe56e057f20f883e','admin@example.com','13800000000','北京市海淀区',1),
('8208230217','e10adc3949ba59abbe56e057f20f883e','user@example.com','13900000000','上海市浦东新区',1);

-- Seed data: categories (top-level)
INSERT INTO `category` (`name`,`parent_id`,`level`,`sort`,`status`) VALUES
('电子产品',0,1,1,1),
('家居用品',0,1,2,1),
('服装鞋帽',0,1,3,1),
('食品饮料',0,1,4,1);

-- Seed data: second-level
INSERT INTO `category` (`name`,`parent_id`,`level`,`sort`,`status`) VALUES
('手机', (SELECT id FROM `category` WHERE name='电子产品' LIMIT 1), 2, 1, 1),
('电脑', (SELECT id FROM `category` WHERE name='电子产品' LIMIT 1), 2, 2, 1),
('平板', (SELECT id FROM `category` WHERE name='电子产品' LIMIT 1), 2, 3, 1),
('家具', (SELECT id FROM `category` WHERE name='家居用品' LIMIT 1), 2, 1, 1),
('厨具', (SELECT id FROM `category` WHERE name='家居用品' LIMIT 1), 2, 2, 1),
('男装', (SELECT id FROM `category` WHERE name='服装鞋帽' LIMIT 1), 2, 1, 1),
('女装', (SELECT id FROM `category` WHERE name='服装鞋帽' LIMIT 1), 2, 2, 1),
('零食', (SELECT id FROM `category` WHERE name='食品饮料' LIMIT 1), 2, 1, 1),
('饮料', (SELECT id FROM `category` WHERE name='食品饮料' LIMIT 1), 2, 2, 1);

-- Seed data: third-level
INSERT INTO `category` (`name`,`parent_id`,`level`,`sort`,`status`) VALUES
('智能手机', (SELECT id FROM `category` WHERE name='手机' LIMIT 1), 3, 1, 1),
('游戏本', (SELECT id FROM `category` WHERE name='电脑' LIMIT 1), 3, 1, 1),
('轻薄本', (SELECT id FROM `category` WHERE name='电脑' LIMIT 1), 3, 2, 1),
('茶几', (SELECT id FROM `category` WHERE name='家具' LIMIT 1), 3, 1, 1),
('沙发', (SELECT id FROM `category` WHERE name='家具' LIMIT 1), 3, 2, 1),
('速食', (SELECT id FROM `category` WHERE name='零食' LIMIT 1), 3, 1, 1),
('碳酸饮料', (SELECT id FROM `category` WHERE name='饮料' LIMIT 1), 3, 1, 1);

-- Seed data: products
INSERT INTO `product` (`name`,`category_id`,`price`,`stock`,`description`,`image`,`status`) VALUES
('iPhone 14 Pro', (SELECT id FROM `category` WHERE name='智能手机' LIMIT 1), 7999.00, 50, 'Apple iPhone 14 Pro 智能手机，配备A16芯片', '/uploads/demo_phone.jpg', 1),
('华为 Mate 50', (SELECT id FROM `category` WHERE name='智能手机' LIMIT 1), 5999.00, 30, '华为 Mate 50 智能手机，搭载鸿蒙系统', '/uploads/demo_phone.jpg', 1),
('小米 13', (SELECT id FROM `category` WHERE name='智能手机' LIMIT 1), 3999.00, 40, '小米 13 智能手机，高性能处理器', '/uploads/demo_phone.jpg', 1),
('MacBook Pro', (SELECT id FROM `category` WHERE name='轻薄本' LIMIT 1), 12999.00, 20, 'Apple MacBook Pro 笔记本电脑', '/uploads/demo_laptop.jpg', 1),
('iPad Air', (SELECT id FROM `category` WHERE name='平板' LIMIT 1), 4599.00, 25, 'Apple iPad Air 平板电脑', '/uploads/demo_tablet.jpg', 1);

-- Optional: create carts for users
INSERT INTO `cart` (`user_id`) SELECT `id` FROM `user` WHERE `username` IN ('admin','8208230217');

-- Example cart items (optional; will be updated by application)
INSERT INTO `cart_item` (`cart_id`,`product_id`,`quantity`) SELECT 
  (SELECT id FROM `cart` WHERE user_id=(SELECT id FROM `user` WHERE username='admin' LIMIT 1)),
  (SELECT id FROM `product` WHERE name='iPhone 14 Pro' LIMIT 1),
  1;

-- Order samples (structure ready; application creates orders at runtime)
-- No initial orders inserted by default

-- Final indices and constraints are already defined above; schema ready