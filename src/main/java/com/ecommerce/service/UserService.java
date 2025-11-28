package com.ecommerce.service;

import com.ecommerce.pojo.User;

/**
 * 用户业务逻辑接口
 */
public interface UserService {
    /**
     * 根据ID查询用户
     * @param id 用户ID
     * @return 用户对象
     */
    User findById(Integer id);

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户对象
     */
    User findByUsername(String username);

    /**
     * 根据邮箱查询用户
     * @param email 邮箱
     * @return 用户对象
     */
    User findByEmail(String email);

    /**
     * 用户注册
     * @param user 用户对象
     * @return 注册是否成功
     */
    boolean register(User user);

    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 登录用户对象
     */
    User login(String username, String password);

    /**
     * 更新用户信息
     * @param user 用户对象
     * @return 更新是否成功
     */
    boolean update(User user);

    /**
     * 删除用户
     * @param id 用户ID
     * @return 删除是否成功
     */
    boolean delete(Integer id);
}