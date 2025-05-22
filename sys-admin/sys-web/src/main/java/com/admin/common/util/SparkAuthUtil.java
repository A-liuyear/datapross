package com.admin.common.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Date;

public class SparkAuthUtil {
    
    // 从配置获取
    private final String apiKey;
    private final String apiSecret;
    
    public SparkAuthUtil(String apiKey, String apiSecret) {
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
    }
    
    /**
     * 生成星火API认证token
     */
    public String generateToken() throws Exception {
        // 1. 生成RFC1123格式的时间戳
        String date = DateTimeFormatter.RFC_1123_DATE_TIME
            .format(ZonedDateTime.now(ZoneId.of("GMT")));
        
        // 2. 构建待签名字符串
        String signatureStr = String.format(
            "host: %s\n" +
            "date: %s\n" +
            "GET %s HTTP/1.1",
            "spark-api.xf-yun.com",  // 主机名
            date,                   // 时间戳
            "/v2.1/image"           // 请求路径
        );
        
        // 3. 使用HMAC-SHA256加密
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec spec = new SecretKeySpec(
            apiSecret.getBytes(StandardCharsets.UTF_8),
            "HmacSHA256"
        );
        mac.init(spec);
        byte[] signatureBytes = mac.doFinal(signatureStr.getBytes(StandardCharsets.UTF_8));
        
        // 4. Base64编码签名
        String signature = Base64.getEncoder().encodeToString(signatureBytes);
        
        // 5. 构建authorization字符串
        String authorization = String.format(
            "api_key=\"%s\", algorithm=\"%s\", headers=\"%s\", signature=\"%s\"",
            apiKey,
            "hmac-sha256",
            "host date request-line",
            signature
        );
        
        // 6. Base64编码authorization
        return Base64.getEncoder().encodeToString(authorization.getBytes(StandardCharsets.UTF_8));
    }
}