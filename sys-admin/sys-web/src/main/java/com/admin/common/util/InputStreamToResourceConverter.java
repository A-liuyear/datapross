package com.admin.common.util;

import org.springframework.core.io.ByteArrayResource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class InputStreamToResourceConverter {

    /**
     * 将 InputStream 转换为 ByteArrayResource
     * @param inputStream 输入流
     * @return ByteArrayResource
     * @throws IOException 如果读取流失败
     */
    public static ByteArrayResource convert(InputStream inputStream) throws IOException {
        // 1. 将 InputStream 读取到 byte[]
        byte[] bytes = toByteArray(inputStream);
        
        // 2. 创建 ByteArrayResource
        return new ByteArrayResource(bytes) {
            @Override
            public String getFilename() {
                return "generated-file"; // 可以重写此方法提供文件名
            }
        };
    }

    /**
     * 将 InputStream 转换为 byte[]
     */
    private static byte[] toByteArray(InputStream input) throws IOException {
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = input.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            return output.toByteArray();
        }
    }
}