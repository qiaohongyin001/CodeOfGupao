package com.wolfman.theory.tcp.socket.multicast.danbo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SingleClient {

  public static void main(String[] args) {
    try {

      Socket socket = new Socket("localhost", 8888);
      BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      PrintWriter writer = new PrintWriter(socket.getOutputStream(),true);
      writer.println("hello，socket");
      while(true){
        String serverData = reader.readLine();
        if (serverData == null) {
          break;
        }
        System.out.println("服务端数据："+serverData);
      }
      socket.close();
      reader.close();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }



  }




}
