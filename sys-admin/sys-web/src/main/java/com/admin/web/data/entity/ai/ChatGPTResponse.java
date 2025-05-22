package com.admin.web.data.entity.ai;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class ChatGPTResponse {

    private String content;

    private int totalTokens;
}
