package com.wolfman.middleware.dubbo.provider;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.ClassPathResource;

public class SpringTest {


    public static void main(String[] args) {
        ClassPathResource resource = new ClassPathResource("application-context.xml");

        DefaultListableBeanFactory factory = new DefaultListableBeanFactory();

        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(factory);

        reader.loadBeanDefinitions(resource);

    }


}
