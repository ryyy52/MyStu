# 电商商品展示与购物车系统

## 项目简介

这是一个基于Java Web开发的电商商品展示与购物车系统，采用经典MVC架构，使用Servlet和JSP技术实现。系统包含完整的电商核心功能，支持管理员和普通用户两种角色，具有良好的安全性和用户体验。

## 技术栈

- **开发语言**：Java 8
- **框架**：Servlet 4.0 + JSP 2.3 + JSTL 1.2
- **数据库**：MySQL 8.0
- **构建工具**：Maven 3.6+
- **Web服务器**：Tomcat 7.0+
- **数据库连接池**：Apache DBCP 2
- **数据库工具**：Apache Commons DbUtils
- **JSON处理**：FastJSON

## 核心功能模块

### 🔐 用户管理
- 用户注册（带表单验证和密码加密）
- 用户登录（支持记住我功能）
- 用户信息修改
- 角色权限管理（管理员/普通用户）
- CSRF防护

### 📦 商品管理
- 商品列表展示（支持分页）
- 商品详情查看
- 商品分类筛选
- 管理员商品CRUD操作
- 商品图片上传

### 🏷️ 分类管理
- 分类层级展示
- 管理员分类CRUD操作

### 🛒 购物车管理
- 添加商品到购物车
- 查看购物车商品
- 修改购物车商品数量
- 删除购物车商品
- 购物车总价自动计算

### 📋 订单管理
- 创建订单
- 查看个人订单列表
- 查看订单详情

### 🛠️ 后台管理
- 仪表盘数据统计
- 商品管理面板
- 分类管理面板

## 项目结构

```
ecommerce-system
├── src/
│   ├── main/
│   │   ├── java/com/ecommerce/        # 主包目录
│   │   │   ├── controller/           # 控制器层，处理HTTP请求
│   │   │   ├── dao/                  # 数据访问层，数据库操作
│   │   │   │   └── impl/             # DAO实现类
│   │   │   ├── pojo/                 # 实体类，映射数据库表
│   │   │   ├── service/              # 业务逻辑层
│   │   │   │   └── impl/             # Service实现类
│   │   │   └── utils/                # 工具类
│   │   ├── resources/                # 资源文件
│   │   │   ├── db.properties         # 数据库配置
│   │   │   └── sql/                  # SQL初始化脚本
│   │   └── webapp/                   # Web应用目录
│   │       ├── WEB-INF/              # Web配置文件
│   │       ├── images/               # 商品图片资源
│   │       └── *.jsp                 # JSP页面
│   └── test/                         # 测试代码目录
├── .gitignore                        # Git忽略文件
├── pom.xml                           # Maven配置文件
└── README.md                         # 项目说明文档
```

## 环境要求

- **JDK**：1.8 或以上
- **Maven**：3.6 或以上
- **MySQL**：5.7 或以上
- **Tomcat**：7.0 或以上

## 快速开始

### 1. 数据库配置

#### 1.1 创建数据库
```sql
CREATE DATABASE IF NOT EXISTS ecommerce DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

#### 1.2 修改配置文件
编辑 `src/main/resources/db.properties` 文件，配置数据库连接信息：

```properties
# 数据库连接信息
url=jdbc:mysql://localhost:3306/ecommerce?useUnicode=true&characterEncoding=utf8&serverTimezone=UTC
username=root
password=your_password_here

# 连接池配置
initialSize=5
maxActive=10
maxIdle=5
minIdle=3
```

### 2. 项目编译与运行

#### 2.1 编译打包
```bash
mvn clean compile package
```

#### 2.2 部署到Tomcat

1. 将生成的 `target/ecommerce-system.war` 文件复制到Tomcat的 `webapps` 目录
2. 启动Tomcat服务器
3. 访问：`http://localhost:8080/ecommerce-system`

#### 2.3 开发环境直接运行

```bash
mvn tomcat7:run
```

