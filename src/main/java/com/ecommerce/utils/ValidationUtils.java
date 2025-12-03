package com.ecommerce.utils;

import java.util.regex.Pattern;

/**
 * 输入验证工具类 - 用户输入数据的验证和清理
 * 
 * 职责：
 * 1. 验证用户输入的各类数据
 * 2. 提供输入数据的清理和转义
 * 3. 防止非法输入和XSS攻击
 * 4. 提供常见数据格式的验证
 * 
 * 主要功能：
 * 验证方法：
 * - isValidUsername(): 验证用户名格式（3-20位字母数字下划线）
 * - isValidPassword(): 验证密码格式（6-20位）
 * - isValidEmail(): 验证邮箱格式
 * - isValidPhone(): 验证手机号格式（中国11位手机号）
 * - isValidProductName(): 验证商品名称
 * - isValidPrice(): 验证价格格式
 * - isValidPositiveInteger(): 验证正整数
 * 
 * 清理方法：
 * - sanitizeInput(): 清理输入字符串，防止XSS攻击
 * 
 * 规则说明：
 * - 用户名: 3-20位，只能是字母、数字、下划线
 * - 密码: 6-20位，字母、数字、特殊字符(!@#$%^&*)
 * - 邮箱: 标准邮箱格式
 * - 手机号: 11位，1开头，3-9为第二位
 * - 商品名: 1-100位，支持中文、字母、数字、空格、横线、点、下划线
 * - 价格: 正数，支持小数点
 * - 正整数: 只能是正数字
 * 
 * 特点：
 * - 使用正则表达式进行验证
 * - 支持XSS防护的输入清理
 * - 提供多种常见数据格式的验证
 * - 静态方法易于调用
 * 
 * 使用场景：
 * - 用户注册表单验证
 * - 商品信息验证
 * - 用户输入的安全检查
 * - 表单提交前验证
 * 
 * XSS防护：
 * - 清理HTML标签和特殊字符
 * - 防止JavaScript代码注入
 * - 保留正常的中文和英文字符
 */
public class ValidationUtils {
    
