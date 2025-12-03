package com.ecommerce.utils;

import javax.servlet.http.HttpSession;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * CSRF令牌工具类 - 防止跨站请求伪造（CSRF）攻击
 * 
 * 职责：
 * 1. 生成安全的CSRF令牌
 * 2. 验证提交的CSRF令牌
 * 3. 管理令牌的存储和验证
 * 
 * CSRF攻击原理：
 * - 攻击者诱导用户访问恶意网站
 * - 恶意网站发送请求到目标网站
 * - 由于用户已登录，浏览器自动发送Cookie
 * - 目标网站无法区分请求来源，执行恶意操作
 * 
 * 防护机制：
 * - 在表单中添加隐藏的令牌字段
 * - 每个用户会话生成唯一令牌
 * - 提交时验证令牌与服务端存储的令牌是否一致
 * - 恶意网站无法获得令牌，无法进行CSRF攻击
 * 
 * 主要方法：
 * - generateCSRFToken(HttpSession): 生成新的CSRF令牌
 * - getCSRFToken(HttpSession): 获取已生成的令牌
 * - validateCSRFToken(HttpSession, String): 验证令牌有效性
 * 
 * 特点：
 * - 使用SecureRandom生成随机令牌
 * - 使用Base64编码转换为字符串
 * - 使用HttpSession存储令牌
 * - 令牌长度32字节，安全性高
 * 
 * 使用场景：
 * - 用户注册表单
 * - 用户登录表单
 * - 用户信息修改表单
 * - 涉及数据变更的所有表单
 * 
 * 使用示例：
 * JSP页面：
 * <input type="hidden" name="csrfToken" value="${csrfToken}" />
 * 
 * Java代码：
 * boolean valid = CSRFTokenUtils.validateCSRFToken(session, request.getParameter("csrfToken"));
 */
public class CSRFTokenUtils {
    
    private static final String CSRF_TOKEN_SESSION_KEY = "csrfToken";
    private static final SecureRandom secureRandom = new SecureRandom();
    
    /**
     * 生成CSRF令牌 - 为当前会话生成新的安全令牌
     * 
     * 实现步骤：
     * 1. 创建32字节的字节数组
     * 2. 使用SecureRandom填充随机数据
     * 3. 使用Base64编码转换为字符串
     * 4. 将令牌保存到Session中
     * 5. 返回生成的令牌字符串
     * 
     * 安全特性：
     * - 使用SecureRandom确保随机性
     * - 32字节长度（256位）提供高安全性
     * - Base64编码便于在HTTP请求中传输
     * - 存储在服务端Session中
     * 
     * @param session 用户的HTTP会话
     * @return 生成的32字节Base64编码的随机令牌
     */
    public static String generateCSRFToken(HttpSession session) {
        // 步骤1：创建32字节的字节数组
        byte[] tokenBytes = new byte[32];
        // 步骤2：使用SecureRandom生成随机数据
        // SecureRandom是密码级别的随机数生成器，比普通Random更安全
        secureRandom.nextBytes(tokenBytes);
        // 步骤3：使用Base64编码将字节数组转换为字符串
        // withoutPadding()：不使用=符号填充，使令牌更紧凑
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
        // 步骤4：将令牌保存到Session中，用于后续验证
        session.setAttribute(CSRF_TOKEN_SESSION_KEY, token);
        // 步骤5：返回生成的令牌
        return token;
    }
    
    /**
     * 获取CSRF令牌 - 获取当前会话的令牌，如不存在则自动生成
     * 
     * 工作流程：
     * 1. 从Session中获取已存储的令牌
     * 2. 如果令牌不存在，自动生成新的令牌
     * 3. 返回令牌
     * 
     * 使用场景：
     * - 在渲染表单时调用此方法获取令牌
     * - 第一次访问表单时会自动生成新令牌
     * - 后续访问时返回相同的令牌
     * 
     * @param session 用户的HTTP会话
     * @return 当前会话的CSRF令牌（如不存在则自动生成）
     */
    public static String getCSRFToken(HttpSession session) {
        // 从Session中获取之前生成的令牌
        String token = (String) session.getAttribute(CSRF_TOKEN_SESSION_KEY);
        // 如果令牌不存在，自动生成新的令牌
        if (token == null) {
            token = generateCSRFToken(session);
        }
        // 返回令牌（无论是存在的还是新生成的）
        return token;
    }
    
    /**
     * 验证CSRF令牌 - 验证提交的令牌是否与服务端存储的令牌一致
     * 
     * 验证流程：
     * 1. 检查提交的令牌和Session是否有效
     * 2. 从Session中获取存储的令牌
     * 3. 比较两个令牌是否相等
     * 4. 返回验证结果
     * 
     * 安全保证：
     * - 只有掌握有效令牌的请求才能通过验证
     * - 来自其他域的请求无法获得令牌，无法伪造
     * - 提供了防CSRF攻击的关键防护
     * 
     * @param session 用户的HTTP会话
     * @param token 要验证的令牌（从表单提交获得）
     * @return true表示令牌有效，false表示令牌无效或为null
     */
    public static boolean validateCSRFToken(HttpSession session, String token) {
        // 检查提交的令牌和Session是否为null
        if (token == null || session == null) {
            // 任何一个为null都验证失败
            return false;
        }
        // 从Session中获取存储的令牌
        String sessionToken = (String) session.getAttribute(CSRF_TOKEN_SESSION_KEY);
        // 比较两个令牌是否相等
        // equals()方法进行逐字符比较，时间复杂度O(n)
        // 返回true仅当两个字符串完全相同
        return token.equals(sessionToken);
    }
    
    /**
     * 移除CSRF令牌 - 从会话中删除令牌
     * 
     * 使用场景：
     * - 用户登出时清除令牌
     * - 防止令牌被重复使用
     * - 提高安全性
     * 
     * @param session 用户的HTTP会话
     */
    public static void removeCSRFToken(HttpSession session) {
        // 检查Session是否有效
        if (session != null) {
            // 从Session中移除令牌
            session.removeAttribute(CSRF_TOKEN_SESSION_KEY);
        }
    }
}