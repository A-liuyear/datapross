package com.admin.config.rabbitmq;

import com.admin.web.data.entity.GenChartByAIRequest;
import com.admin.web.data.entity.ImageAnalysisRequest;
import com.admin.web.data.entity.ai.BIResponse;
import com.admin.web.data.service.ChartService;
import com.alibaba.fastjson.JSON;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQConsumer {

    @Autowired
    private ChartService chartService;
    /**
     * 监听队列消息
     * @param message 接收到的消息
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME_DATA, ackMode = "AUTO")
    public void receiveMessage(String message) throws Exception {
        System.out.println("接收到消息: " + message);
        GenChartByAIRequest genChartByAIRequest = JSON.parseObject(message, GenChartByAIRequest.class);
        try{
            chartService.genChartByAI(genChartByAIRequest);
        }catch (Exception e){

        }

    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME_IMG, ackMode = "AUTO")
    public void receiveMessageImg(String message) throws Exception {
        System.out.println("接收到消息: " + message);
        ImageAnalysisRequest genChartByAIRequest = JSON.parseObject(message, ImageAnalysisRequest.class);
        try{
            chartService.analyzeImage(genChartByAIRequest);
        }catch (Exception e){

        }

    }
}