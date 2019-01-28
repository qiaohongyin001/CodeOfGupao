package com.wolfman.theory.tcp.socket.bio;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketClient {

  public static void main(String[] args) {
    try {
      Socket socket = new Socket("localhost",8888);
      PrintWriter pw = new PrintWriter(socket.getOutputStream(),true);
      pw.println("hello,socket!");
      socket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }


  }

}
