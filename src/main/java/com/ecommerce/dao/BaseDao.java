package com.ecommerce.dao;

import com.ecommerce.utils.JDBCUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 通用DAO基类，提供通用的JDBC操作方法
 */
public abstract class BaseDao {

    /**
     * 通用的增删改方法
     * @param sql SQL语句
     * @param params 参数数组
     * @return 影响的行数
     */
    protected int executeUpdate(String sql, Object... params) {
        Connection conn = null;
        PreparedStatement ps = null;
        int result = 0;
        try {
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(sql);
            // 设置参数
            if (params != null && params.length > 0) {
                for (int i = 0; i < params.length; i++) {
                    ps.setObject(i + 1, params[i]);
                }
            }
            result = ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.close(conn, ps, null);
        }
        return result;
    }

    /**
     * 通用的查询方法
     * @param sql SQL语句
     * @param rowMapper 结果集映射器
     * @param params 参数数组
     * @param <T> 泛型类型
     * @return 查询结果列表
     */
    protected <T> List<T> executeQuery(String sql, RowMapper<T> rowMapper, Object... params) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<T> result = new ArrayList<>();
        try {
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(sql);
            // 设置参数
            if (params != null && params.length > 0) {
                for (int i = 0; i < params.length; i++) {
                    ps.setObject(i + 1, params[i]);
                }
            }
            rs = ps.executeQuery();
            // 映射结果集
            while (rs.next()) {
                T t = rowMapper.mapRow(rs);
                result.add(t);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.close(conn, ps, rs);
        }
        return result;
    }

    /**
     * 通用的查询单个对象方法
     * @param sql SQL语句
     * @param rowMapper 结果集映射器
     * @param params 参数数组
     * @param <T> 泛型类型
     * @return 查询结果对象
     */
    protected <T> T executeQueryForObject(String sql, RowMapper<T> rowMapper, Object... params) {
        List<T> list = executeQuery(sql, rowMapper, params);
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * 通用的查询单个值方法
     * @param sql SQL语句
     * @param params 参数数组
     * @return 查询结果值
     */
    protected <T> T executeQueryForValue(String sql, Object... params) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        T result = null;
        try {
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(sql);
            // 设置参数
            if (params != null && params.length > 0) {
                for (int i = 0; i < params.length; i++) {
                    ps.setObject(i + 1, params[i]);
                }
            }
            rs = ps.executeQuery();
            // 获取结果
            if (rs.next()) {
                result = (T) rs.getObject(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.close(conn, ps, rs);
        }
        return result;
    }

    /**
     * 结果集映射器接口
     * @param <T> 泛型类型
     */
    public interface RowMapper<T> {
        /**
         * 将结果集的一行映射为一个对象
         * @param rs 结果集
         * @return 映射后的对象
         * @throws SQLException SQL异常
         */
        T mapRow(ResultSet rs) throws SQLException;
    }
}
