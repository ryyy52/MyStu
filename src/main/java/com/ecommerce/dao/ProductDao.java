package com.ecommerce.dao;

import com.ecommerce.pojo.Product;

import java.util.List;

/**
 * 商品数据访问接口
 */
public interface ProductDao {
    /**
     * 根据ID查询商品
     * @param id 商品ID
     * @return 商品对象
     */
    Product findById(Integer id);

    /**
     * 查询所有商品
     * @return 商品列表
     */
    List<Product> findAll();

    /**
     * 根据分类ID查询商品
     * @param categoryId 分类ID
     * @return 商品列表
     */
    List<Product> findByCategoryId(Integer categoryId);

    /**
     * 根据关键词搜索商品
     * @param keyword 搜索关键词
     * @return 商品列表
     */
    List<Product> search(String keyword);

    /**
     * 分页查询商品
     * @param offset 偏移量
     * @param limit 每页数量
     * @return 商品列表
     */
    List<Product> findByPage(int offset, int limit);

    /**
     * 根据分类ID分页查询商品
     * @param categoryId 分类ID
     * @param offset 偏移量
     * @param limit 每页数量
     * @return 商品列表
     */
    List<Product> findByCategoryIdAndPage(Integer categoryId, int offset, int limit);

    /**
     * 搜索商品（分页）
     * @param keyword 搜索关键词
     * @param offset 偏移量
     * @param limit 每页数量
     * @return 商品列表
     */
    List<Product> searchByPage(String keyword, int offset, int limit);

    /**
     * 获取商品总数
     * @return 商品总数
     */
    int countAll();

    /**
     * 根据分类ID获取商品总数
     * @param categoryId 分类ID
     * @return 商品总数
     */
    int countByCategoryId(Integer categoryId);

    /**
     * 搜索商品总数
     * @param keyword 搜索关键词
     * @return 商品总数
     */
    int countSearchResults(String keyword);

    /**
     * 保存商品
     * @param product 商品对象
     * @return 影响的行数
     */
    int save(Product product);

    /**
     * 更新商品
     * @param product 商品对象
     * @return 影响的行数
     */
    int update(Product product);

    /**
     * 更新商品库存
     * @param id 商品ID
     * @param stock 新库存数量
     * @return 影响的行数
     */
    int updateStock(Integer id, Integer stock);

    /**
     * 删除商品
     * @param id 商品ID
     * @return 影响的行数
     */
    int delete(Integer id);
}