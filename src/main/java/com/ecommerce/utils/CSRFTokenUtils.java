package com.ecommerce.utils;

import javax.servlet.http.HttpSession;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * CSRF令牌工具类
 */
public class CSRFTokenUtils {
    
    private static final String CSRF_TOKEN_SESSION_KEY = "csrfToken";
    private static final SecureRandom secureRandom = new SecureRandom();
    
    /**
     * 生成CSRF令牌
     */
    public static String generateCSRFToken(HttpSession session) {
        byte[] tokenBytes = new byte[32];
        secureRandom.nextBytes(tokenBytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
        session.setAttribute(CSRF_TOKEN_SESSION_KEY, token);
        return token;
    }
    
    /**
     * 获取CSRF令牌
     */
    public static String getCSRFToken(HttpSession session) {
        String token = (String) session.getAttribute(CSRF_TOKEN_SESSION_KEY);
        if (token == null) {
            token = generateCSRFToken(session);
        }
        return token;
    }
    
    /**
     * 验证CSRF令牌
     */
    public static boolean validateCSRFToken(HttpSession session, String token) {
        if (token == null || session == null) {
            return false;
        }
        String sessionToken = (String) session.getAttribute(CSRF_TOKEN_SESSION_KEY);
        return token.equals(sessionToken);
    }
    
    /**
     * 移除CSRF令牌
     */
    public static void removeCSRFToken(HttpSession session) {
        if (session != null) {
            session.removeAttribute(CSRF_TOKEN_SESSION_KEY);
        }
    }
}