访问：`http://localhost:8080/ecommerce-system`

## 初始数据

系统启动时会自动初始化数据库，包括：

- **管理员账户**：`devuser` / `dev123456`
- **普通用户**：`alice` / `alice123`
- 测试商品和分类数据

## 功能使用指南

### 🎯 普通用户

1. **注册/登录**：访问首页，点击注册或登录
2. **浏览商品**：首页查看商品列表，点击商品查看详情
3. **购物车操作**：
   - 商品详情页点击"加入购物车"
   - 顶部导航点击"购物车"查看
   - 修改数量或删除商品
4. **创建订单**：购物车中点击"结算"创建订单
5. **查看订单**：点击"我的订单"查看历史订单

### 🔧 管理员

1. **登录**：使用管理员账户登录
2. **管理商品**：
   - 点击"商品管理"查看商品列表
   - 点击"添加商品"上传新商品
   - 点击商品后的"编辑"或"删除"按钮进行操作
3. **管理分类**：
   - 点击"分类管理"查看分类列表
   - 点击"添加分类"创建新分类
   - 点击分类后的"编辑"或"删除"按钮进行操作

## 系统特性

### 🛡️ 安全性
- **SQL注入防护**：使用PreparedStatement和DbUtils
- **XSS防护**：JSP页面输出转义
- **CSRF防护**：CSRF Token验证
- **密码安全**：MD5加密存储
- **权限控制**：Filter实现的URL权限管理
- **输入验证**：服务器端表单验证

### 🚀 性能优化
- **数据库连接池**：减少连接创建开销
- **SQL优化**：合理的索引设计
- **代码分层**：清晰的MVC架构

### 📱 用户体验
- 响应式设计
- 友好的错误提示
- 直观的操作界面
- 快速的页面加载

## 关键技术点

### 1. 数据库连接池配置
使用Apache DBCP 2实现高效的数据库连接管理，配置在`JDBCUtils.java`中。

### 2. CSRF防护机制
通过`CSRFTokenUtils.java`生成和验证CSRF Token，防止跨站请求伪造。

### 3. 密码加密
使用`MD5Utils.java`对用户密码进行加密存储，提高安全性。

### 4. 记住我功能
通过`RememberMeUtils.java`实现，使用持久化Cookie技术。

### 5. 数据库初始化
`DatabaseInitListener.java`在应用启动时自动执行SQL脚本，初始化数据库。

## 开发指南

### 开发工具推荐
- IDE：IntelliJ IDEA 或 Eclipse
- 数据库管理工具：Navicat 或 MySQL Workbench
- 浏览器开发工具：Chrome DevTools

### 代码规范
- 遵循Java命名规范
- 方法和类添加适当注释
- 控制器层职责单一
- 业务逻辑封装在Service层
- DAO层只负责数据库操作

## 测试

### 功能测试
1. 注册新用户
2. 登录（使用不同角色）
3. 浏览商品并添加到购物车
4. 管理购物车商品
5. 创建订单
6. 管理员操作商品和分类

### 安全测试
1. 尝试SQL注入攻击
2. 尝试CSRF攻击
3. 测试权限控制

## 部署说明

### 生产环境部署
1. 确保JDK、Maven、MySQL和Tomcat已正确安装
2. 配置生产环境数据库连接
3. 编译打包项目
4. 部署WAR包到Tomcat
5. 配置Tomcat生产环境参数
6. 启动Tomcat服务

### 环境变量配置
推荐在生产环境使用环境变量管理数据库密码等敏感信息。

## 许可证

MIT License

## 更新日志

### v1.0.0 (当前版本)
- 完整的电商系统功能实现
- 安全性增强（CSRF防护、SQL注入防护）
- 响应式设计
- 良好的代码分层架构

## 联系方式

如有问题或建议，欢迎提交Issue或Pull Request。

---

**感谢使用电商商品展示与购物车系统！** 🛒✨
