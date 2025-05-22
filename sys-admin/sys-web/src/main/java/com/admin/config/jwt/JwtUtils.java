package com.admin.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

@Component
@Data
@ConfigurationProperties(prefix = "jwt")
public class JwtUtils {
    private String issuer;
    private String secret;
    private int expiration;

    /**
     * 创建token
     * @param claimMap
     * @return
     */
    public  String createToken(Map<String, String> claimMap) {
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.MINUTE, expiration);
        JWTCreator.Builder builder= JWT.create();
        claimMap.forEach((k,v)->{
            builder.withClaim(k,v);
        });
        String token=builder.withIssuer(issuer).withIssuedAt(new Date()).withExpiresAt(instance.getTime())
                .sign(Algorithm.HMAC256(secret));
        return token;
    }

    /**
     * 验证token
     * @param token
     * @return
     */
    public DecodedJWT jwtDecode(String token) {
       return JWT.require(Algorithm.HMAC256(secret)).build().verify(token);
    }

    public boolean verifyToken(String token) {
        try{
            JWT.require(Algorithm.HMAC256(secret)).build().verify(token);
        }catch (Exception e){
            return false;
        }
        return true;
    }


}
