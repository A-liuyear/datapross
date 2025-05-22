package com.admin.config.ai;


import com.admin.common.util.AIUtil;
import com.admin.web.data.entity.ImageAnalysisRequest;
import com.admin.web.data.entity.ImageAnalysisResponse;
import com.zhipu.oapi.ClientV4;
import com.zhipu.oapi.Constants;
import com.zhipu.oapi.service.v4.model.ChatCompletionRequest;
import com.zhipu.oapi.service.v4.model.ChatMessage;
import com.zhipu.oapi.service.v4.model.ChatMessageRole;
import com.zhipu.oapi.service.v4.model.ModelApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class ZhiPuAIManager {

    @Autowired
    private ClientV4 clientV4;


    public String doChat(ChatMessage chatMessage) {
        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage prompt = new ChatMessage(ChatMessageRole.SYSTEM.value(), "将我的输入的内容第1部分用英文生成，第2部分用中文生成，切记第2部分用中文生成" +
                "并且不要生成多余内容");
        messages.add(prompt);

        messages.add(chatMessage);
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model("glm-4-flash")//模型
                .stream(Boolean.FALSE)
                .invokeMethod(Constants.invokeMethod)
                .messages(messages)
                .build();
        ModelApiResponse invokeModelApiResp = clientV4.invokeModelApi(chatCompletionRequest);
        String result = invokeModelApiResp.getData().getChoices().get(0).getMessage().getContent().toString();
        log.info("ZhiPuAI Response: {}", result);
        return result;
    }

    public String doChat(ChatMessage chatMessage, Long chartId) {
        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage prompt = new ChatMessage(ChatMessageRole.SYSTEM.value(), AIConstant.SYSTEM_PROMPT_PRO);
        messages.add(prompt);

        messages.add(chatMessage);
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model("glm-4-flash")
                .stream(Boolean.FALSE)
                .invokeMethod(Constants.invokeMethod)
                .messages(messages)
                .build();
        ModelApiResponse invokeModelApiResp = clientV4.invokeModelApi(chatCompletionRequest);
        String result = invokeModelApiResp.getData().getChoices().get(0).getMessage().getContent().toString();
        log.info("ZhiPuAI Response For chartId: {} {}", chartId, result);
        return result;
    }



    public ImageAnalysisResponse analyzeImage(ImageAnalysisRequest request) {
        ImageAnalysisResponse response = new ImageAnalysisResponse();

        try {
            // 1. 准备请求消息
            List<ChatMessage> messages = new ArrayList<>();

            // 系统提示词 - 定义图片分析任务
            ChatMessage systemPrompt = new ChatMessage(
                    ChatMessageRole.SYSTEM.value(),
                            "请理解这个图片内容（人物景色）并用中文生成描述返回结果,切记返回中文放到【【【【【【以及】】】】】】之间"
            );
            messages.add(systemPrompt);

            // 1. 构建用户消息（支持URL/Base64）
            String userMessageContent;
            if (request.getImageUrl() != null) {
                // URL方式
                userMessageContent = String.format(
                        "{\"type\": \"image_url\", \"image_url\": {\"url\": \"%s\"}}",
                        request.getImageUrl()
                );
            } else if (request.getImageBase64() != null) {
                // Base64方式（自动清理数据URI前缀）
                String cleanBase64 = request.getImageBase64();
                userMessageContent = String.format(
                        "{\"type\": \"image_url\", \"image_url\": \"%s\"}",
                        cleanBase64
                );
            } else {
                throw new IllegalArgumentException("必须提供 imageUrl 或 imageBase64");
            }

            // 2. 创建用户消息
            ChatMessage userMessage = new ChatMessage(
                    ChatMessageRole.USER.value(),
                    userMessageContent
            );
            messages.add(userMessage);

            // 3. 调用API
            ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                    .model("glm-4v-plus")  // 使用官方最新多模态模型
                    .stream(false)
                    .messages(messages)
                    .invokeMethod(Constants.invokeMethod)
                    .maxTokens(2000)  // 图片分析建议更多token
                    .build();

            ModelApiResponse apiResponse = clientV4.invokeModelApi(chatCompletionRequest);

            // 4. 解析响应
            String resultJson = apiResponse.getData().getChoices().get(0).getMessage().getContent()!=null?
                    apiResponse.getData().getChoices().get(0).getMessage().getContent().toString():"";
            log.info("智普AI图片分析响应: {}", resultJson);


            // 将JSON响应转换为对象
            String content = AIUtil.extractJsZNCode(resultJson);

            ImageAnalysisResponse analysisResult = new ImageAnalysisResponse();
            analysisResult.setSuccess(true);
            analysisResult.setContent(content);


            return analysisResult;

        } catch (Exception e) {
            log.error("图片分析失败", e);
            ImageAnalysisResponse analysisResult =new ImageAnalysisResponse();
            analysisResult.setSuccess(false);
            analysisResult.setContent(e.getMessage());
            return analysisResult;
        }
    }

    /**
     * 清理Base64数据URI前缀
     * 输入可能格式：
     * - data:image/png;base64,ABC123...
     * - 直接Base64编码字符串
     */
    private String cleanBase64Data(String base64Input) {
        if (base64Input == null) return null;

        // 处理data URI格式
        if (base64Input.startsWith("data:")) {
            int commaIndex = base64Input.indexOf(',');
            if (commaIndex > 0) {
                return base64Input.substring(commaIndex + 1);
            }
        }
        return base64Input;
    }

    /**
     * 验证Base64图片有效性
     */
    private void validateImageBase64(String base64) {
        if (base64 == null || base64.isEmpty()) {
            throw new IllegalArgumentException("Base64数据不能为空");
        }

        // 简单验证Base64格式
        if (!base64.matches("^[A-Za-z0-9+/]+={0,2}$")) {
            throw new IllegalArgumentException("无效的Base64格式");
        }

        // 检查图片大小（假设限制5MB）
        int sizeInBytes = (int) (base64.length() * 0.75); // Base64的近似二进制大小
        if (sizeInBytes > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("图片大小不能超过5MB");
        }
    }
}
