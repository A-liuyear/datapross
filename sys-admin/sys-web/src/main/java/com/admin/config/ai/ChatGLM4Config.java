package com.admin.config.ai;

import com.zhipu.oapi.ClientV4;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatGLM4Config {
    @Value("${zhipu.key}")
    private String key;
    @Bean
    public ClientV4 clientV4() {
        return new ClientV4.Builder(key).build();
    }
}
