package com.ecommerce.service.impl;

import com.ecommerce.dao.UserDao;
import com.ecommerce.dao.impl.UserDaoImpl;
import com.ecommerce.pojo.User;
import com.ecommerce.service.UserService;
import com.ecommerce.utils.MD5Utils;

import java.util.Objects;

/**
 * 用户业务逻辑实现类
 */
public class UserServiceImpl implements UserService {
    private UserDao userDao = new UserDaoImpl();

    @Override
    public User findById(Integer id) {
        return userDao.findById(id);
    }

    @Override
    public User findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    @Override
    public User findByEmail(String email) {
        return userDao.findByEmail(email);
    }

    @Override
    public boolean register(User user) {
        // 检查用户名是否已存在
        User existingUser = userDao.findByUsername(user.getUsername());
        if (existingUser != null) {
            return false;
        }

        // 检查邮箱是否已存在
        existingUser = userDao.findByEmail(user.getEmail());
        if (existingUser != null) {
            return false;
        }

        // 设置默认状态为1（正常）
        user.setStatus(1);
        // 设置默认角色为user
        user.setRole("user");
        
        // 对密码进行MD5加密
        String encryptedPassword = MD5Utils.encrypt(user.getPassword());
        user.setPassword(encryptedPassword);

        // 保存用户
        int result = userDao.save(user);
        return result > 0;
    }

    @Override
    public User login(String username, String password) {
        // 根据用户名查询用户
        System.out.println("DEBUG UserServiceImpl: Attempting login for username: " + username);
        User user = userDao.findByUsername(username);
        if (user == null) {
            System.out.println("DEBUG UserServiceImpl: User not found for username: " + username);
            return null;
        }
        System.out.println("DEBUG UserServiceImpl: User found, ID: " + user.getId() + ", Status: " + user.getStatus());

        // 对密码进行MD5加密后比较
        String encryptedPassword = MD5Utils.encrypt(password);
        System.out.println("DEBUG UserServiceImpl: Input password: " + password);
        System.out.println("DEBUG UserServiceImpl: Input password encrypted: " + encryptedPassword);
        System.out.println("DEBUG UserServiceImpl: Stored password: " + user.getPassword());
        System.out.println("DEBUG UserServiceImpl: Password match result: " + Objects.equals(user.getPassword(), encryptedPassword));
        
        // 尝试使用不同的加密方式进行比较
        String encryptedPasswordUpperCase = encryptedPassword.toUpperCase();
        System.out.println("DEBUG UserServiceImpl: Input password encrypted (uppercase): " + encryptedPasswordUpperCase);
        System.out.println("DEBUG UserServiceImpl: Password match result (uppercase): " + Objects.equals(user.getPassword(), encryptedPasswordUpperCase));
        
        // 直接比较原始密码（用于调试）
        System.out.println("DEBUG UserServiceImpl: Direct password match: " + Objects.equals(user.getPassword(), password));
        
        if (Objects.equals(user.getPassword(), encryptedPassword) || Objects.equals(user.getPassword(), encryptedPasswordUpperCase)) {
            System.out.println("DEBUG UserServiceImpl: Login successful for user: " + username);
            return user;
        }
        System.out.println("DEBUG UserServiceImpl: Login failed - password mismatch");
        return null;
    }

    @Override
    public boolean update(User user) {
        int result = userDao.update(user);
        return result > 0;
    }

    @Override
    public boolean delete(Integer id) {
        int result = userDao.delete(id);
        return result > 0;
    }
}