package com.ecommerce.dao.impl;

import com.ecommerce.dao.ProductDao;
import com.ecommerce.pojo.Product;
import com.ecommerce.utils.JDBCUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductDaoImpl implements ProductDao {
    private static final String FIND_BY_ID = "SELECT id, name, category_id, price, stock, description, image, status, create_time, update_time FROM product WHERE id = ?";
    private static final String FIND_ALL = "SELECT id, name, category_id, price, stock, description, image, status, create_time, update_time FROM product WHERE status = 1 ORDER BY create_time DESC";
    private static final String FIND_BY_CATEGORY_ID = "SELECT id, name, category_id, price, stock, description, image, status, create_time, update_time FROM product WHERE category_id = ? AND status = 1 ORDER BY create_time DESC";
    private static final String SEARCH = "SELECT id, name, category_id, price, stock, description, image, status, create_time, update_time FROM product WHERE name LIKE ? AND status = 1 ORDER BY create_time DESC";
    private static final String SAVE = "INSERT INTO product (name, category_id, price, stock, description, image, status, create_time, update_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE product SET name = ?, category_id = ?, price = ?, stock = ?, description = ?, image = ?, status = ?, update_time = ? WHERE id = ?";
    private static final String UPDATE_STOCK = "UPDATE product SET stock = ? WHERE id = ?";
    private static final String DELETE = "DELETE FROM product WHERE id = ?";
    private static final String FIND_BY_PAGE = "SELECT id, name, category_id, price, stock, description, image, status, create_time, update_time FROM product WHERE status = 1 ORDER BY create_time DESC LIMIT ?, ?";
    private static final String FIND_BY_CATEGORY_ID_AND_PAGE = "SELECT id, name, category_id, price, stock, description, image, status, create_time, update_time FROM product WHERE category_id = ? AND status = 1 ORDER BY create_time DESC LIMIT ?, ?";
    private static final String SEARCH_BY_PAGE = "SELECT id, name, category_id, price, stock, description, image, status, create_time, update_time FROM product WHERE name LIKE ? AND status = 1 ORDER BY create_time DESC LIMIT ?, ?";
    private static final String COUNT_ALL = "SELECT COUNT(*) FROM product WHERE status = 1";
    private static final String COUNT_BY_CATEGORY_ID = "SELECT COUNT(*) FROM product WHERE category_id = ? AND status = 1";
    private static final String COUNT_SEARCH_RESULTS = "SELECT COUNT(*) FROM product WHERE name LIKE ? AND status = 1";

    @Override
    public Product findById(Integer id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Product product = null;
        try {
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(FIND_BY_ID);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                product = new Product();
                product.setId(rs.getInt("id"));
                product.setName(rs.getString("name"));
                product.setCategoryId(rs.getInt("category_id"));
                product.setPrice(rs.getBigDecimal("price"));
                product.setStock(rs.getInt("stock"));
                product.setDescription(rs.getString("description"));
                product.setImage(rs.getString("image"));
                product.setStatus(rs.getInt("status"));
                product.setCreateTime(rs.getTimestamp("create_time"));
                product.setUpdateTime(rs.getTimestamp("update_time"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.close(conn, ps, rs);
        }
        return product;
    }

    @Override
    public List<Product> findAll() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Product> products = new ArrayList<>();
        try {
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(FIND_ALL);
            rs = ps.executeQuery();
            while (rs.next()) {
                Product product = new Product();
                product.setId(rs.getInt("id"));
                product.setName(rs.getString("name"));
                product.setCategoryId(rs.getInt("category_id"));
                product.setPrice(rs.getBigDecimal("price"));
                product.setStock(rs.getInt("stock"));
                product.setDescription(rs.getString("description"));
                product.setImage(rs.getString("image"));
                product.setStatus(rs.getInt("status"));
                product.setCreateTime(rs.getTimestamp("create_time"));
                product.setUpdateTime(rs.getTimestamp("update_time"));
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.close(conn, ps, rs);
        }
        return products;
    }

    @Override
    public List<Product> findByCategoryId(Integer categoryId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Product> products = new ArrayList<>();
        try {
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(FIND_BY_CATEGORY_ID);
            ps.setInt(1, categoryId);
            rs = ps.executeQuery();
            while (rs.next()) {
                Product product = new Product();
                product.setId(rs.getInt("id"));
                product.setName(rs.getString("name"));
                product.setCategoryId(rs.getInt("category_id"));
                product.setPrice(rs.getBigDecimal("price"));
                product.setStock(rs.getInt("stock"));
                product.setDescription(rs.getString("description"));
                product.setImage(rs.getString("image"));
                product.setStatus(rs.getInt("status"));
                product.setCreateTime(rs.getTimestamp("create_time"));
                product.setUpdateTime(rs.getTimestamp("update_time"));
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.close(conn, ps, rs);
        }
        return products;
    }

    @Override
    public List<Product> search(String keyword) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Product> products = new ArrayList<>();
        try {
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(SEARCH);
            ps.setString(1, "%" + keyword + "%");
            rs = ps.executeQuery();
            while (rs.next()) {
                Product product = new Product();
                product.setId(rs.getInt("id"));
                product.setName(rs.getString("name"));
                product.setCategoryId(rs.getInt("category_id"));
                product.setPrice(rs.getBigDecimal("price"));
                product.setStock(rs.getInt("stock"));
                product.setDescription(rs.getString("description"));
                product.setImage(rs.getString("image"));
                product.setStatus(rs.getInt("status"));
                product.setCreateTime(rs.getTimestamp("create_time"));
                product.setUpdateTime(rs.getTimestamp("update_time"));
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.close(conn, ps, rs);
        }
        return products;
    }

    @Override
    public int save(Product product) {
        Connection conn = null;
        PreparedStatement ps = null;
        int result = 0;
        try {
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(SAVE);
            ps.setString(1, product.getName());
            ps.setInt(2, product.getCategoryId());
            ps.setBigDecimal(3, product.getPrice());
            ps.setInt(4, product.getStock());
            ps.setString(5, product.getDescription());
            ps.setString(6, product.getImage());
            ps.setInt(7, product.getStatus());
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
    public List<Product> findByPage(int offset, int limit) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Product> products = new ArrayList<>();
        try {
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(FIND_BY_PAGE);
            ps.setInt(1, offset);
            ps.setInt(2, limit);
            rs = ps.executeQuery();
            while (rs.next()) {
                Product product = new Product();
                product.setId(rs.getInt("id"));
                product.setName(rs.getString("name"));
                product.setCategoryId(rs.getInt("category_id"));
                product.setPrice(rs.getBigDecimal("price"));
                product.setStock(rs.getInt("stock"));
                product.setDescription(rs.getString("description"));
                product.setImage(rs.getString("image"));
                product.setStatus(rs.getInt("status"));
                product.setCreateTime(rs.getTimestamp("create_time"));
                product.setUpdateTime(rs.getTimestamp("update_time"));
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.close(conn, ps, rs);
        }
        return products;
    }

    @Override
    public List<Product> findByCategoryIdAndPage(Integer categoryId, int offset, int limit) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Product> products = new ArrayList<>();
        try {
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(FIND_BY_CATEGORY_ID_AND_PAGE);
            ps.setInt(1, categoryId);
            ps.setInt(2, offset);
            ps.setInt(3, limit);
            rs = ps.executeQuery();
            while (rs.next()) {
                Product product = new Product();
                product.setId(rs.getInt("id"));
                product.setName(rs.getString("name"));
                product.setCategoryId(rs.getInt("category_id"));
                product.setPrice(rs.getBigDecimal("price"));
                product.setStock(rs.getInt("stock"));
                product.setDescription(rs.getString("description"));
                product.setImage(rs.getString("image"));
                product.setStatus(rs.getInt("status"));
                product.setCreateTime(rs.getTimestamp("create_time"));
                product.setUpdateTime(rs.getTimestamp("update_time"));
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.close(conn, ps, rs);
        }
        return products;
    }

    @Override
    public List<Product> searchByPage(String keyword, int offset, int limit) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Product> products = new ArrayList<>();
        try {
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(SEARCH_BY_PAGE);
            ps.setString(1, "%" + keyword + "%");
            ps.setInt(2, offset);
            ps.setInt(3, limit);
            rs = ps.executeQuery();
            while (rs.next()) {
                Product product = new Product();
                product.setId(rs.getInt("id"));
                product.setName(rs.getString("name"));
                product.setCategoryId(rs.getInt("category_id"));
                product.setPrice(rs.getBigDecimal("price"));
                product.setStock(rs.getInt("stock"));
                product.setDescription(rs.getString("description"));
                product.setImage(rs.getString("image"));
                product.setStatus(rs.getInt("status"));
                product.setCreateTime(rs.getTimestamp("create_time"));
                product.setUpdateTime(rs.getTimestamp("update_time"));
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.close(conn, ps, rs);
        }
        return products;
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

    @Override
    public int countByCategoryId(Integer categoryId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int count = 0;
        try {
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(COUNT_BY_CATEGORY_ID);
            ps.setInt(1, categoryId);
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

    @Override
    public int countSearchResults(String keyword) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int count = 0;
        try {
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(COUNT_SEARCH_RESULTS);
            ps.setString(1, "%" + keyword + "%");
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

    @Override
    public int update(Product product) {
        Connection conn = null;
        PreparedStatement ps = null;
        int result = 0;
        try {
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(UPDATE);
            ps.setString(1, product.getName());
            ps.setInt(2, product.getCategoryId());
            ps.setBigDecimal(3, product.getPrice());
            ps.setInt(4, product.getStock());
            ps.setString(5, product.getDescription());
            ps.setString(6, product.getImage());
            ps.setInt(7, product.getStatus());
            ps.setTimestamp(8, new java.sql.Timestamp(System.currentTimeMillis()));
            ps.setInt(9, product.getId());
            result = ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.close(conn, ps, null);
        }
        return result;
    }

    @Override
    public int updateStock(Integer id, Integer stock) {
        Connection conn = null;
        PreparedStatement ps = null;
        int result = 0;
        try {
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(UPDATE_STOCK);
            ps.setInt(1, stock);
            ps.setInt(2, id);
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