package com.wolfman.rabbitmq.ttl;

import com.rabbitmq.client.*;
import com.wolfman.rabbitmq.ConnectionUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class TTLConsumer {

  public static void main(String[] args) throws IOException, TimeoutException {
    //建立连接
    Connection conn = ConnectionUtils.getConnection();

    //创建消息通道
    Channel channel = conn.createChannel();

    // 声明交换机
    // String exchange, String type, boolean durable, boolean autoDelete, Map<String, Object> arguments
    channel.exchangeDeclare("simple_exchange","direct",false, false, null);

    // 通过队列属性设置消息过期时间
    Map<String, Object> argss = new HashMap<String, Object>();
    argss.put("x-message-ttl",6000);

    // 声明队列
    channel.queueDeclare("ttl_queue", false, false, false, argss);

    // 绑定队列和交换机
    channel.queueBind("ttl_queue","simple_exchange","ttl.first");

    // String queue, boolean durable, boolean exclusive, boolean autoDelete,Map<String, Object> arguments
    System.out.println(" Waiting for message....");

    // 创建消费者
    DefaultConsumer consumer = new DefaultConsumer(channel) {
      @Override
      public void handleDelivery(String consumerTag, Envelope envelope,
                                 AMQP.BasicProperties properties, byte[] body) throws IOException {
        String msg = new String(body, "UTF-8");
        System.out.println("Received message : '" + msg + "'");
      }
    };
    // 开始获取消息
    // String queue, boolean autoAck, Consumer callback
    channel.basicConsume("ttl_queue", true, consumer);
  }


}
