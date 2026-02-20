package cn.kmbeast.utils;

import io.jsonwebtoken.*;

import java.util.Date;
import java.util.UUID;

/**
 * JWT Token工具类
 * 功能：生成和解析JWT Token，用于用户身份认证
 */
public class JwtUtil {
    /**
     * 密钥（用于签名和验证）
     */
    private static final String privateKey = "d8c986df-8512-42b5-906f-eeea9b3acf86";
    /**
     * Token有效期：一周（毫秒）
     */
    private static final Integer time = 1000 * 60 * 60 * 24 * 7;

    /**
     * 生成JWT Token
     * 包含用户ID和角色信息，有效期一周
     *
     * @param id   用户ID
     * @param role 用户角色（1=管理员，2=普通用户）
     * @return JWT Token字符串
     */
    public static String toToken(Integer id, Integer role) {
        JwtBuilder jwtBuilder = Jwts.builder();
        return jwtBuilder.setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS256")
                .claim("id", id)
                .claim("role", role)
                .setSubject("用户认证")
                .setExpiration(new Date(System.currentTimeMillis() + time))
                .setId(UUID.randomUUID().toString())
                .signWith(SignatureAlgorithm.HS256, privateKey)
                .compact();
    }

    /**
     * 解析JWT Token
     * 提取Token中的用户信息
     *
     * @param token JWT Token字符串
     * @return Claims对象（包含用户ID和角色），解析失败返回null
     */
    public static Claims fromToken(String token) {
        JwtParser jwtParser = Jwts.parser();
        Jws<Claims> claimsJws;
        try {
            claimsJws = jwtParser.setSigningKey(privateKey).parseClaimsJws(token);
            return claimsJws.getBody();
        } catch (Exception e) {
            return null;
        }
    }

}
