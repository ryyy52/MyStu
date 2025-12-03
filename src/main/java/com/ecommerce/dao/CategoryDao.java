package com.ecommerce.dao;

import com.ecommerce.pojo.Category;

import java.util.List;

/**
 * 商品分类数据访问接口 - 定义分类数据的数据库操作规范
 * 
 * 职责：
 * 1. 定义分类数据的CRUD操作
 * 2. 定义分类查询的多种方式
 * 3. 定义分类树形结构查询
 * 
 * 主要功能：
 * 分类查询:
 * - findById(Integer): 按ID查询分类
 * - findAll(): 查询所有分类
 * - findByParentId(Integer): 按父分类ID查询子分类
 * 
 * 分类管理:
 * - save(Category): 新增分类
 * - update(Category): 修改分类
 * - delete(Integer): 删除分类
 * 
 * 特点：
 * - 支持树形结构查询
 * - 支持多层级分类
 * - 支持分类排序
 * - 完整的CRUD操作
 * 
 * 使用场景：
 * - 分类数据的持久化
 * - 分类树的构建
 * - 商品分类管理
 * - 商品分类浏览
 */
public interface CategoryDao {
    /**
     * 根据ID查询分类
     * @param id 分类ID
     * @return 分类对象
     */
    Category findById(Integer id);

    /**
     * 查询所有分类
     * @return 分类列表
     */
    List<Category> findAll();

    /**
     * 根据父分类ID查询子分类
     * @param parentId 父分类ID
     * @return 子分类列表
     */
    List<Category> findByParentId(Integer parentId);

    /**
     * 保存分类
     * @param category 分类对象
     * @return 影响的行数
     */
    int save(Category category);

    /**
     * 更新分类
     * @param category 分类对象
     * @return 影响的行数
     */
    int update(Category category);

    /**
     * 删除分类
     * @param id 分类ID
     * @return 影响的行数
     */
    int delete(Integer id);
}