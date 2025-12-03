package com.ecommerce.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;

/**
 * "记住我"工具类 - 实现用户自动登录功能
 * 
 * 职责：
 * 1. 生成"记住我"的Cookie令牌
 * 2. 验证Cookie令牌的有效性
 * 3. 支持用户会话过期后自动登录
 * 
 * 工作原理：
 * - 用户勾选"记住我"复选框
 * - 登录成功后生成特殊Cookie
 * - Cookie包含用户名、过期时间和HMAC签名
 * - Cookie设置HttpOnly防止JavaScript访问
 * - 用户下次访问时，验证Cookie有效性进行自动登录
 * 
 * 主要方法：
 * - setRememberMe(): 设置"记住我" Cookie
 * - validateAndGetUsername(): 验证Cookie并获取用户名
 * 
 * Cookie特点：
 * - HttpOnly: 防止JavaScript访问，提高安全性
 * - Path: 应用于整个网站
 * - MaxAge: 默认有效期7天
 * - 包含HMAC签名验证完整性
 * 
 * 安全特点：
 * - Cookie中只存储用户名，不存储密码
 * - 使用HMAC-SHA1对Cookie进行签名
 * - 验证签名防止Cookie被篡改
 * - 使用HttpOnly防止XSS盗取
 * - 支持自定义过期时间
 * 
 * 使用场景：
 * - 用户登录时的"记住我"功能
 * - 支持用户长期自动登录
 * - 增强用户体验
 * 
 * 使用示例：
 * 登录成功：
 * RememberMeUtils.setRememberMe(response, username, 0);
 * 
 * 自动登录：
 * String username = RememberMeUtils.validateAndGetUsername(request);
 * if(username != null) {
 *     User user = userService.findByUsername(username);
 *     session.setAttribute("user", user);
 * }
 */
public class RememberMeUtils {
    private static final String COOKIE_NAME = "rememberMe";
    private static final String SECRET = "ecommerce-secret-key";
    private static final long DEFAULT_EXP_SECONDS = 7L * 24 * 3600;

    /**
     * 设置"记住我"Cookie - 生成并保存自动登录令牌
     * 
     * 实现流程：
     * 1. 计算Cookie过期时间
     * 2. 构建有效载荷（用户名:过期时间）
     * 3. 计算载荷的HMAC签名
     * 4. 组合载荷和签名，使用Base64编码
     * 5. 创建Cookie并设置属性
     * 6. 添加Cookie到响应
     * 
     * @param response HTTP响应对象
     * @param username 用户名
     * @param expSeconds 过期时间（秒），0表示使用默认7天
     */
    public static void setRememberMe(HttpServletResponse response, String username, long expSeconds) {
        // 计算过期时间戳
        long exp = Instant.now().getEpochSecond() + (expSeconds > 0 ? expSeconds : DEFAULT_EXP_SECONDS);
        // 构建载荷：用户名:过期时间
        String payload = username + ":" + exp;
        // 计算签名（防止篡改）
        String sig = hmac(payload);
        // 组合并编码：用户名:时间戳:签名 -> Base64
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString((payload + ":" + sig).getBytes(StandardCharsets.UTF_8));
        // 创建Cookie
        Cookie c = new Cookie(COOKIE_NAME, token);
        // 设置HttpOnly标志防止XSS
        c.setHttpOnly(true);
        // 设置路径
        c.setPath("/");
        // 设置过期时间
        c.setMaxAge((int) (expSeconds > 0 ? expSeconds : DEFAULT_EXP_SECONDS));
        // 添加到响应
        response.addCookie(c);
    }

    /**
     * 验证和获取用户名 - 从Cookie中验证身份
     * 
     * 验证步骤：
     * 1. 获取所有Cookie并查找"rememberMe"
     * 2. Base64解码Cookie值
     * 3. 分解用户名、时间戳和签名
     * 4. 验证时间戳未过期
     * 5. 验证HMAC签名正确
     * 
     * @param request HTTP请求对象
     * @return 用户名或null
     */
    public static String validateAndGetUsername(HttpServletRequest request) {
        // 获取所有Cookie
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        
        // 查找"rememberMe" Cookie
        for (Cookie c : cookies) {
            if (COOKIE_NAME.equals(c.getName())) {
                try {
                    // Base64解码
                    String decoded = new String(Base64.getUrlDecoder().decode(c.getValue()), StandardCharsets.UTF_8);
                    // 分解：用户名:时间戳:签名
                    String[] parts = decoded.split(":");
                    if (parts.length != 3) return null;
                    
                    String username = parts[0];
                    long exp = Long.parseLong(parts[1]);
                    String sig = parts[2];
                    
                    // 验证是否过期
                    if (Instant.now().getEpochSecond() > exp) return null;
                    
                    // 验证签名
                    String expect = hmac(username + ":" + exp);
                    if (!expect.equals(sig)) return null;
                    
                    // 验证成功，返回用户名
                    return username;
                } catch (Exception ignored) { }
            }
        }
        return null;
    }

    /**
     * 清除"记住我"Cookie - 用户登出时清除
     * @param response HTTP响应对象
     */
    public static void clear(HttpServletResponse response) {
        // 创建同名空Cookie，MaxAge=0立即删除
        Cookie c = new Cookie(COOKIE_NAME, "");
        c.setPath("/");
        c.setMaxAge(0);
        response.addCookie(c);
    }

    /**
     * 计算HMAC-SHA256签名 - 防止Cookie被篡改
     * @param payload 要签名的内容
     * @return 16进制签名字符串
     */
    private static String hmac(String payload) {
        try {
            // 使用SHA-256算法
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            // 将载荷和密钥串联后哈希
            md.update((payload + SECRET).getBytes(StandardCharsets.UTF_8));
            byte[] out = md.digest();
            // 转为16进制字符串
            StringBuilder sb = new StringBuilder();
            for (byte b : out) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }
}