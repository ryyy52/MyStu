package com.ecommerce.dao;

import com.ecommerce.pojo.Product;

import java.util.List;

/**
 * 商品数据访问接口 - 定义商品数据的数据库操作规范
 * 
 * 职责：
 * 1. 定义商品的CRUD操作
 * 2. 定义商品查询的多种方式
 * 3. 定义商品搜索和分页功能
 * 4. 定义商品库存管理方法
 * 5. 定义商品统计方法
 * 
 * 主要功能：
 * 商品查询:
 * - findById(Integer): 按ID查询单个商品
 * - findAll(): 查询所有商品
 * - findByCategoryId(Integer): 按分类查询商品
 * - findByCategoryIds(List): 按多个分类查询商品
 * 
 * 商品搜索:
 * - search(String): 按关键词搜索商品
 * - searchByPage(String, int, int): 搜索商品（含分页）
 * 
 * 分页查询:
 * - findByPage(int, int): 分页查询所有商品
 * - findByCategoryIdAndPage(Integer, int, int): 按分类分页查询
 * - findByCategoryIdsAndPage(List, int, int): 按多个分类分页查询
 * 
 * 商品统计:
 * - countAll(): 商品总数
 * - countByCategoryId(Integer): 按分类统计
 * - countByCategoryIds(List): 按多个分类统计
 * - countSearchResults(String): 搜索结果数
 * - countLowStockProducts(): 库存紧张商品数（库存<=10）
 * 
 * 商品管理:
 * - save(Product): 新增商品
 * - update(Product): 修改商品
 * - updateStock(Integer, Integer): 修改库存
 * - delete(Integer): 删除商品
 * 
 * 特点：
 * - 支持多条件查询
 * - 支持全文搜索
 * - 支持分页查询优化性能
 * - 支持库存管理
 * - 支持统计分析
 * 
 * 使用场景：
 * - 商品列表展示（分页）
 * - 商品详情查询
 * - 商品搜索
 * - 分类浏览
 * - 库存管理
 * - 销售统计
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
     * 根据分类ID列表查询商品
     * @param categoryIds 分类ID列表
     * @return 商品列表
     */
    List<Product> findByCategoryIds(List<Integer> categoryIds);
    
    /**
     * 根据分类ID列表分页查询商品
     * @param categoryIds 分类ID列表
     * @param offset 偏移量
     * @param limit 每页数量
     * @return 商品列表
     */
    List<Product> findByCategoryIdsAndPage(List<Integer> categoryIds, int offset, int limit);
    
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
    
    /**
     * 统计库存紧张的商品数量（库存 <= 10）
     * @return 库存紧张商品数量
     */
    int countLowStockProducts();
}