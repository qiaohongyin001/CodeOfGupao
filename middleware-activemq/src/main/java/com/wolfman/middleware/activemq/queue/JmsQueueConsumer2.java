package com.wolfman.middleware.activemq.queue;


import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * 点对点消费端
 */
public class JmsQueueConsumer2 {

    public static void main(String[] args) {

        //step1.ConnectionFactory 连接工厂
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://39.107.31.208:61616");
        Connection connection = null;
        try {
            //step2.封装客户端与JMS provider 之间的一个虚拟的连接
            connection = connectionFactory.createConnection();
            connection.start();

            //step3.Session 生产和消费的一个单线程上下文；用于创建
            Session session = connection.createSession(true,Session.AUTO_ACKNOWLEDGE);

            //step4.创建队列（如果队列已经存在则不会创建， first-queue是队列名称）
            //destination表示目的地
            Destination destination = session.createQueue("wjg-fb-queue");
            //step5.创建消息接收者
            MessageConsumer messageConsumer = session.createConsumer(destination);
            TextMessage textMessage = (TextMessage) messageConsumer.receive();
            System.out.println(textMessage.getText());
            session.commit();
            session.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }finally {
            if (connection != null){
                try {
                    connection.close();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        }


    }





}
