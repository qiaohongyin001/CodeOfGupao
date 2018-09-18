package com.wolfman.middleware.zookeeper.rmi;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class Client {

    public static void main(String[] args) {


        try {
            IHelloService helloService = (IHelloService) Naming.lookup("rmi://39.107.31.208/Hello");

            System.out.println(helloService.sayHello("huhao"));


        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }


    }



}
