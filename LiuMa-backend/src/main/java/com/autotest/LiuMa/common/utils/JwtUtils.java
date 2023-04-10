package com.autotest.LiuMa.common.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.autotest.LiuMa.database.domain.Engine;
import com.autotest.LiuMa.database.domain.User;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtils {
    // 验签工具

    private static final String SECRET = "LiuMaTest_Secret"; // 加密秘钥

    private static final long WEB_EXPIRATION = 7200L;//平台token过期时间 2小时

    private static final long ENGINE_EXPIRATION = 604800L;//引擎token过期时间 7天

    public static String createWebToken(User user) {
        // 生成平台token
        Date expireDate = new Date(System.currentTimeMillis() + WEB_EXPIRATION*1000); //过期时间
        Map<String, Object> map = new HashMap<>();
        map.put("alg", "HS256");
        map.put("typ", "JWT");
        return JWT.create()
                .withHeader(map)// 添加头部
                //可以将基本信息放到claims中
                .withClaim("id", user.getId()) //id
                .withClaim("username", user.getUsername())//username
                .withClaim("account", user.getAccount()) //account
                .withClaim("password", user.getPassword())//password
                .withExpiresAt(expireDate) //超时设置,设置过期的日期
                .withIssuedAt(new Date()) //签发时间
                .sign(Algorithm.HMAC256(SECRET));
    }

    public static String createEngineToken(Engine engine) {
        // 生成引擎token
        Date expireDate = new Date(System.currentTimeMillis() + ENGINE_EXPIRATION*1000); //过期时间
        Map<String, Object> map = new HashMap<>();
        map.put("alg", "HS256");
        map.put("typ", "JWT");
        String token = JWT.create()
                .withHeader(map)// 添加头部
                //可以将基本信息放到claims中
                .withClaim("engineId", engine.getId())//engineId
                .withClaim("engineSecret", engine.getSecret())//engineSecret
                .withExpiresAt(expireDate) //超时设置,设置过期的日期
                .withIssuedAt(new Date()) //签发时间
                .sign(Algorithm.HMAC256(SECRET)); //SECRET加密
        return token;
    }

    /**
     * 校验token并解析token
     */
    public static DecodedJWT verifyToken(String token) {
        DecodedJWT jwt = null;
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET)).build();
            jwt = verifier.verify(token);
        } catch (TokenExpiredException e) {
            // 时间校验出错抛出过期
            throw new TokenExpiredException("token已过期");
        }catch (Exception e) {
            // 解码异常抛出校验出错
            throw new JWTVerificationException("token校验出错");
        }
        // 返回基本信息
        return jwt;
    }
}
