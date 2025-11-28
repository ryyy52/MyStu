package com.ecommerce.dao.impl;

import com.ecommerce.dao.CategoryDao;
import com.ecommerce.pojo.Category;
import com.ecommerce.utils.JDBCUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 商品分类数据访问实现类
 */
public class CategoryDaoImpl implements CategoryDao {
    private static final String FIND_BY_ID = "SELECT id, name, parent_id, level, sort, icon, description, create_time, update_time FROM category WHERE id = ?";
    private static final String FIND_ALL = "SELECT id, name, parent_id, level, sort, icon, description, create_time, update_time FROM category ORDER BY parent_id ASC, sort ASC, id ASC";
    private static final String FIND_BY_PARENT_ID = "SELECT id, name, parent_id, level, sort, icon, description, create_time, update_time FROM category WHERE parent_id = ? ORDER BY sort ASC";
    private static final String SAVE = "INSERT INTO category (name, parent_id, level, sort, icon, description, create_time, update_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE category SET name = ?, parent_id = ?, level = ?, sort = ?, icon = ?, description = ?, update_time = ? WHERE id = ?";
    private static final String DELETE = "DELETE FROM category WHERE id = ?";

    @Override
    public Category findById(Integer id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Category category = null;
        try {
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(FIND_BY_ID);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                category = new Category();
                category.setId(rs.getInt("id"));
                category.setName(rs.getString("name"));
                category.setParentId(rs.getInt("parent_id"));
                category.setLevel(rs.getInt("level"));
                category.setSort(rs.getInt("sort"));
                category.setIcon(rs.getString("icon"));
                category.setDescription(rs.getString("description"));
                category.setCreateTime(rs.getTimestamp("create_time"));
                category.setUpdateTime(rs.getTimestamp("update_time"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.close(conn, ps, rs);
        }
        return category;
    }

    @Override
    public List<Category> findAll() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Category> categories = new ArrayList<>();
        try {
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(FIND_ALL);
            rs = ps.executeQuery();
            while (rs.next()) {
                Category category = new Category();
                category.setId(rs.getInt("id"));
                category.setName(rs.getString("name"));
                category.setParentId(rs.getInt("parent_id"));
                category.setLevel(rs.getInt("level"));
                category.setSort(rs.getInt("sort"));
                category.setIcon(rs.getString("icon"));
                category.setDescription(rs.getString("description"));
                category.setCreateTime(rs.getTimestamp("create_time"));
                category.setUpdateTime(rs.getTimestamp("update_time"));
                categories.add(category);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.close(conn, ps, rs);
        }
        return categories;
    }

    @Override
    public List<Category> findByParentId(Integer parentId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Category> categories = new ArrayList<>();
        try {
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(FIND_BY_PARENT_ID);
            ps.setInt(1, parentId);
            rs = ps.executeQuery();
            while (rs.next()) {
                Category category = new Category();
                category.setId(rs.getInt("id"));
                category.setName(rs.getString("name"));
                category.setParentId(rs.getInt("parent_id"));
                category.setLevel(rs.getInt("level"));
                category.setSort(rs.getInt("sort"));
                category.setIcon(rs.getString("icon"));
                category.setDescription(rs.getString("description"));
                category.setCreateTime(rs.getTimestamp("create_time"));
                category.setUpdateTime(rs.getTimestamp("update_time"));
                categories.add(category);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.close(conn, ps, rs);
        }
        return categories;
    }

    @Override
    public int save(Category category) {
        Connection conn = null;
        PreparedStatement ps = null;
        int result = 0;
        try {
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(SAVE);
            ps.setString(1, category.getName());
            ps.setInt(2, category.getParentId());
            ps.setInt(3, category.getLevel());
            ps.setInt(4, category.getSort());
            ps.setString(5, category.getIcon());
            ps.setString(6, category.getDescription());
            ps.setTimestamp(7, new java.sql.Timestamp(System.currentTimeMillis()));
            ps.setTimestamp(8, new java.sql.Timestamp(System.currentTimeMillis()));
            result = ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.close(conn, ps, null);
        }
        return result;
    }

    @Override
    public int update(Category category) {
        Connection conn = null;
        PreparedStatement ps = null;
        int result = 0;
        try {
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(UPDATE);
            ps.setString(1, category.getName());
            ps.setInt(2, category.getParentId());
            ps.setInt(3, category.getLevel());
            ps.setInt(4, category.getSort());
            ps.setString(5, category.getIcon());
            ps.setString(6, category.getDescription());
            ps.setTimestamp(7, new java.sql.Timestamp(System.currentTimeMillis()));
            ps.setInt(8, category.getId());
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
}