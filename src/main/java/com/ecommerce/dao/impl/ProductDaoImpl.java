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

/**
 * 商品数据访问实现类 - 实现商品的数据库操作和复杂查询
 * 
 * 职责：
 * 1. 实现商品的查询、搜索、分页操作
 * 2. 实现库存管理和商品上下架控制
 * 3. 支持多条件组合查询
 * 4. 使用JDBC和参数化查询防止SQL注入
 * 
 * 主要方法：
 * - findById/findAll/findByCategoryId: 基础查询
 * - search/searchByPage: 关键词搜索
 * - findByPage系列: 分页查询
 * - countAll/countByCategoryId等: 数据统计
 * - updateStock: 库存更新
 * 
 * 查询特点：
 * - 使用LIKE支持模糊搜索
 * - WHERE status = 1过滤下架商品
 * - 使用LIMIT实现分页
 * - 按创建时间倒序显示最新商品
 * 
 * 库存管理：
 * - countLowStockProducts(): 统计库存<=10的商品
 * - updateStock(): 修改库存数量
 * 
 * 性能优化：
 * - 使用分页避免大量数据一次性加载
 * - 使用COUNT快速获取总数
 * - 缓存SQL语句提高执行效率
 * 
 * 数据库表：
 * - product: 商品表（id、name、category_id、price、stock、status等）
 */
public class ProductDaoImpl implements ProductDao {
    /** SQL语句常量 */
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
    private static final String COUNT_LOW_STOCK = "SELECT COUNT(*) FROM product WHERE stock <= 10 AND status = 1";

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
        System.out.println("=== ProductDaoImpl.save() 开始执行 ===");
        System.out.println("SQL语句: " + SAVE);
        
        Connection conn = null;
        PreparedStatement ps = null;
        int result = 0;
        try {
            System.out.println("获取数据库连接");
            conn = JDBCUtils.getConnection();
            System.out.println("获取数据库连接成功");
            
            System.out.println("创建PreparedStatement");
            ps = conn.prepareStatement(SAVE);
            
            // 设置参数
            System.out.println("设置SQL参数：");
            ps.setString(1, product.getName());
            System.out.println("参数1 (name): " + product.getName());
            
            ps.setInt(2, product.getCategoryId());
            System.out.println("参数2 (category_id): " + product.getCategoryId());
            
            ps.setBigDecimal(3, product.getPrice());
            System.out.println("参数3 (price): " + product.getPrice());
            
            ps.setInt(4, product.getStock());
            System.out.println("参数4 (stock): " + product.getStock());
            
            ps.setString(5, product.getDescription());
            System.out.println("参数5 (description): " + product.getDescription());
            
            ps.setString(6, product.getImage());
            System.out.println("参数6 (image): " + product.getImage());
            
            ps.setInt(7, product.getStatus());
            System.out.println("参数7 (status): " + product.getStatus());
            
            java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());
            ps.setTimestamp(8, now);
            System.out.println("参数8 (create_time): " + now);
            
            ps.setTimestamp(9, now);
            System.out.println("参数9 (update_time): " + now);
            
            System.out.println("执行SQL语句：ps.executeUpdate()");
            result = ps.executeUpdate();
            System.out.println("SQL执行结果: " + result);
            
        } catch (SQLException e) {
            System.out.println("SQL执行异常: " + e.getMessage());
            e.printStackTrace();
        } finally {
            System.out.println("关闭数据库资源");
            JDBCUtils.close(conn, ps, null);
            System.out.println("关闭数据库资源成功");
        }
        
        System.out.println("ProductDaoImpl.save()返回结果: " + result);
        System.out.println("=== ProductDaoImpl.save() 执行结束 ===");
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
    public List<Product> findByCategoryIds(List<Integer> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Product> products = new ArrayList<>();
        try {
            conn = JDBCUtils.getConnection();
            // 动态生成SQL语句
            StringBuilder sql = new StringBuilder("SELECT id, name, category_id, price, stock, description, image, status, create_time, update_time FROM product WHERE category_id IN (");
            for (int i = 0; i < categoryIds.size(); i++) {
                if (i > 0) {
                    sql.append(",");
                }
                sql.append("?");
            }
            sql.append(") AND status = 1 ORDER BY create_time DESC");
            
            ps = conn.prepareStatement(sql.toString());
            // 设置参数
            for (int i = 0; i < categoryIds.size(); i++) {
                ps.setInt(i + 1, categoryIds.get(i));
            }
            
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
    public List<Product> findByCategoryIdsAndPage(List<Integer> categoryIds, int offset, int limit) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Product> products = new ArrayList<>();
        try {
            conn = JDBCUtils.getConnection();
            // 动态生成SQL语句
            StringBuilder sql = new StringBuilder("SELECT id, name, category_id, price, stock, description, image, status, create_time, update_time FROM product WHERE category_id IN (");
            for (int i = 0; i < categoryIds.size(); i++) {
                if (i > 0) {
                    sql.append(",");
                }
                sql.append("?");
            }
            sql.append(") AND status = 1 ORDER BY create_time DESC LIMIT ?, ?");
            
            ps = conn.prepareStatement(sql.toString());
            // 设置分类ID参数
            for (int i = 0; i < categoryIds.size(); i++) {
                ps.setInt(i + 1, categoryIds.get(i));
            }
            // 设置分页参数
            ps.setInt(categoryIds.size() + 1, offset);
            ps.setInt(categoryIds.size() + 2, limit);
            
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
    public int countByCategoryIds(List<Integer> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return 0;
        }
        
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int count = 0;
        try {
            conn = JDBCUtils.getConnection();
            // 动态生成SQL语句
            StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM product WHERE category_id IN (");
            for (int i = 0; i < categoryIds.size(); i++) {
                if (i > 0) {
                    sql.append(",");
                }
                sql.append("?");
            }
            sql.append(") AND status = 1");
            
            ps = conn.prepareStatement(sql.toString());
            // 设置参数
            for (int i = 0; i < categoryIds.size(); i++) {
                ps.setInt(i + 1, categoryIds.get(i));
            }
            
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

    @Override
    public int countLowStockProducts() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int count = 0;
        try {
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(COUNT_LOW_STOCK);
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