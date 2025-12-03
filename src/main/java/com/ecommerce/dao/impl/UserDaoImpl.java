package com.ecommerce.dao.impl;

import com.ecommerce.dao.UserDao;
import com.ecommerce.pojo.User;
import com.ecommerce.utils.JDBCUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * 用户数据访问实现类 - 实现用户数据的数据库操作
 * 
 * 职责：
 * 1. 实现用户的查询操作（多条件查询）
 * 2. 实现用户的新增、修改、删除操作
 * 3. 支持用户数据的唯一性检查
 * 4. 使用JDBC和参数化查询
 * 
 * 主要方法：
 * - findById: 按用户ID查询
 * - findByUsername: 按用户名查询（唯一）
 * - findByEmail: 按邮箱查询（唯一）
 * - save/update/delete: 用户CRUD操作
 * - countAll: 统计用户总数
 * 
 * 用户属性说明：
 * - username和email都是唯一的，用于唯一性检查
 * - password存储MD5加密值，不应直接返回
 * - status控制账户启用/禁用
 * - role字段定义用户权限（admin/user）
 * 
 * 实现特点：
 * - 查询返回完整用户信息
 * - 支持多条件查询
 * - 完整的异常处理和资源释放
 * - 支持用户数据统计
 * 
 * 安全特点：
 * - 使用参数化查询防止SQL注入
 * - 密码以MD5形式存储
 * - 支持账户状态控制
 * 
 * 数据库表：
 * - user: 用户表（id、username、password、email、phone、address、status、role等）
 */
public class UserDaoImpl implements UserDao {
    /** SQL语句常量 */
    private static final String FIND_BY_ID = "SELECT id, username, password, email, phone, address, status, role, create_time, update_time FROM user WHERE id = ?";
    private static final String FIND_BY_USERNAME = "SELECT id, username, password, email, phone, address, status, role, create_time, update_time FROM user WHERE username = ?";
    private static final String FIND_BY_EMAIL = "SELECT id, username, password, email, phone, address, status, role, create_time, update_time FROM user WHERE email = ?";
    private static final String SAVE = "INSERT INTO user (username, password, email, phone, address, status, role, create_time, update_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE user SET username = ?, password = ?, email = ?, phone = ?, address = ?, status = ?, role = ?, update_time = ? WHERE id = ?";
    private static final String DELETE = "DELETE FROM user WHERE id = ?";
    private static final String COUNT_ALL = "SELECT COUNT(*) FROM user";

    @Override
    public User findById(Integer id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        User user = null;
        try {
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(FIND_BY_ID);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getString("phone"));
                user.setAddress(rs.getString("address"));
                user.setStatus(rs.getInt("status"));
                user.setRole(rs.getString("role"));
                user.setCreateTime(rs.getTimestamp("create_time"));
                user.setUpdateTime(rs.getTimestamp("update_time"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.close(conn, ps, rs);
        }
        return user;
    }

    @Override
    public User findByUsername(String username) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        User user = null;
        try {
            System.out.println("DEBUG UserDaoImpl: Executing findByUsername for: " + username);
            conn = JDBCUtils.getConnection();
            System.out.println("DEBUG UserDaoImpl: Connection obtained: " + (conn != null));
            ps = conn.prepareStatement(FIND_BY_USERNAME);
            ps.setString(1, username);
            System.out.println("DEBUG UserDaoImpl: Executing query: " + FIND_BY_USERNAME);
            rs = ps.executeQuery();
            if (rs.next()) {
                user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getString("phone"));
                user.setAddress(rs.getString("address"));
                user.setStatus(rs.getInt("status"));
                user.setRole(rs.getString("role"));
                user.setCreateTime(rs.getTimestamp("create_time"));
                user.setUpdateTime(rs.getTimestamp("update_time"));
                System.out.println("DEBUG UserDaoImpl: User found - ID: " + user.getId() + ", Username: " + user.getUsername() + ", Status: " + user.getStatus() + ", Role: " + user.getRole());
            } else {
                System.out.println("DEBUG UserDaoImpl: No user found for username: " + username);
            }
        } catch (SQLException e) {
            System.out.println("DEBUG UserDaoImpl: SQLException occurred: " + e.getMessage());
            e.printStackTrace();
        } finally {
            JDBCUtils.close(conn, ps, rs);
        }
        return user;
    }

    @Override
    public User findByEmail(String email) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        User user = null;
        try {
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(FIND_BY_EMAIL);
            ps.setString(1, email);
            rs = ps.executeQuery();
            if (rs.next()) {
                user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getString("phone"));
                user.setAddress(rs.getString("address"));
                user.setStatus(rs.getInt("status"));
                user.setRole(rs.getString("role"));
                user.setCreateTime(rs.getTimestamp("create_time"));
                user.setUpdateTime(rs.getTimestamp("update_time"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.close(conn, ps, rs);
        }
        return user;
    }

    @Override
    public int save(User user) {
        Connection conn = null;
        PreparedStatement ps = null;
        int result = 0;
        try {
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(SAVE);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPhone());
            ps.setString(5, user.getAddress());
            ps.setInt(6, user.getStatus());
            ps.setString(7, user.getRole() != null ? user.getRole() : "user");
            ps.setTimestamp(8, new java.sql.Timestamp(System.currentTimeMillis()));
            ps.setTimestamp(9, new java.sql.Timestamp(System.currentTimeMillis()));
            result = ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.close(conn, ps, null);
        }
        return result;
    }

    @Override
    public int update(User user) {
        Connection conn = null;
        PreparedStatement ps = null;
        int result = 0;
        try {
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(UPDATE);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPhone());
            ps.setString(5, user.getAddress());
            ps.setInt(6, user.getStatus());
            ps.setString(7, user.getRole() != null ? user.getRole() : "user");
            ps.setTimestamp(8, new java.sql.Timestamp(System.currentTimeMillis()));
            ps.setInt(9, user.getId());
            result = ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.close(conn, ps, null);
        }
        return result;
    }

    @Override
    public int delete(Integer id) {
        Connection conn = null;
        PreparedStatement ps = null;
        int result = 0;
        try {
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(DELETE);
            ps.setInt(1, id);
            result = ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.close(conn, ps, null);
        }
        return result;
    }

    @Override
    public int countAll() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int count = 0;
        try {
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(COUNT_ALL);
            rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.close(conn, ps, rs);
        }
        return count;
    }
}