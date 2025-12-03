package com.ecommerce.service.impl;

import com.ecommerce.dao.UserDao;
import com.ecommerce.dao.impl.UserDaoImpl;
import com.ecommerce.pojo.User;
import com.ecommerce.service.UserService;
import com.ecommerce.utils.MD5Utils;

import java.util.Objects;

/**
 * 用户业务逻辑实现类 - 实现UserService接口的具体业务逻辑
 * 
 * 功能说明：
 * 1. 用户查询：根据ID、用户名、邮箱查询用户
 * 2. 用户注册：新用户注册，含唯一性检查和密码加密
 * 3. 用户登录：验证用户身份，支持用户名和密码验证
 * 4. 用户更新：修改用户信息
 * 5. 用户删除：删除用户账户
 * 
 * 核心业务规则：
 * - 用户名和邮箱必须唯一
 * - 密码存储前必须MD5加密
 * - 新注册用户默认启用且角色为普通用户
 * - 登录时密码需要加密后比对
 */
public class UserServiceImpl implements UserService {
    // 注入UserDao依赖 - 用于访问数据库
    private UserDao userDao = new UserDaoImpl();

    /**
     * 根据用户ID查询用户信息
     * @param id 用户ID
     * @return 用户对象，不存在则返回null
     */
    @Override
    public User findById(Integer id) {
        // 直接调用DAO层查询
        return userDao.findById(id);
    }

    /**
     * 根据用户名查询用户信息
     * @param username 用户名
     * @return 用户对象，不存在则返回null
     */
    @Override
    public User findByUsername(String username) {
        // 直接调用DAO层查询
        return userDao.findByUsername(username);
    }

    /**
     * 根据邮箱查询用户信息
     * @param email 邮箱地址
     * @return 用户对象，不存在则返回null
     */
    @Override
    public User findByEmail(String email) {
        // 直接调用DAO层查询
        return userDao.findByEmail(email);
    }

    /**
     * 用户注册业务逻辑
     * @param user 新用户信息对象
     * @return true表示注册成功，false表示注册失败（用户名或邮箱已存在）
     */
    @Override
    public boolean register(User user) {
        // 步骤1：检查用户名是否已存在
        User existingUser = userDao.findByUsername(user.getUsername());
        if (existingUser != null) {
            // 用户名已存在，注册失败
            return false;
        }

        // 步骤2：检查邮箱是否已存在
        existingUser = userDao.findByEmail(user.getEmail());
        if (existingUser != null) {
            // 邮箱已存在，注册失败
            return false;
        }

        // 步骤3：设置用户默认状态为1（启用）
        user.setStatus(1);
        // 步骤4：设置用户默认角色为"user"（普通用户）
        user.setRole("user");
        
        // 步骤5：对密码进行MD5加密处理
        String encryptedPassword = MD5Utils.encrypt(user.getPassword());
        // 步骤6：将加密后的密码设置到用户对象中
        user.setPassword(encryptedPassword);

        // 步骤7：调用DAO保存用户到数据库
        int result = userDao.save(user);
        // 步骤8：根据保存结果返回成功/失败标识（result>0表示成功）
        return result > 0;
    }

    /**
     * 用户登录业务逻辑
     * @param username 用户名
     * @param password 密码（明文）
     * @return 登录成功返回用户对象，失败返回null
     */
    @Override
    public User login(String username, String password) {
        // 步骤1：根据用户名查询用户信息
        System.out.println("DEBUG UserServiceImpl: Attempting login for username: " + username);
        User user = userDao.findByUsername(username);
        // 步骤2：如果用户不存在，登录失败
        if (user == null) {
            System.out.println("DEBUG UserServiceImpl: User not found for username: " + username);
            return null;
        }
        System.out.println("DEBUG UserServiceImpl: User found, ID: " + user.getId() + ", Status: " + user.getStatus());

        // 步骤3：对输入的密码进行MD5加密
        String encryptedPassword = MD5Utils.encrypt(password);
        System.out.println("DEBUG UserServiceImpl: Input password: " + password);
        System.out.println("DEBUG UserServiceImpl: Input password encrypted: " + encryptedPassword);
        System.out.println("DEBUG UserServiceImpl: Stored password: " + user.getPassword());
        // 步骤4：比较加密后的密码与数据库中存储的密码是否相同
        System.out.println("DEBUG UserServiceImpl: Password match result: " + Objects.equals(user.getPassword(), encryptedPassword));
        
        // 步骤5：处理不同的密码编码格式（有些系统可能存储大写十六进制）
        String encryptedPasswordUpperCase = encryptedPassword.toUpperCase();
        System.out.println("DEBUG UserServiceImpl: Input password encrypted (uppercase): " + encryptedPasswordUpperCase);
        System.out.println("DEBUG UserServiceImpl: Password match result (uppercase): " + Objects.equals(user.getPassword(), encryptedPasswordUpperCase));
        
        // 步骤6：调试用 - 直接比较原始密码
        System.out.println("DEBUG UserServiceImpl: Direct password match: " + Objects.equals(user.getPassword(), password));
        
        // 步骤7：验证密码（支持两种格式的加密密码）
        if (Objects.equals(user.getPassword(), encryptedPassword) || Objects.equals(user.getPassword(), encryptedPasswordUpperCase)) {
            // 密码正确，登录成功
            System.out.println("DEBUG UserServiceImpl: Login successful for user: " + username);
            return user;
        }
        // 密码错误，登录失败
        System.out.println("DEBUG UserServiceImpl: Login failed - password mismatch");
        return null;
    }

    /**
     * 更新用户信息
     * @param user 包含更新信息的用户对象
     * @return true表示更新成功，false表示更新失败
     */
    @Override
    public boolean update(User user) {
        // 调用DAO层更新用户信息
        int result = userDao.update(user);
        // 根据数据库操作结果返回成功/失败标识
        return result > 0;
    }

    /**
     * 删除用户账户
     * @param id 用户ID
     * @return true表示删除成功，false表示删除失败
     */
    @Override
    public boolean delete(Integer id) {
        // 调用DAO层删除用户
        int result = userDao.delete(id);
        // 根据数据库操作结果返回成功/失败标识（result>0表示至少删除了一行）
        return result > 0;
    }
}