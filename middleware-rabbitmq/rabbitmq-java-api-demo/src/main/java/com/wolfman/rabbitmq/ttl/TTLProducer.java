package com.wolfman.rabbitmq.ttl;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.wolfman.rabbitmq.ConnectionUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * TTL(Time To Live)
 * 消息失效时间
 */
public class TTLProducer {

    public static void main(String[] args) throws Exception {
        // 建立连接
        Connection conn = ConnectionUtils.getConnection();

        // 创建消息通道
        Channel channel = conn.createChannel();
        String msg = "Hello world, Rabbit MQ, DLX MSG";

        // 通过队列属性设置消息过期时间
        Map<String, Object> argss = new HashMap<String, Object>();
        argss.put("x-message-ttl",6000);

        // 声明队列（默认交换机AMQP default，Direct）
        // String queue, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments
        channel.queueDeclare("ttl_queue", false, false, false, argss);

        // 对每条消息设置过期时间
        AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
                .deliveryMode(2) // 持久化消息
                .contentEncoding("UTF-8")
                .expiration("10000") // TTL
                .build();

        // 此处两种方式设置消息过期时间的方式都使用了，将以较小的数值为准

        // 发送消息
        channel.basicPublish("simple_exchange", "ttl.first", properties, msg.getBytes());

        channel.close();
        conn.close();
    }

}

