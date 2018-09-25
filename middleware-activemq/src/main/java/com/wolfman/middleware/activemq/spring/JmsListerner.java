package com.wolfman.middleware.activemq.spring;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

public class JmsListerner implements MessageListener {

    @Override
    public void onMessage(Message message) {
        try {
            System.out.println(message);
            System.out.println(((TextMessage)message).getText());
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

}