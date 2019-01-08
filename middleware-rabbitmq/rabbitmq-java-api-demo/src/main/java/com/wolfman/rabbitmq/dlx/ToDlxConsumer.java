package com.wolfman.rabbitmq.dlx;

import com.rabbitmq.client.*;
import com.wolfman.rabbitmq.ConnectionUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class ToDlxConsumer {

  public static void main(String[] args) throws IOException, TimeoutException {
    //建立连接
    Connection conn = ConnectionUtils.getConnection();
    //创建消息通道
    Channel channel = conn.createChannel();

    // 指定队列的死信交换机
    Map<String,Object> arguments = new HashMap<String,Object>();
    arguments.put("x-dead-letter-exchange","dlx_exchange_kawayi");
    // arguments.put("x-expires","9000"); // 设置队列的TTL
    // arguments.put("x-max-length", 4); // 如果设置了队列的最大长度，超过长度时，先入队的消息会被发送到DLX

    // 声明队列
    channel.queueDeclare("kawayi_queue_two", false, false, false, arguments);
    // 声明交换机
    // String exchange, String type, boolean durable, boolean autoDelete, Map<String, Object> arguments
    channel.exchangeDeclare("kawayi","direct",false, false, null);
    // 绑定队列和交换机
    channel.queueBind("kawayi_queue_two","kawayi","ka");

    // 声明死信交换机
    channel.exchangeDeclare("dlx_exchange_kawayi","topic", false, false, false, null);
    // 声明死信队列
    channel.queueDeclare("dlx_queue_kawayi_two", false, false, false, null);
    // 绑定，此处 Dead letter routing key 设置为 #
    channel.queueBind("dlx_queue_kawayi_two","dlx_exchange_kawayi","#");

    // String queue, boolean durable, boolean exclusive, boolean autoDelete,Map<String, Object> arguments
    System.out.println(" Waiting for message....");
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
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
    channel.basicConsume("kawayi_queue_two", true, consumer);
  }


}
