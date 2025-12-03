CREATE DATABASE IF NOT EXISTS ecommerce_new DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE ecommerce_new;

-- 删除所有表，确保重新创建
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS order_item;
DROP TABLE IF EXISTS `order`;
DROP TABLE IF EXISTS cart_item;
DROP TABLE IF EXISTS cart;
DROP TABLE IF EXISTS product;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS user;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE IF NOT EXISTS user (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(64) NOT NULL UNIQUE,
  password VARCHAR(128) NOT NULL,
  email VARCHAR(128) UNIQUE,
  phone VARCHAR(32),
  address VARCHAR(255),
  status INT DEFAULT 1,
  role VARCHAR(16) DEFAULT 'user',
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS category (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(64) NOT NULL,
  parent_id INT DEFAULT 0,
  level INT DEFAULT 1,
  sort INT DEFAULT 0,
  icon VARCHAR(255),
  description TEXT,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_name_parent (name, parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS product (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(128) NOT NULL,
  category_id INT,
  price DECIMAL(10,2) DEFAULT 0.00,
  stock INT DEFAULT 0,
  description TEXT,
  image VARCHAR(1024),
  status INT DEFAULT 1,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (category_id) REFERENCES category(id),
  UNIQUE KEY uk_name_category (name, category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS cart (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS cart_item (
  id INT AUTO_INCREMENT PRIMARY KEY,
  cart_id INT NOT NULL,
  product_id INT NOT NULL,
  quantity INT NOT NULL DEFAULT 1,
  FOREIGN KEY (cart_id) REFERENCES cart(id) ON DELETE CASCADE,
  FOREIGN KEY (product_id) REFERENCES product(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `order` (
  id INT AUTO_INCREMENT PRIMARY KEY,
  order_no VARCHAR(64) NOT NULL UNIQUE,
  user_id INT NOT NULL,
  total_amount DECIMAL(10,2) DEFAULT 0.00,
  status INT DEFAULT 0,
  receiver_name VARCHAR(64),
  receiver_phone VARCHAR(32),
  receiver_address VARCHAR(255),
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS order_item (
  id INT AUTO_INCREMENT PRIMARY KEY,
  order_id INT NOT NULL,
  product_id INT NOT NULL,
  quantity INT NOT NULL DEFAULT 1,
  price DECIMAL(10,2) DEFAULT 0.00,
  FOREIGN KEY (order_id) REFERENCES `order`(id) ON DELETE CASCADE,
  FOREIGN KEY (product_id) REFERENCES product(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT IGNORE INTO user (username, password, email, phone, address, status, role) VALUES
('devuser', MD5('dev123456'), 'dev@local.test', '13800000000', 'Beijing Road 1', 1, 'admin'),
('alice', MD5('alice123'), 'alice@example.com', '13900000000', 'Shenzhen Road 2', 1, 'user');

INSERT IGNORE INTO category (name, parent_id, level, sort, icon, description) VALUES
('电子产品', 0, 1, 0, NULL, NULL),
('服饰鞋包', 0, 1, 0, NULL, NULL);

SET @cat_elec := (SELECT id FROM category WHERE name='电子产品' LIMIT 1);
SET @cat_wear := (SELECT id FROM category WHERE name='服饰鞋包' LIMIT 1);

INSERT IGNORE INTO product (name, category_id, price, stock, description, image, status) VALUES
('测试商品A', @cat_elec, 99.99, 50, '自动化验证商品A', '/images/testA.jpg', 1),
('测试商品B', @cat_elec, 199.00, 30, '自动化验证商品B', '/images/testB.jpg', 1),
('测试商品C', @cat_wear, 59.90, 100, '自动化验证商品C', '/images/testC.jpg', 1);

SET @user_id := (SELECT id FROM user WHERE username='devuser' LIMIT 1);

INSERT IGNORE INTO `order` (order_no, user_id, total_amount, status, receiver_name, receiver_phone, receiver_address)
VALUES ('TESTORDER0001', @user_id, 0.00, 1, '张三', '13600000000', '上海市黄浦区中山东一路1号');

SET @order_id := LAST_INSERT_ID();
SET @pA := (SELECT id FROM product WHERE name='测试商品A' LIMIT 1);
SET @pB := (SELECT id FROM product WHERE name='测试商品B' LIMIT 1);

INSERT IGNORE INTO order_item (order_id, product_id, quantity, price) VALUES
(@order_id, @pA, 2, (SELECT price FROM product WHERE id=@pA)),
(@order_id, @pB, 1, (SELECT price FROM product WHERE id=@pB));

UPDATE `order` o
JOIN (SELECT SUM(quantity*price) AS total FROM order_item WHERE order_id=@order_id) t
ON o.id=@order_id
SET o.total_amount = t.total;

-- 扩充用户与分类层级
INSERT IGNORE INTO user (username, password, email, phone, address, status, role) VALUES
('bob', MD5('bobpass123'), 'bob@example.com', '13700000000', 'Guangzhou Road 3', 1, 'user'),
('carol', MD5('carolpass456'), 'carol@example.com', '13600000001', 'Hangzhou Road 4', 1, 'user');

INSERT IGNORE INTO category (name, parent_id, level, sort, icon, description) VALUES
('手机', @cat_elec, 2, 0, NULL, NULL),
('笔记本', @cat_elec, 2, 0, NULL, NULL),
('男装', @cat_wear, 2, 0, NULL, NULL),
('女装', @cat_wear, 2, 0, NULL, NULL);

SET @cat_phone := (SELECT id FROM category WHERE name='手机' LIMIT 1);
SET @cat_laptop := (SELECT id FROM category WHERE name='笔记本' LIMIT 1);
SET @cat_men := (SELECT id FROM category WHERE name='男装' LIMIT 1);
SET @cat_women := (SELECT id FROM category WHERE name='女装' LIMIT 1);

-- 扩充商品
INSERT IGNORE INTO product (name, category_id, price, stock, description, image, status) VALUES
('旗舰手机X', @cat_phone, 4999.00, 200, '旗舰级智能手机', '/images/phone_x.jpg', 1),
('入门手机Y', @cat_phone, 999.00, 500, '性价比智能手机', '/images/phone_y.jpg', 1),
('轻薄本Pro', @cat_laptop, 7999.00, 80, '高性能轻薄笔记本', '/images/laptop_pro.jpg', 1),
('商务本Air', @cat_laptop, 5999.00, 120, '商务轻薄本', '/images/laptop_air.jpg', 1),
('纯色T恤', @cat_men, 59.00, 1000, '纯棉T恤', '/images/tshirt_men.jpg', 1),
('连衣裙', @cat_women, 199.00, 300, '夏季连衣裙', '/images/dress_women.jpg', 1);

-- 为 devuser 与 alice 创建购物车并加入商品
SET @dev_id := (SELECT id FROM user WHERE username='devuser' LIMIT 1);
SET @alice_id := (SELECT id FROM user WHERE username='alice' LIMIT 1);

INSERT IGNORE INTO cart (user_id) VALUES (@dev_id);
INSERT IGNORE INTO cart (user_id) VALUES (@alice_id);

SET @cart_dev := (SELECT id FROM cart WHERE user_id=@dev_id ORDER BY id DESC LIMIT 1);
SET @cart_alice := (SELECT id FROM cart WHERE user_id=@alice_id ORDER BY id DESC LIMIT 1);

SET @pPhoneX := (SELECT id FROM product WHERE name='旗舰手机X' LIMIT 1);
SET @pLaptopPro := (SELECT id FROM product WHERE name='轻薄本Pro' LIMIT 1);
SET @pMenT := (SELECT id FROM product WHERE name='纯色T恤' LIMIT 1);

INSERT IGNORE INTO cart_item (cart_id, product_id, quantity) VALUES
(@cart_dev, @pPhoneX, 1),
(@cart_dev, @pMenT, 2),
(@cart_alice, @pLaptopPro, 1);

-- 为 devuser 创建第二个订单（待付款）
INSERT IGNORE INTO `order` (order_no, user_id, total_amount, status, receiver_name, receiver_phone, receiver_address)
VALUES ('REPLACE(UUID()', @dev_id, 0.00, 1, '李四', '13500000000', '北京市海淀区学院路5号');

SET @order2 := LAST_INSERT_ID();
SET @pFlagship := (SELECT id FROM product WHERE name='旗舰手机X' LIMIT 1);
SET @pTshirt := (SELECT id FROM product WHERE name='纯色T恤' LIMIT 1);

INSERT IGNORE INTO order_item (order_id, product_id, quantity, price) VALUES
(@order2, @pFlagship, 1, (SELECT price FROM product WHERE id=@pFlagship)),
(@order2, @pTshirt, 3, (SELECT price FROM product WHERE id=@pTshirt));

UPDATE `order` o
JOIN (SELECT SUM(quantity*price) AS total FROM order_item WHERE order_id=@order2) t
ON o.id=@order2
SET o.total_amount = t.total;

-- 为 alice 创建一个已支付订单
INSERT IGNORE INTO `order` (order_no, user_id, total_amount, status, receiver_name, receiver_phone, receiver_address)
VALUES (REPLACE(UUID(), '-', ''), @alice_id, 0.00, 2, '王五', '13400000000', '广州市天河区体育东路6号');

SET @orderAlice := LAST_INSERT_ID();
SET @pAir := (SELECT id FROM product WHERE name='商务本Air' LIMIT 1);
SET @pDress := (SELECT id FROM product WHERE name='连衣裙' LIMIT 1);

INSERT IGNORE INTO order_item (order_id, product_id, quantity, price) VALUES
(@orderAlice, @pAir, 1, (SELECT price FROM product WHERE id=@pAir)),
(@orderAlice, @pDress, 2, (SELECT price FROM product WHERE id=@pDress));

UPDATE `order` o
JOIN (SELECT SUM(quantity*price) AS total FROM order_item WHERE order_id=@orderAlice) t
ON o.id=@orderAlice
SET o.total_amount = t.total;

-- 可选：根据订单扣减库存（确保不为负数）
UPDATE product SET stock = stock - 1 WHERE id=@pFlagship AND stock >= 1;
UPDATE product SET stock = stock - 3 WHERE id=@pTshirt AND stock >= 3;
UPDATE product SET stock = stock - 1 WHERE id=@pAir AND stock >= 1;
UPDATE product SET stock = stock - 2 WHERE id=@pDress AND stock >= 2;
