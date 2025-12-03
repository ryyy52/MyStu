package com.ecommerce.service.impl;

import com.ecommerce.dao.CategoryDao;
import com.ecommerce.dao.impl.CategoryDaoImpl;
import com.ecommerce.dao.ProductDao;
import com.ecommerce.dao.impl.ProductDaoImpl;
import com.ecommerce.pojo.Category;
import com.ecommerce.service.CategoryService;

import java.util.ArrayList;
import java.util.List;

/**
 * 商品分类业务逻辑实现类 - 实现CategoryService接口的具体业务逻辑
 * 
 * 功能说明：
 * 1. 分类查询：按ID、父类ID、全量查询
 * 2. 分类树构建：递归构建多级分类树结构
 * 3. 分类管理：新增、修改、删除分类
 * 4. 子分类查询：获取指定分类及其所有子分类
 * 
 * 核心业务规则：
 * - 分类支持多级结构（通过parentId字段实现）
 * - 分类树根节点的parentId为0或null
 * - 删除分类时必须检查是否有子分类和关联商品
 * - 如果有子分类或关联商品，则不允许删除
 * - 支持递归获取分类及其所有子分类ID列表
 * 
 * 依赖关系：
 * - CategoryDao：分类数据访问
 * - ProductDao：商品数据访问（用于检查商品关联）
 */
public class CategoryServiceImpl implements CategoryService {
    // 注入CategoryDao依赖 - 用于访问分类数据库
    private CategoryDao categoryDao = new CategoryDaoImpl();
    // 注入ProductDao依赖 - 用于验证是否有商品关联该分类
    private ProductDao productDao = new ProductDaoImpl();

    /**
     * 根据分类ID查询分类信息
     * @param id 分类ID
     * @return 分类对象，不存在则返回null
     */
    @Override
    public Category findById(Integer id) {
        // 直接调用DAO层查询
        return categoryDao.findById(id);
    }

    /**
     * 查询所有分类
     * @return 分类列表，不包含树形结构关系
     */
    @Override
    public List<Category> findAll() {
        // 直接调用DAO层查询所有分类
        return categoryDao.findAll();
    }

    /**
     * 根据父分类ID查询子分类
     * @param parentId 父分类ID
     * @return 该父分类下的所有子分类列表
     */
    @Override
    public List<Category> findByParentId(Integer parentId) {
        // 直接调用DAO层查询指定父ID的子分类
        return categoryDao.findByParentId(parentId);
    }

    /**
     * 获取完整的分类树业务逻辑
     * 
     * 实现原理：
     * - 查询所有分类
     * - 递归构建树形结构
     * - 根节点的parentId为0
     * 
     * 树形结构示例：
     * 家电 (id=1, parentId=0)
     *   ├─ 电视 (id=2, parentId=1)
     *   └─ 冰箱 (id=3, parentId=1)
     * 图书 (id=4, parentId=0)
     *   ├─ 文学 (id=5, parentId=4)
     *   └─ 技术 (id=6, parentId=4)
     * 
     * @return 完整的分类树列表（只包含顶级分类及其子孙）
     */
    @Override
    public List<Category> getCategoryTree() {
        // 步骤1：获取所有分类
        List<Category> allCategories = categoryDao.findAll();
        // 步骤2：从parentId=0开始递归构建分类树（0表示顶级分类）
        return buildCategoryTree(allCategories, 0);
    }

    /**
     * 递归构建分类树的私有方法
     * 
     * 实现原理：
     * - 从所有分类中查找parentId匹配的分类
     * - 对每个分类递归调用此方法以获取其子分类
     * - 设置每个分类的children属性
     * 
     * 时间复杂度：O(n²) - n为分类总数（嵌套循环）
     * 空间复杂度：O(n) - 树形结构所需空间
     * 
     * @param allCategories 所有分类列表
     * @param parentId 要查找的父分类ID
     * @return 该父分类下的所有子分类列表（已递归设置其children）
     */
    private List<Category> buildCategoryTree(List<Category> allCategories, Integer parentId) {
        // 创建结果列表，用于存储当前级别的分类
        List<Category> children = new ArrayList<>();
        // 遍历所有分类
        for (Category category : allCategories) {
            // 获取分类的父ID（可能为null）
            Integer pid = category.getParentId();
            // 如果parentId为null，使用0作为默认值（表示顶级分类）
            if (pid == null) pid = 0;
            // 检查该分类是否属于当前parentId
            if (pid.equals(parentId)) {
                // 对该分类的子分类进行递归构建
                category.setChildren(buildCategoryTree(allCategories, category.getId()));
                // 将该分类添加到结果列表
                children.add(category);
            }
        }
        // 返回当前级别的所有分类
        return children;
    }

