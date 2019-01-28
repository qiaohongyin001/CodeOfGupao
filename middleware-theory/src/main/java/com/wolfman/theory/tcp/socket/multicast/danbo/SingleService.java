package com.wolfman.theory.tcp.socket.multicast.danbo;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class SingleService {

  public static void main(String[] args) throws IOException {
    ServerSocket serverSocket = null;
    try {
      serverSocket = new ServerSocket(8888);
      while (true) {
        Socket socket = serverSocket.accept();
        new Thread(()->{
          try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            while(true){
              String clientData = reader.readLine();
              if (clientData == null) {
                break;
              }
              System.out.println("客户端数据:"+clientData);
              writer.println("Hello Mik");
              writer.flush();
            }
          } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        }).start();

      }
    } catch (Exception e) {
      e.printStackTrace();
    }finally {
      serverSocket.close();
    }

  }


}
