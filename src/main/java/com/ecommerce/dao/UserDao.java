package com.ecommerce.dao;

import com.ecommerce.pojo.User;

/**
 * 用户数据访问接口 - 定义用户数据的数据库操作规范
 * 
 * 职责：
 * 1. 定义用户的查询操作
 * 2. 定义用户的新增、修改、删除操作
 * 3. 定义用户统计方法
 * 
 * 主要功能：
 * 用户查询:
 * - findById(Integer): 按ID查询用户
 * - findByUsername(String): 按用户名查询用户
 * - findByEmail(String): 按邮箱查询用户
 * 
 * 用户管理:
 * - save(User): 新增用户
 * - update(User): 修改用户
 * - delete(Integer): 删除用户
 * 
 * 用户统计:
 * - countAll(): 用户总数
 * 
 * 特点：
 * - 支持多条件查询
 * - 支持唯一性检查（用户名、邮箱）
 * - 支持完整的CRUD操作
 * - 支持用户统计
 * 
 * 使用场景：
 * - 用户注册时检查用户名和邮箱是否存在
 * - 用户登录时查询用户信息
 * - 用户信息修改
 * - 用户账户删除
 * - 用户数据统计
 */
public interface UserDao {
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
     * 保存用户
     * @param user 用户对象
     * @return 影响的行数
     */
    int save(User user);

    /**
     * 更新用户信息
     * @param user 用户对象
     * @return 影响的行数
     */
    int update(User user);

    /**
     * 删除用户
     * @param id 用户ID
     * @return 影响的行数
     */
    int delete(Integer id);
    
    /**
     * 获取用户总数
     * @return 用户总数
     */
    int countAll();
}