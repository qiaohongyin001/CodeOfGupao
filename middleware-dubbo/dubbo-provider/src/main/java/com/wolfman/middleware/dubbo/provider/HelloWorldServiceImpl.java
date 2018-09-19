package com.wolfman.middleware.dubbo.provider;


import com.wolfman.middleware.dubbo.api.DoRequest;
import com.wolfman.middleware.dubbo.api.DoResponse;
import com.wolfman.middleware.dubbo.api.HelloWorldService;
import org.springframework.stereotype.Service;

@Service(value = "helloWorldService")
public class HelloWorldServiceImpl implements HelloWorldService {

    @Override
    public DoResponse sayHello(DoRequest request) {
        System.out.println("曾经来过："+request);
        DoResponse response=new DoResponse();
        response.setCode("1000");
        response.setMemo("处理成功");
        return response;
    }

}
