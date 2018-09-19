package com.wolfman.middleware.dubbo.consumer;

import com.wolfman.middleware.dubbo.api.DoRequest;
import com.wolfman.middleware.dubbo.api.DoResponse;
import com.wolfman.middleware.dubbo.api.HelloWorldService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

public class DubboSingleConsumer {

    public static void main(String[] args) throws IOException {
        ClassPathXmlApplicationContext context =
                new ClassPathXmlApplicationContext("dubbo-consumer.xml");
//        异步回掉
//        IHelloWorldService service = (IHelloWorldService) context.getBean("orderServices");
//        long start=System.currentTimeMillis();
//        DoRequest request = new DoRequest();
//        request.setName("huhao");
//        service.sayHello(request);
//        Future<DoResponse> response = RpcContext.getContext().getFuture();
//        System.out.println("aaaaaaaaaa");
//        DoResponse response1 = response.get();
//        System.out.println(response1);
//        long end=System.currentTimeMillis();
//        System.out.println("总共耗时："+(end-start)/1000+"秒");

        HelloWorldService service = (HelloWorldService) context.getBean("orderServices");
        DoRequest request = new DoRequest();
        request.setName("huhao");
        DoResponse response = service.sayHello(request);
        System.out.println(response);
        System.in.read();

    }

}
