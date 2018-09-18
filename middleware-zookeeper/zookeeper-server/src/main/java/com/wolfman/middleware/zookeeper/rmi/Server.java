package com.wolfman.middleware.zookeeper.rmi;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class Server {

    public static void main(String[] args) {
        try {
            //注意：客户端和服务端的接口包名需一直，要不会报错
            IHelloService helloService = new HelloServiceImpl();//已经发布了一个远程对象
            LocateRegistry.createRegistry(2018);
            Naming.rebind("rmi://39.107.31.208/Hello",helloService);
            System.out.println("服务启动成功");
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


    }


}