    // 正则表达式模式
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,20}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[a-zA-Z0-9_!@#$%^&*]{6,20}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    private static final Pattern PRODUCT_NAME_PATTERN = Pattern.compile("^[\\u4e00-\\u9fa5a-zA-Z0-9\\s\\-_.]{1,100}$");
    private static final Pattern PRICE_PATTERN = Pattern.compile("^\\d+\\.?\\d{0,2}$");
    private static final Pattern INTEGER_PATTERN = Pattern.compile("^\\d+$");
    
    /**
     * 验证用户名 - 检查用户名是否符合要求
     * 
     * 规则说明：
     * - 长度：3-20位
     * - 字符：只能包含英文字母(a-z, A-Z)、数字(0-9)、下划线(_)
     * - 示例有效的用户名：admin, user_123, a1b2c3
     * - 示例无效的用户名：ab(长度不足), user@name(含特殊字符), 123 (纯数字建议不用)
     * 
     * @param username 要验证的用户名
     * @return true表示用户名格式正确，false表示格式不正确或为null
     */
    public static boolean isValidUsername(String username) {
        return username != null && USERNAME_PATTERN.matcher(username).matches();
    }
    
    /**
     * 验证密码 - 检查密码是否符合要求
     * 
     * 规则说明：
     * - 长度：6-20位
     * - 字符：可包含英文字母、数字、特殊字符(!@#$%^&*)
     * - 示例有效的密码：Pass123, pwd!@#, a1b2c3d4
     * - 示例无效的密码：123(长度不足), password123456789012(过长)
     * 
     * @param password 要验证的密码
     * @return true表示密码格式正确，false表示格式不正确或为null
     */
    public static boolean isValidPassword(String password) {
        return password != null && PASSWORD_PATTERN.matcher(password).matches();
    }
    
    /**
     * 验证邮箱 - 检查邮箱格式是否正确
     * 
     * 规则说明：
     * - 格式：标准邮箱格式(用户名@域名.后缀)
     * - 支持多种域名格式
     * - 示例有效的邮箱：user@example.com, admin@company.co.uk
     * - 示例无效的邮箱：user@example(缺少后缀), @example.com(缺少用户名)
     * 
     * @param email 要验证的邮箱地址
     * @return true表示邮箱格式正确，false表示格式不正确或为null
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * 验证手机号 - 检查手机号是否符合要求
     * 
     * 规则说明：
     * - 格式：中国11位手机号
     * - 首位：1
     * - 第二位：3-9（移动3/8，联通1/5/6/9，电信0/1/7）
     * - 长度：11位数字
     * - 示例有效的号码：13800000000, 15900000000, 18600000000
     * - 示例无效的号码：1234567890(位数不足), 02800000000(非手机号)
     * 
     * @param phone 要验证的手机号
     * @return true表示手机号格式正确，false表示格式不正确或为null
     */
    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }
    
    /**
     * 验证商品名称 - 检查商品名称是否符合要求
     * 
     * 规则说明：
     * - 长度：1-100位
     * - 字符：支持中文、英文字母、数字、空格、横线(-)、点(.)、下划线(_)
     * - 示例有效的名称：iPhone 13 Pro, 华为手机-Mate40, Product_001
     * - 示例无效的名称：空字符串, 超过100个字符的文本
     * 
     * @param productName 要验证的商品名称
     * @return true表示商品名称格式正确，false表示格式不正确或为null
     */
    public static boolean isValidProductName(String productName) {
        return productName != null && PRODUCT_NAME_PATTERN.matcher(productName).matches();
    }
    
    /**
     * 验证价格 - 检查价格格式是否正确
     * 
     * 规则说明：
     * - 格式：正实数
     * - 小数：最多两位小数(支持分)
     * - 示例有效的价格：99, 99.9, 99.99, 0.01
     * - 示例无效的价格：-99(负数), 99.999(超过两位小数), abc(非数字)
     * 
     * @param price 要验证的价格
     * @return true表示价格格式正确，false表示格式不正确或为null
     */
    public static boolean isValidPrice(String price) {
        return price != null && PRICE_PATTERN.matcher(price).matches();
    }
    
    /**
     * 验证正整数 - 检查字符串是否为正整数
     * 
     * 规则说明：
     * - 仅包含数字0-9
     * - 不能为负数或小数
     * - 不能有符号前缀
     * - 示例有效的值：1, 100, 999999
     * - 示例无效的值：-1(负数), 1.5(小数), abc(非数字)
     * 
     * @param value 要验证的数值字符串
     * @return true表示为正整数，false表示不是正整数或为null
     */
    public static boolean isValidPositiveInteger(String value) {
        return value != null && INTEGER_PATTERN.matcher(value).matches();
    }
    
    /**
     * 验证ID是否有效 - 检查ID是否为正整数且大于0
     * 
     * 实现逻辑：
     * 1. 首先检查是否为正整数格式
     * 2. 然后检查转换后的值是否大于0
     * 3. 处理转换异常情况
     * 
     * @param id 要验证的ID
     * @return true表示ID有效（大于0），false表示ID无效或为null
     */
    public static boolean isValidId(String id) {
        // 检查ID是否为null
        if (id == null) return false;
        try {
            // 将字符串转换为整数
            int value = Integer.parseInt(id);
            // 检查值是否大于0
            return value > 0;
        } catch (NumberFormatException e) {
            // 转换异常说明字符串不是有效的数字
            return false;
        }
    }
    
    /**
     * 验证数量 - 检查数量是否为有效的正整数且在合理范围内
     * 
     * 规则说明：
     * - 必须是正整数（不能为零）
     * - 最大限制为1000件
     * - 最小限制为1件
     * - 用于验证购物车商品数量或订单数量
     * 
     * 实现逻辑：
     * 1. 先检查基本的正整数格式
     * 2. 转换为整数
     * 3. 检查是否在1-1000范围内
     * 4. 处理转换异常
     * 
     * @param quantity 要验证的数量
     * @return true表示数量有效，false表示数量无效或为null
     */
    public static boolean isValidQuantity(String quantity) {
        // 先进行基本的正整数验证
        if (!isValidPositiveInteger(quantity)) return false;
        try {
            // 转换为整数
            int value = Integer.parseInt(quantity);
            // 检查是否在有效范围内（1-1000）
            // 小于等于0表示数量无效
            // 大于1000表示超出合理范围
            return value > 0 && value <= 1000;
        } catch (NumberFormatException e) {
            // 转换失败，返回false
            return false;
        }
    }
    
    /**
     * 转义HTML特殊字符 - 防止XSS攻击
     * 
     * 处理的特殊字符：
     * - & → &amp; (连接符)
     * - < → &lt; (小于号)
     * - > → &gt; (大于号)
     * - " → &quot; (双引号)
     * - ' → &#39; (单引号)
     * 
     * 目的：
     * - 防止浏览器执行恶意脚本代码
     * - 允许在HTML中安全地显示用户输入
     * 
     * 使用场景：
     * - 在HTML中显示用户输入的文本
     * - 生成动态HTML内容时转义用户数据
     * 
     * @param input 要转义的输入字符串
     * @return 转义后的安全字符串，或null如果输入为null
     */
    public static String escapeHtml(String input) {
        // 检查输入是否为null
        if (input == null) return null;
        // 进行字符替换，将特殊字符转换为HTML实体编码
        return input.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }
    
    /**
     * 清理用户输入 - 防止SQL注入和其他攻击
     * 
     * 处理的危险字符：
     * - ; (分号 - SQL语句分隔符)
     * - ' (单引号 - SQL字符串定界符)
     * - " (双引号 - SQL字符串定界符)
     * - - (横线 - SQL注释符)
     * - * (星号 - SQL通配符和注释符)
     * - / (斜杠 - SQL注释符)
     * - \ (反斜杠 - 转义字符)
     * 
     * 目的：
     * - 防止SQL注入攻击
     * - 防止恶意代码注入
     * - 保护数据库安全
     * 
     * 使用场景：
     * - 用户表单输入清理
     * - SQL查询参数清理
     * - 数据库操作前的预处理
     * 
     * 实现逻辑：
     * 1. 如果输入为null，直接返回null
     * 2. 使用正则表达式移除所有危险字符
     * 3. 去除前后空白
     * 
     * @param input 要清理的输入字符串
     * @return 清理后的安全字符串，或null如果输入为null
     */
    public static String sanitizeInput(String input) {
        // 检查输入是否为null
        if (input == null) return null;
        // 使用正则表达式[;'\"\\-\\*\\/\\\\]+移除SQL注入相关的危险字符
        // [] - 字符集，匹配其中任意字符
        // ; - 分号
        // ' - 单引号
        // \" - 双引号
        // \\- - 横线
        // \\* - 星号
        // \\/ - 斜杠
        // \\\\ - 反斜杠
        // + - 一个或多个匹配项
        // .trim() - 删除前后空白
        return input.replaceAll("[;'\"\\-\\*\\/\\\\]+", "").trim();
    }
}