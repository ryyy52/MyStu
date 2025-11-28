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
 * 商品分类业务逻辑实现类
 */
public class CategoryServiceImpl implements CategoryService {
    private CategoryDao categoryDao = new CategoryDaoImpl();
    private ProductDao productDao = new ProductDaoImpl();

    @Override
    public Category findById(Integer id) {
        return categoryDao.findById(id);
    }

    @Override
    public List<Category> findAll() {
        return categoryDao.findAll();
    }

    @Override
    public List<Category> findByParentId(Integer parentId) {
        return categoryDao.findByParentId(parentId);
    }

    @Override
    public List<Category> getCategoryTree() {
        // 获取所有分类
        List<Category> allCategories = categoryDao.findAll();
        // 构建分类树
        return buildCategoryTree(allCategories, 0); // 0表示顶级分类
    }

    /**
     * 递归构建分类树
     * @param allCategories 所有分类
     * @param parentId 父分类ID
     * @return 分类树列表
     */
    private List<Category> buildCategoryTree(List<Category> allCategories, Integer parentId) {
        List<Category> children = new ArrayList<>();
        for (Category category : allCategories) {
            Integer pid = category.getParentId();
            if (pid == null) pid = 0;
            if (pid.equals(parentId)) {
                // 递归查找子分类
                category.setChildren(buildCategoryTree(allCategories, category.getId()));
                children.add(category);
            }
        }
        return children;
    }

    @Override
    public boolean save(Category category) {
        int result = categoryDao.save(category);
        return result > 0;
    }

    @Override
    public boolean update(Category category) {
        int result = categoryDao.update(category);
        return result > 0;
    }

    @Override
    public boolean delete(Integer id) {
        // 检查是否有子分类
        List<Category> children = categoryDao.findByParentId(id);
        if (!children.isEmpty()) {
            return false; // 有子分类，不能删除
        }
        // 检查是否有商品属于该分类
        int productCount = productDao.countByCategoryId(id);
        if (productCount > 0) {
            return false; // 仍有关联商品，不能删除
        }
        int result = categoryDao.delete(id);
        return result > 0;
    }
    
    @Override
    public List<Integer> getCategoryIdsWithChildren(Integer categoryId) {
        List<Integer> categoryIds = new ArrayList<>();
        // 添加当前分类ID
        categoryIds.add(categoryId);
        // 递归添加所有子分类ID
        addChildrenIds(categoryId, categoryIds);
        return categoryIds;
    }
    
    /**
     * 递归添加所有子分类ID
     * @param parentId 父分类ID
     * @param categoryIds 分类ID列表
     */
    private void addChildrenIds(Integer parentId, List<Integer> categoryIds) {
        List<Category> children = categoryDao.findByParentId(parentId);
        for (Category child : children) {
            categoryIds.add(child.getId());
            // 递归添加子分类的子分类
            addChildrenIds(child.getId(), categoryIds);
        }
    }
}