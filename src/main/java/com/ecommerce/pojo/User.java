package com.ecommerce.pojo;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户实体类 - 代表电商平台的用户账户
 * 
 * 职责：
 * 1. 存储用户的账户信息
 * 2. 记录用户的个人信息
 * 3. 管理用户的身份和权限
 * 4. 跟踪用户账户状态
 * 
 * 属性说明：
 * - id: 用户唯一标识
 * - username: 用户登录账号（唯一）
 * - password: 用户密码（MD5加密存储）
 * - email: 用户邮箱（唯一）
 * - phone: 用户手机号
 * - address: 用户默认地址
 * - status: 用户账户状态
 *   * 0: 禁用（不允许登录）
 *   * 1: 启用（正常使用）
 * - role: 用户角色
 *   * admin: 管理员
 *   * user: 普通用户
 * - createTime: 账户创建时间
 * - updateTime: 账户最后修改时间
 * 
 * 主要特点：
 * - 实现Serializable接口支持序列化
 * - 密码使用MD5加密存储
 * - 支持用户启用/禁用控制
 * - 支持管理员和普通用户两种角色
 * - 保存用户的基本联系方式
 * 
 * 使用场景：
 * - 用户注册和登录
 * - 用户信息管理（修改个人资料）
 * - 权限控制（根据role判断）
 * - 用户数据统计
 * - 购物车和订单关联
 * 
 * 安全说明：
 * - 密码在存储前必须MD5加密
 * - 登录时比对密码需要进行加密后比对
 * - 不应直接返回密码字段给前端
 */
public class User implements Serializable {
    private Integer id; // 用户ID
    private String username; // 用户名
    private String password; // 密码
    private String email; // 邮箱
    private String phone; // 手机号
    private String address; // 地址
    private Integer status; // 状态（0：禁用，1：启用）
    private String role; // 角色（admin：管理员，user：普通用户）
    private Date createTime; // 创建时间
    private Date updateTime; // 更新时间

    // 构造方法
    public User() {
    }

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    // getter和setter方法
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", status=" + status +
                ", role='" + role + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}