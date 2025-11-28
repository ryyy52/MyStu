package com.ecommerce.service;

import com.ecommerce.pojo.Product;

import java.util.List;

/**
 * 商品业务逻辑接口
 */
public interface ProductService {
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
     * 搜索商品
     * @param keyword 关键词
     * @return 商品列表
     */
    List<Product> search(String keyword);

    /**
     * 保存商品
     * @param product 商品对象
     * @return 保存是否成功
     */
    boolean save(Product product);

    /**
     * 更新商品
     * @param product 商品对象
     * @return 更新是否成功
     */
    boolean update(Product product);

    /**
     * 更新商品库存
     * @param productId 商品ID
     * @param quantity 库存数量
     * @return 更新是否成功
     */
    boolean updateStock(Integer productId, Integer quantity);

    /**
     * 删除商品
     * @param id 商品ID
     * @return 删除是否成功
     */
    boolean delete(Integer id);

    /**
     * 分页查询商品
     * @param page 页码（从1开始）
     * @param pageSize 每页数量
     * @return 商品列表
     */
    List<Product> findByPage(int page, int pageSize);

    /**
     * 根据分类ID分页查询商品
     * @param categoryId 分类ID
     * @param page 页码（从1开始）
     * @param pageSize 每页数量
     * @return 商品列表
     */
    List<Product> findByCategoryIdAndPage(Integer categoryId, int page, int pageSize);

    /**
     * 搜索商品（分页）
     * @param keyword 搜索关键词
     * @param page 页码（从1开始）
     * @param pageSize 每页数量
     * @return 商品列表
     */
    List<Product> searchByPage(String keyword, int page, int pageSize);

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
     * 根据分类ID列表查询商品
     * @param categoryIds 分类ID列表
     * @return 商品列表
     */
    List<Product> findByCategoryIds(List<Integer> categoryIds);
    
    /**
     * 根据分类ID列表分页查询商品
     * @param categoryIds 分类ID列表
     * @param page 页码（从1开始）
     * @param pageSize 每页数量
     * @return 商品列表
     */
    List<Product> findByCategoryIdsAndPage(List<Integer> categoryIds, int page, int pageSize);
    
    /**
     * 根据分类ID列表获取商品总数
     * @param categoryIds 分类ID列表
     * @return 商品总数
     */
    int countByCategoryIds(List<Integer> categoryIds);
    
    /**
     * 搜索商品总数
     * @param keyword 搜索关键词
     * @return 商品总数
     */
    int countSearchResults(String keyword);
}