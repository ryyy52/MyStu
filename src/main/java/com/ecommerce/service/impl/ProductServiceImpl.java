package com.ecommerce.service.impl;

import com.ecommerce.dao.ProductDao;
import com.ecommerce.dao.impl.ProductDaoImpl;
import com.ecommerce.pojo.Product;
import com.ecommerce.service.ProductService;

import java.util.List;

/**
 * 商品业务逻辑实现类 - 实现ProductService接口的具体业务逻辑
 * 
 * 功能说明：
 * 1. 商品查询：按ID、分类、关键词等条件查询商品
 * 2. 商品新增：添加新商品，包含默认值设置
 * 3. 商品更新：修改商品信息
 * 4. 库存管理：更新商品库存数量
 * 5. 商品删除：删除商品记录
 * 6. 分页查询：支持各种条件下的分页查询
 * 7. 数量统计：支持各种条件下的商品总数统计
 * 
 * 核心业务规则：
 * - 新商品默认状态为1（上架）
 * - 新商品默认库存为0
 * - 分页计算使用Math.max防止负数offset
 * - 支持多分类ID查询（用于显示多个分类的商品）
 */
public class ProductServiceImpl implements ProductService {
    // 注入ProductDao依赖 - 用于访问商品数据库
    private ProductDao productDao = new ProductDaoImpl();

    /**
     * 根据商品ID查询单个商品详情
     * @param id 商品ID
     * @return 商品对象，不存在则返回null
     */
    @Override
    public Product findById(Integer id) {
        // 直接调用DAO层查询，返回指定ID的商品
        return productDao.findById(id);
    }

    /**
     * 查询所有商品
     * @return 商品列表，如果没有商品则返回空列表
     */
    @Override
    public List<Product> findAll() {
        // 直接调用DAO层查询所有商品
        return productDao.findAll();
    }

    /**
     * 根据分类ID查询该分类下的所有商品
     * @param categoryId 分类ID
     * @return 该分类下的商品列表
     */
    @Override
    public List<Product> findByCategoryId(Integer categoryId) {
        // 调用DAO层按分类ID查询商品
        return productDao.findByCategoryId(categoryId);
    }

    /**
     * 按关键词搜索商品
     * @param keyword 搜索关键词
     * @return 匹配关键词的商品列表
     */
    @Override
    public List<Product> search(String keyword) {
        // 调用DAO层搜索商品（通常在商品名称和描述中搜索）
        return productDao.search(keyword);
    }

    /**
     * 新增商品业务逻辑
     * @param product 商品对象（包含商品基本信息）
     * @return true表示新增成功，false表示新增失败
     */
    @Override
    public boolean save(Product product) {
        // 调试输出：开始执行save方法
        System.out.println("=== ProductServiceImpl.save() 开始执行 ===");
        // 调试输出：商品名称
        System.out.println("传入的商品对象：");
        System.out.println("商品名称: " + product.getName());
        // 调试输出：所属分类
        System.out.println("分类ID: " + product.getCategoryId());
        // 调试输出：商品价格
        System.out.println("价格: " + product.getPrice());
        // 调试输出：库存数量
        System.out.println("库存: " + product.getStock());
        // 调试输出：商品图片URL
        System.out.println("图片URL: " + product.getImage());
        // 调试输出：商品描述
        System.out.println("描述: " + product.getDescription());
        // 调试输出：商品当前状态
        System.out.println("状态: " + product.getStatus());
        
        // 步骤1：设置商品默认状态为1（表示上架/发布状态）
        product.setStatus(1);
        System.out.println("设置默认状态为1（上架）");
        
        // 步骤2：检查库存是否为null或未设置，如果没有设置则使用默认值0
        if (product.getStock() == null) {
            // 设置默认库存为0，表示新商品暂无库存
            product.setStock(0);
            System.out.println("设置默认库存为0");
        }
        
        // 步骤3：调用DAO层保存商品到数据库
        System.out.println("调用productDao.save()");
        int result = productDao.save(product);
        // 步骤4：输出DAO层返回的影响行数
        System.out.println("productDao.save()返回结果: " + result);
        
        // 步骤5：判断保存是否成功（result>0表示至少有一行受影响）
        boolean success = result > 0;
        System.out.println("ProductServiceImpl.save()返回结果: " + success);
        System.out.println("=== ProductServiceImpl.save() 执行结束 ===");
        // 步骤6：返回保存结果
        return success;
    }

    /**
     * 更新商品信息业务逻辑
     * @param product 包含更新信息的商品对象（必须含有ID）
     * @return true表示更新成功，false表示更新失败
     */
    @Override
    public boolean update(Product product) {
        // 调用DAO层更新商品信息
        int result = productDao.update(product);
        // 根据数据库操作结果返回成功/失败标识
        return result > 0;
    }

    /**
     * 更新商品库存业务逻辑
     * @param productId 商品ID
     * @param quantity 库存数量变化（可正可负，正数为增加，负数为减少）
     * @return true表示更新成功，false表示更新失败
     */
    @Override
    public boolean updateStock(Integer productId, Integer quantity) {
        // 调用DAO层更新库存
        int result = productDao.updateStock(productId, quantity);
        // 根据数据库操作结果返回成功/失败标识
        return result > 0;
    }

