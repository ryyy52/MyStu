-- =============================================
-- 电子商务系统数据库初始化脚本
-- 版本：v1.0
-- 功能：创建数据库、表结构和初始测试数据
-- 编码：utf8mb4（支持emoji）
-- =============================================

-- 创建数据库，如果不存在则创建
CREATE DATABASE IF NOT EXISTS ecommerce_new DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE ecommerce_new;

-- =============================================
-- 清理现有表结构
-- =============================================
-- 禁用外键约束检查，避免删除表时出现外键依赖错误
SET FOREIGN_KEY_CHECKS = 0;
-- 按照依赖关系从后往前删除表
DROP TABLE IF EXISTS order_item;       -- 订单商品项表（依赖订单表）
DROP TABLE IF EXISTS `order`;          -- 订单表（依赖用户表）
DROP TABLE IF EXISTS cart_item;         -- 购物车商品项表（依赖购物车表）
DROP TABLE IF EXISTS cart;              -- 购物车表（依赖用户表）
DROP TABLE IF EXISTS product;           -- 商品表（依赖分类表）
DROP TABLE IF EXISTS category;          -- 分类表
DROP TABLE IF EXISTS user;              -- 用户表
-- 重新启用外键约束检查
SET FOREIGN_KEY_CHECKS = 1;

