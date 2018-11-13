package com.wolfman.middleware.activemq.queue;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * 点对点发送端
 */
public class JmsQueueSender {

    public static void main(String[] args) {

        //step1.创建连接工厂
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://127.0.0.1:61616");
        /* ActiveMQConnectionFactory connectionFactory =
                new ActiveMQConnectionFactory("failover:(tcp://39.107.31.208:61616,tcp://39.107.32.43:61616)");
        */
        Connection connection = null;

        try {
            //step2.创建连接
            connection = connectionFactory.createConnection();
            connection.start();
            //step3.session 生产和消费的一个单线程上下文，用于创建
            //Session.AUTO_ACKNOWLEDGE 自动提交
            Session session = connection.createSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE);
            //step4.创建队列（如果队列已经存在则不会创建）
            //destination表示目的地
            Destination destination = session.createQueue("wjg-fb-queue");
            //创建消息的接收者
            MessageProducer messageProducer = session.createProducer(destination);
            TextMessage textMessage = session.createTextMessage("第六关真假乌鸡国王");
            messageProducer.send(textMessage);
            System.out.println(textMessage.getText());
            session.commit();
            session.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }finally {
            if (connection !=null){
                try {
                    connection.close();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        }
    }







}