    /**
     * 删除商品业务逻辑
     * @param id 商品ID
     * @return true表示删除成功，false表示删除失败
     */
    @Override
    public boolean delete(Integer id) {
        // 调用DAO层删除商品
        int result = productDao.delete(id);
        // 根据数据库操作结果返回成功/失败标识（result>0表示至少删除了一行）
        return result > 0;
    }

    /**
     * 分页查询所有商品业务逻辑
     * 
     * 实现原理：
     * - 将页码和每页数量转换为数据库查询的offset和limit
     * - 使用Math.max(0, ...)防止负数offset导致的数据库异常
     * 
     * @param page 页码（从1开始），示例：page=1表示第一页
     * @param pageSize 每页记录数，示例：pageSize=10表示每页10条记录
     * @return 指定页码的商品列表
     */
    @Override
    public List<Product> findByPage(int page, int pageSize) {
        // 步骤1：计算数据库中的offset值
        // offset = (page - 1) * pageSize
        // 例：page=2, pageSize=10 => offset=10（表示跳过前10条）
        int offset = Math.max(0, (page - 1) * pageSize);
        // 步骤2：使用Math.max(0, ...)确保offset不为负数，防止SQL异常
        // 步骤3：调用DAO层执行分页查询
        return productDao.findByPage(offset, pageSize);
    }

    /**
     * 根据分类ID分页查询商品业务逻辑
     * 
     * @param categoryId 分类ID
     * @param page 页码（从1开始）
     * @param pageSize 每页记录数
     * @return 指定分类在指定页码的商品列表
     */
    @Override
    public List<Product> findByCategoryIdAndPage(Integer categoryId, int page, int pageSize) {
        // 计算offset：将页码转换为数据库查询所需的偏移量
        int offset = Math.max(0, (page - 1) * pageSize);
        // 使用Math.max防止offset为负数
        // 调用DAO层执行分类下的分页查询
        return productDao.findByCategoryIdAndPage(categoryId, offset, pageSize);
    }

    /**
     * 按关键词搜索商品（分页）业务逻辑
     * 
     * @param keyword 搜索关键词
     * @param page 页码（从1开始）
     * @param pageSize 每页记录数
     * @return 匹配关键词在指定页码的商品列表
     */
    @Override
    public List<Product> searchByPage(String keyword, int page, int pageSize) {
        // 计算offset：将页码转换为数据库查询所需的偏移量
        int offset = Math.max(0, (page - 1) * pageSize);
        // 使用Math.max防止offset为负数
        // 调用DAO层执行关键词搜索分页查询
        return productDao.searchByPage(keyword, offset, pageSize);
    }

    /**
     * 获取所有商品的总数业务逻辑
     * 
     * 用途：用于计算总页数
     * 总页数 = (总数 + pageSize - 1) / pageSize
     * 
     * @return 所有商品的总数
     */
    @Override
    public int countAll() {
        // 调用DAO层执行COUNT查询，获得商品总数
        return productDao.countAll();
    }

    /**
     * 根据分类ID获取商品总数业务逻辑
     * 
     * @param categoryId 分类ID
     * @return 该分类下的商品总数
     */
    @Override
    public int countByCategoryId(Integer categoryId) {
        // 调用DAO层执行分类COUNT查询
        return productDao.countByCategoryId(categoryId);
    }

    /**
     * 根据多个分类ID查询商品业务逻辑
     * 
     * 用途：在主页或分类导航中显示多个分类的商品
     * 
     * @param categoryIds 分类ID列表
     * @return 这些分类下的所有商品列表
     */
    @Override
    public List<Product> findByCategoryIds(List<Integer> categoryIds) {
        // 调用DAO层执行多分类IN查询
        return productDao.findByCategoryIds(categoryIds);
    }

    /**
     * 根据多个分类ID分页查询商品业务逻辑
     * 
     * @param categoryIds 分类ID列表
     * @param page 页码（从1开始）
     * @param pageSize 每页记录数
     * @return 这些分类下在指定页码的商品列表
     */
    @Override
    public List<Product> findByCategoryIdsAndPage(List<Integer> categoryIds, int page, int pageSize) {
        // 计算offset：将页码转换为数据库查询所需的偏移量
        int offset = Math.max(0, (page - 1) * pageSize);
        // 使用Math.max防止offset为负数
        // 调用DAO层执行多分类下的分页查询
        return productDao.findByCategoryIdsAndPage(categoryIds, offset, pageSize);
    }

    /**
     * 根据多个分类ID获取商品总数业务逻辑
     * 
     * @param categoryIds 分类ID列表
     * @return 这些分类下的商品总数
     */
    @Override
    public int countByCategoryIds(List<Integer> categoryIds) {
        // 调用DAO层执行多分类COUNT查询
        return productDao.countByCategoryIds(categoryIds);
    }

    /**
     * 获取搜索结果的商品总数业务逻辑
     * 
     * @param keyword 搜索关键词
     * @return 匹配关键词的商品总数
     */
    @Override
    public int countSearchResults(String keyword) {
        // 调用DAO层执行搜索COUNT查询
        return productDao.countSearchResults(keyword);
    }
}