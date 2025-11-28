package com.ecommerce.dao;

import com.ecommerce.pojo.User;

/**
 * 用户数据访问接口
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