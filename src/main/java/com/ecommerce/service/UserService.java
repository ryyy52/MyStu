package com.ecommerce.service;

import com.ecommerce.pojo.User;

/**
 * 用户业务逻辑接口 - 定义用户相关的业务操作规范
 * 
 * 该接口定义了用户管理系统中的所有业务方法，包括查询、注册、登录、更新和删除等操作。
 * 实现类应提供相应的业务逻辑，包括数据验证、密码加密、重复检查等。
 * 
 * 主要功能：
 * - 用户查询：按ID、用户名、邮箱查询用户
 * - 用户注册：验证和保存新用户
 * - 用户登录：验证用户身份
 * - 用户更新：修改用户信息
 * - 用户删除：删除用户账户
 * 
 * 特点：
 * - 提供多种查询方式便于不同场景使用
 * - 支持完整的CRUD操作
 * - 业务方法返回值清晰明确
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