-- =============================================
-- 创建用户表
-- =============================================
-- 存储系统用户信息，包括管理员和普通用户
CREATE TABLE IF NOT EXISTS user (
  id INT AUTO_INCREMENT PRIMARY KEY,                  -- 用户ID，主键自增
  username VARCHAR(64) NOT NULL UNIQUE,              -- 用户名，唯一约束，不可为空
  password VARCHAR(128) NOT NULL,                    -- 密码，加密存储，不可为空
  email VARCHAR(128) UNIQUE,                         -- 邮箱，唯一约束
  phone VARCHAR(32),                                 -- 电话号码
  address VARCHAR(255),                              -- 收货地址
  status INT DEFAULT 1,                              -- 用户状态：1=正常，0=禁用
  role VARCHAR(16) DEFAULT 'user',                   -- 用户角色：admin=管理员，user=普通用户
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,   -- 创建时间，自动生成
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP  -- 更新时间，自动更新
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================
-- 创建分类表
-- =============================================
-- 存储商品分类信息，支持无限层级分类
CREATE TABLE IF NOT EXISTS category (
  id INT AUTO_INCREMENT PRIMARY KEY,                  -- 分类ID，主键自增
  name VARCHAR(64) NOT NULL,                         -- 分类名称，不可为空
  parent_id INT DEFAULT 0,                           -- 父分类ID，0表示顶级分类
  level INT DEFAULT 1,                               -- 分类级别，1=顶级，2=二级，以此类推
  sort INT DEFAULT 0,                                -- 排序字段，用于展示顺序
  icon VARCHAR(255),                                 -- 分类图标URL
  description TEXT,                                  -- 分类描述
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,   -- 创建时间，自动生成
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,  -- 更新时间，自动更新
  UNIQUE KEY uk_name_parent (name, parent_id)        -- 唯一约束：同一父分类下名称唯一
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================
-- 创建商品表
-- =============================================
-- 存储商品基本信息
CREATE TABLE IF NOT EXISTS product (
  id INT AUTO_INCREMENT PRIMARY KEY,                  -- 商品ID，主键自增
  name VARCHAR(128) NOT NULL,                         -- 商品名称，不可为空
  category_id INT,                                    -- 分类ID，外键关联分类表
  price DECIMAL(10,2) DEFAULT 0.00,                  -- 商品价格，精确到分
  stock INT DEFAULT 0,                                -- 商品库存
  description TEXT,                                  -- 商品描述
  image VARCHAR(1024),                                -- 商品图片URL
  status INT DEFAULT 1,                              -- 商品状态：1=上架，0=下架
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,   -- 创建时间，自动生成
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,  -- 更新时间，自动更新
  FOREIGN KEY (category_id) REFERENCES category(id),  -- 外键约束：关联分类表
  UNIQUE KEY uk_name_category (name, category_id)     -- 唯一约束：同一分类下商品名称唯一
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================
-- 创建购物车表
-- =============================================
-- 存储用户购物车信息
CREATE TABLE IF NOT EXISTS cart (
  id INT AUTO_INCREMENT PRIMARY KEY,                  -- 购物车ID，主键自增
  user_id INT NOT NULL,                               -- 用户ID，外键关联用户表
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,   -- 创建时间，自动生成
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,  -- 更新时间，自动更新
  FOREIGN KEY (user_id) REFERENCES user(id)           -- 外键约束：关联用户表
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================
-- 创建购物车商品项表
-- =============================================
-- 存储购物车中的商品信息
CREATE TABLE IF NOT EXISTS cart_item (
  id INT AUTO_INCREMENT PRIMARY KEY,                  -- 购物车商品项ID，主键自增
  cart_id INT NOT NULL,                               -- 购物车ID，外键关联购物车表
  product_id INT NOT NULL,                            -- 商品ID，外键关联商品表
  quantity INT NOT NULL DEFAULT 1,                    -- 商品数量，默认为1
  FOREIGN KEY (cart_id) REFERENCES cart(id) ON DELETE CASCADE,  -- 外键约束：关联购物车表，级联删除
  FOREIGN KEY (product_id) REFERENCES product(id)     -- 外键约束：关联商品表
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================
-- 创建订单表
-- =============================================
-- 存储订单基本信息
CREATE TABLE IF NOT EXISTS `order` (
  id INT AUTO_INCREMENT PRIMARY KEY,                  -- 订单ID，主键自增
  order_no VARCHAR(64) NOT NULL UNIQUE,               -- 订单号，唯一约束，系统生成
  user_id INT NOT NULL,                               -- 用户ID，外键关联用户表
  total_amount DECIMAL(10,2) DEFAULT 0.00,           -- 订单总金额，精确到分
  status INT DEFAULT 0,                               -- 订单状态：0=待付款，1=待发货，2=待收货，3=已完成，4=已取消
  receiver_name VARCHAR(64),                          -- 收货人姓名
  receiver_phone VARCHAR(32),                         -- 收货人电话
  receiver_address VARCHAR(255),                      -- 收货地址
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,   -- 创建时间，自动生成
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,  -- 更新时间，自动更新
  FOREIGN KEY (user_id) REFERENCES user(id)           -- 外键约束：关联用户表
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================
-- 创建订单商品项表
-- =============================================
-- 存储订单中的商品信息
CREATE TABLE IF NOT EXISTS order_item (
  id INT AUTO_INCREMENT PRIMARY KEY,                  -- 订单商品项ID，主键自增
  order_id INT NOT NULL,                              -- 订单ID，外键关联订单表
  product_id INT NOT NULL,                            -- 商品ID，外键关联商品表
  quantity INT NOT NULL DEFAULT 1,                    -- 商品数量，默认为1
  price DECIMAL(10,2) DEFAULT 0.00,                  -- 商品单价，精确到分
  FOREIGN KEY (order_id) REFERENCES `order`(id) ON DELETE CASCADE,  -- 外键约束：关联订单表，级联删除
  FOREIGN KEY (product_id) REFERENCES product(id)     -- 外键约束：关联商品表
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================
-- 插入初始测试数据
-- =============================================

-- 1. 插入用户数据
-- 插入管理员和普通用户，用于测试不同角色的功能
INSERT IGNORE INTO user (username, password, email, phone, address, status, role) VALUES
('devuser', MD5('dev123456'), 'dev@local.test', '13800000000', 'Beijing Road 1', 1, 'admin'),  -- 管理员用户
('alice', MD5('alice123'), 'alice@example.com', '13900000000', 'Shenzhen Road 2', 1, 'user');  -- 普通用户

-- 2. 插入顶级分类数据
-- 插入两个顶级分类，用于测试分类功能
INSERT IGNORE INTO category (name, parent_id, level, sort, icon, description) VALUES
('电子产品', 0, 1, 0, NULL, NULL),  -- 顶级分类：电子产品
('服饰鞋包', 0, 1, 0, NULL, NULL);  -- 顶级分类：服饰鞋包

-- 3. 定义分类变量，方便后续插入商品使用
SET @cat_elec := (SELECT id FROM category WHERE name='电子产品' LIMIT 1);
SET @cat_wear := (SELECT id FROM category WHERE name='服饰鞋包' LIMIT 1);

-- 4. 插入测试商品数据
-- 插入基本测试商品，用于自动化测试和功能验证
INSERT IGNORE INTO product (name, category_id, price, stock, description, image, status) VALUES
('测试商品A', @cat_elec, 99.99, 50, '自动化验证商品A', '/images/testA.jpg', 1),
('测试商品B', @cat_elec, 199.00, 30, '自动化验证商品B', '/images/testB.jpg', 1),
('测试商品C', @cat_wear, 59.90, 100, '自动化验证商品C', '/images/testC.jpg', 1);

-- 5. 插入第一个订单
-- 为管理员用户创建一个测试订单
SET @user_id := (SELECT id FROM user WHERE username='devuser' LIMIT 1);

INSERT IGNORE INTO `order` (order_no, user_id, total_amount, status, receiver_name, receiver_phone, receiver_address)
VALUES ('TESTORDER0001', @user_id, 0.00, 1, '张三', '13600000000', '上海市黄浦区中山东一路1号');

-- 6. 插入订单商品项
-- 为第一个订单添加商品项，并更新订单总金额
SET @order_id := 103981715;
SET @pA := (SELECT id FROM product WHERE name='测试商品A' LIMIT 1);
SET @pB := (SELECT id FROM product WHERE name='测试商品B' LIMIT 1);

INSERT IGNORE INTO order_item (order_id, product_id, quantity, price) VALUES
(@order_id, @pA, 2, (SELECT price FROM product WHERE id=@pA)),
(@order_id, @pB, 1, (SELECT price FROM product WHERE id=@pB));

-- 更新订单总金额
UPDATE `order` o
JOIN (SELECT SUM(quantity*price) AS total FROM order_item WHERE order_id=@order_id) t
ON o.id=@order_id
SET o.total_amount = t.total;

-- 7. 扩充用户与分类层级
-- 插入更多用户和二级分类，丰富测试数据
INSERT IGNORE INTO user (username, password, email, phone, address, status, role) VALUES
('bob', MD5('bobpass123'), 'bob@example.com', '13700000000', 'Guangzhou Road 3', 1, 'user'),
('carol', MD5('carolpass456'), 'carol@example.com', '13600000001', 'Hangzhou Road 4', 1, 'user');

INSERT IGNORE INTO category (name, parent_id, level, sort, icon, description) VALUES
('手机', @cat_elec, 2, 0, NULL, NULL),     -- 电子产品下的二级分类：手机
('笔记本', @cat_elec, 2, 0, NULL, NULL),   -- 电子产品下的二级分类：笔记本
('男装', @cat_wear, 2, 0, NULL, NULL),     -- 服饰鞋包下的二级分类：男装
('女装', @cat_wear, 2, 0, NULL, NULL);     -- 服饰鞋包下的二级分类：女装

-- 8. 定义二级分类变量，方便后续插入商品使用
SET @cat_phone := (SELECT id FROM category WHERE name='手机' LIMIT 1);
SET @cat_laptop := (SELECT id FROM category WHERE name='笔记本' LIMIT 1);
SET @cat_men := (SELECT id FROM category WHERE name='男装' LIMIT 1);
SET @cat_women := (SELECT id FROM category WHERE name='女装' LIMIT 1);

-- 9. 扩充商品数据
-- 插入更多真实商品，用于测试商品列表、详情等功能
INSERT IGNORE INTO product (name, category_id, price, stock, description, image, status) VALUES
('旗舰手机X', @cat_phone, 4999.00, 200, '旗舰级智能手机', '/images/phone_x.jpg', 1),
('入门手机Y', @cat_phone, 999.00, 500, '性价比智能手机', '/images/phone_y.jpg', 1),
('轻薄本Pro', @cat_laptop, 7999.00, 80, '高性能轻薄笔记本', '/images/laptop_pro.jpg', 1),
('商务本Air', @cat_laptop, 5999.00, 120, '商务轻薄本', '/images/laptop_air.jpg', 1),
('纯色T恤', @cat_men, 59.00, 1000, '纯棉T恤', '/images/tshirt_men.jpg', 1),
('连衣裙', @cat_women, 199.00, 300, '夏季连衣裙', '/images/dress_women.jpg', 1);

-- 10. 创建购物车并加入商品
-- 为用户创建购物车并添加商品，用于测试购物车功能
SET @dev_id := (SELECT id FROM user WHERE username='devuser' LIMIT 1);
SET @alice_id := (SELECT id FROM user WHERE username='alice' LIMIT 1);

-- 创建购物车
INSERT IGNORE INTO cart (user_id) VALUES (@dev_id);
INSERT IGNORE INTO cart (user_id) VALUES (@alice_id);

-- 获取购物车ID
SET @cart_dev := (SELECT id FROM cart WHERE user_id=@dev_id ORDER BY id DESC LIMIT 1);
SET @cart_alice := (SELECT id FROM cart WHERE user_id=@alice_id ORDER BY id DESC LIMIT 1);

-- 定义商品变量
SET @pPhoneX := (SELECT id FROM product WHERE name='旗舰手机X' LIMIT 1);
SET @pLaptopPro := (SELECT id FROM product WHERE name='轻薄本Pro' LIMIT 1);
SET @pMenT := (SELECT id FROM product WHERE name='纯色T恤' LIMIT 1);

-- 添加商品到购物车
INSERT IGNORE INTO cart_item (cart_id, product_id, quantity) VALUES
(@cart_dev, @pPhoneX, 1),   -- 管理员购物车：1个旗舰手机X
(@cart_dev, @pMenT, 2),      -- 管理员购物车：2件纯色T恤
(@cart_alice, @pLaptopPro, 1); -- Alice购物车：1台轻薄本Pro

-- 11. 为管理员创建第二个订单（待付款状态）
INSERT IGNORE INTO `order` (order_no, user_id, total_amount, status, receiver_name, receiver_phone, receiver_address)
VALUES ('REPLACE(UUID()', @dev_id, 0.00, 1, '李四', '13500000000', '北京市海淀区学院路5号');

-- 为第二个订单添加商品项
SET @order2 := LAST_INSERT_ID();
SET @pFlagship := (SELECT id FROM product WHERE name='旗舰手机X' LIMIT 1);
SET @pTshirt := (SELECT id FROM product WHERE name='纯色T恤' LIMIT 1);

INSERT IGNORE INTO order_item (order_id, product_id, quantity, price) VALUES
(@order2, @pFlagship, 1, (SELECT price FROM product WHERE id=@pFlagship)),
(@order2, @pTshirt, 3, (SELECT price FROM product WHERE id=@pTshirt));

-- 更新第二个订单的总金额
UPDATE `order` o
JOIN (SELECT SUM(quantity*price) AS total FROM order_item WHERE order_id=@order2) t
ON o.id=@order2
SET o.total_amount = t.total;

-- 12. 为Alice创建一个已支付订单
-- 测试普通用户的订单功能
INSERT IGNORE INTO `order` (order_no, user_id, total_amount, status, receiver_name, receiver_phone, receiver_address)
VALUES (REPLACE(UUID(), '-', ''), @alice_id, 0.00, 2, '王五', '13400000000', '广州市天河区体育东路6号');

-- 为Alice的订单添加商品项
SET @orderAlice := 104044821;  -- 获取Alice订单的ID
SET @pAir := (SELECT id FROM product WHERE name='商务本Air' LIMIT 1);  -- 获取商务本Air的ID
SET @pDress := (SELECT id FROM product WHERE name='连衣裙' LIMIT 1);  -- 获取连衣裙的ID

-- 插入订单商品项
INSERT IGNORE INTO order_item (order_id, product_id, quantity, price) VALUES
(@orderAlice, @pAir, 1, (SELECT price FROM product WHERE id=@pAir)),  -- 1台商务本Air
(@orderAlice, @pDress, 2, (SELECT price FROM product WHERE id=@pDress));  -- 2件连衣裙

-- 更新Alice订单的总金额
UPDATE `order` o
JOIN (SELECT SUM(quantity*price) AS total FROM order_item WHERE order_id=@orderAlice) t  -- 计算订单商品项总金额
ON o.id=@orderAlice  -- 关联订单
SET o.total_amount = t.total;  -- 更新订单总金额

-- 13. 根据订单扣减库存
-- 模拟订单支付后，扣减商品库存的操作，确保库存不为负数
UPDATE product SET stock = stock - 1 WHERE id=@pFlagship AND stock >= 1;  -- 扣减旗舰手机X库存1个
UPDATE product SET stock = stock - 3 WHERE id=@pTshirt AND stock >= 3;    -- 扣减纯色T恤库存3件
UPDATE product SET stock = stock - 1 WHERE id=@pAir AND stock >= 1;      -- 扣减商务本Air库存1台
UPDATE product SET stock = stock - 2 WHERE id=@pDress AND stock >= 2;    -- 扣减连衣裙库存2件