    /**
     * 新增分类业务逻辑
     * @param category 分类对象
     * @return true表示新增成功，false表示新增失败
     */
    @Override
    public boolean save(Category category) {
        // 调用DAO层保存分类到数据库
        int result = categoryDao.save(category);
        // 根据数据库操作结果返回成功/失败标识
        return result > 0;
    }

    /**
     * 修改分类业务逻辑
     * @param category 包含更新信息的分类对象（必须含有ID）
     * @return true表示修改成功，false表示修改失败
     */
    @Override
    public boolean update(Category category) {
        // 调用DAO层更新分类信息到数据库
        int result = categoryDao.update(category);
        // 根据数据库操作结果返回成功/失败标识
        return result > 0;
    }

    /**
     * 删除分类业务逻辑
     * 
     * 删除前置检查：
     * 1. 检查是否有子分类
     * 2. 检查是否有商品属于该分类
     * 3. 同时满足以上两个条件才允许删除
     * 
     * @param id 分类ID
     * @return true表示删除成功，false表示删除失败（有子分类或关联商品）
     */
    @Override
    public boolean delete(Integer id) {
        // 步骤1：检查是否有子分类
        List<Category> children = categoryDao.findByParentId(id);
        if (!children.isEmpty()) {
            // 存在子分类，不能删除
            return false;
        }
        // 步骤2：检查是否有商品属于该分类
        int productCount = productDao.countByCategoryId(id);
        if (productCount > 0) {
            // 仍有关联商品，不能删除
            return false;
        }
        // 步骤3：删除前置检查通过，执行删除操作
        int result = categoryDao.delete(id);
        // 步骤4：根据数据库操作结果返回成功/失败标识
        return result > 0;
    }
    
    /**
     * 获取指定分类及其所有子分类ID业务逻辑
     * 
     * 用途：
     * - 点击父分类时，查询该分类及所有子分类下的商品
     * - 在分类管理中验证分类树
     * 
     * 返回结果示例：
     * 参数：categoryId=1（家电）
     * 返回：[1, 2, 3] （包括家电、电视、冰箱）
     * 
     * @param categoryId 分类ID
     * @return 该分类及其所有子分类的ID列表
     */
    @Override
    public List<Integer> getCategoryIdsWithChildren(Integer categoryId) {
        // 创建结果列表
        List<Integer> categoryIds = new ArrayList<>();
        // 步骤1：添加当前分类ID到结果列表
        categoryIds.add(categoryId);
        // 步骤2：递归添加所有子分类ID
        addChildrenIds(categoryId, categoryIds);
        // 步骤3：返回包含当前分类及所有子分类的ID列表
        return categoryIds;
    }
    
    /**
     * 递归添加所有子分类ID的私有方法
     * 
     * 实现原理：
     * - 查询指定parentId的所有子分类
     * - 对每个子分类添加其ID
     * - 递归调用以处理子分类的子分类
     * 
     * @param parentId 父分类ID
     * @param categoryIds 分类ID列表（作为累积结果，会被修改）
     */
    private void addChildrenIds(Integer parentId, List<Integer> categoryIds) {
        // 步骤1：查询该父ID下的所有子分类
        List<Category> children = categoryDao.findByParentId(parentId);
        // 步骤2：遍历所有子分类
        for (Category child : children) {
            // 步骤3：将子分类ID添加到列表
            categoryIds.add(child.getId());
            // 步骤4：递归调用处理该子分类的子分类
            addChildrenIds(child.getId(), categoryIds);
        }
    }
}