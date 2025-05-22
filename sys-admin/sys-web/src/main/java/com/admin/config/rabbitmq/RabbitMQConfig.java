package com.admin.config.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitMQConfig {

    // 定义队列
    public static final String QUEUE_NAME_DATA = "springboot.queue.data";
    public static final String TOPIC_EXCHANGE_NAME_DATA = "springboot.topic.exchange.data";
    public static final String ROUTING_KEY_DATA = "springboot.routing.data";


    // 定义队列
    public static final String QUEUE_NAME_IMG = "springboot.queue.img";
    public static final String TOPIC_EXCHANGE_NAME_IMG = "springboot.topic.exchange.img";
    public static final String ROUTING_KEY_IMG = "springboot.routing.img";
    // 创建队列
    @Bean
    public Queue queue() {
        return new Queue(QUEUE_NAME_DATA, true); // true表示持久化
    }

    // 创建Topic交换机
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(TOPIC_EXCHANGE_NAME_DATA);
    }

    // 绑定队列和交换机
    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY_DATA);
    }



    // 创建队列
    @Bean
    public Queue queueImg() {
        return new Queue(QUEUE_NAME_IMG, true); // true表示持久化
    }

    // 创建Topic交换机
    @Bean
    public TopicExchange exchangeImg() {
        return new TopicExchange(TOPIC_EXCHANGE_NAME_IMG);
    }

    // 绑定队列和交换机
    @Bean
    public Binding bindingImg(Queue queueImg, TopicExchange exchangeImg) {
        return BindingBuilder.bind(queueImg).to(exchangeImg).with(ROUTING_KEY_IMG);
    }

}