package com.admin.config.ai;

import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversation;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationParam;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationResult;
import com.alibaba.dashscope.common.MultiModalMessage;
import com.alibaba.dashscope.common.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;

@Component
public class ImageCaptionManager {
    @Value("${dashscope.api-key}")
    private String apiKey;

    public String generateCaptionFromImageUrl(String imageUrl) {
        try {
            // 1. 下载图片并转换为 Base64 编码字符串
            byte[] imageData = downloadImageAsBytes(imageUrl);
            String base64Image = Base64.getEncoder().encodeToString(imageData);

            // 2. 构建多模态消息（使用 base64 图片）
            MultiModalConversation conv = new MultiModalConversation();
            MultiModalMessage systemMessage = MultiModalMessage.builder()
                    .role(Role.SYSTEM.getValue())
                    .content(Collections.singletonList(Map.of("text", "你是一个图片分析师")))
                    .build();

            MultiModalMessage userMessage = MultiModalMessage.builder()
                    .role(Role.USER.getValue())
                    .content(Arrays.asList(
                            Map.of("image", "data:image/jpeg;base64," + base64Image),
                            Map.of("text", "图中描绘的是什么景象?")
                    ))
                    .build();

            // 3. 构建请求参数
            MultiModalConversationParam param = MultiModalConversationParam.builder()
                    .apiKey(apiKey)
                    .model("qwen-vl-max-latest")
                    .messages(Arrays.asList(systemMessage, userMessage))
                    .build();

            // 4. 调用模型并返回结果
            MultiModalConversationResult result = conv.call(param);
            return (String) result.getOutput().getChoices().get(0).getMessage().getContent().get(0).get("text");

        } catch (Exception e) {
            throw new RuntimeException("调用失败: " + e.getMessage(), e);
        }
    }

    // 下载图片为 byte[]
    private byte[] downloadImageAsBytes(String imageUrl) throws IOException {
        URL url = new URL(imageUrl);
        BufferedImage image = ImageIO.read(url);

        // 可选：预处理（压缩尺寸）
        int maxDimension = 1024;
        if (image.getWidth() > maxDimension || image.getHeight() > maxDimension) {
            double ratio = Math.min((double) maxDimension / image.getWidth(),
                    (double) maxDimension / image.getHeight());
            int newWidth = (int) (image.getWidth() * ratio);
            int newHeight = (int) (image.getHeight() * ratio);
            BufferedImage resized = new BufferedImage(newWidth, newHeight, image.getType());
            Graphics2D g = resized.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(image, 0, 0, newWidth, newHeight, null);
            g.dispose();
            image = resized;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);
        return baos.toByteArray();
    }
}