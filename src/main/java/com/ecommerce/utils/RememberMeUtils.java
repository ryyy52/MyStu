package com.ecommerce.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;

public class RememberMeUtils {
    private static final String COOKIE_NAME = "rememberMe";
    private static final String SECRET = "ecommerce-secret-key";
    private static final long DEFAULT_EXP_SECONDS = 7L * 24 * 3600;

    public static void setRememberMe(HttpServletResponse response, String username, long expSeconds) {
        long exp = Instant.now().getEpochSecond() + (expSeconds > 0 ? expSeconds : DEFAULT_EXP_SECONDS);
        String payload = username + ":" + exp;
        String sig = hmac(payload);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString((payload + ":" + sig).getBytes(StandardCharsets.UTF_8));
        Cookie c = new Cookie(COOKIE_NAME, token);
        c.setHttpOnly(true);
        c.setPath("/");
        c.setMaxAge((int) (expSeconds > 0 ? expSeconds : DEFAULT_EXP_SECONDS));
        response.addCookie(c);
    }

    public static String validateAndGetUsername(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        for (Cookie c : cookies) {
            if (COOKIE_NAME.equals(c.getName())) {
                try {
                    String decoded = new String(Base64.getUrlDecoder().decode(c.getValue()), StandardCharsets.UTF_8);
                    String[] parts = decoded.split(":");
                    if (parts.length != 3) return null;
                    String username = parts[0];
                    long exp = Long.parseLong(parts[1]);
                    String sig = parts[2];
                    if (Instant.now().getEpochSecond() > exp) return null;
                    String expect = hmac(username + ":" + exp);
                    if (!expect.equals(sig)) return null;
                    return username;
                } catch (Exception ignored) { }
            }
        }
        return null;
    }

    public static void clear(HttpServletResponse response) {
        Cookie c = new Cookie(COOKIE_NAME, "");
        c.setPath("/");
        c.setMaxAge(0);
        response.addCookie(c);
    }

    private static String hmac(String payload) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update((payload + SECRET).getBytes(StandardCharsets.UTF_8));
            byte[] out = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : out) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }
}