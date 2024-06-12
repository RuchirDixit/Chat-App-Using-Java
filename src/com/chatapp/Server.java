package com.chatapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
public class Server {

    ServerSocket serverSocket;
    Socket socket;
    BufferedReader bufferedReader;
    PrintWriter out;

    public Server(){
        try {
            serverSocket = new ServerSocket(8887);
            System.out.println("Server is ready to accept new requests....");
            socket = serverSocket.accept();

            // get input stream from socket (client) convert bytes into chars using InputStreamReader and assign it to BUfferedReader
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());
            // Start reading and writing messages
            startReading();
            startWriting();

        }
        catch (Exception e){
           e.printStackTrace();
        }
    }

    private void startReading() {
        Runnable input = () -> {
            System.out.println("Server Reader started....");
            try {
            while(true && !socket.isClosed()){

                    String messageFromClient = bufferedReader.readLine();
                    if(messageFromClient.equalsIgnoreCase("Bye")){
                        System.out.println("Client has ended the chat!");
                        socket.close();
                        break;
                    }
                    System.out.println("Client:"+messageFromClient);
            }
            } catch (IOException e) {
                System.out.println("Connection with client is closed!!");
            }
        };
        new Thread(input).start();
    }

    private void startWriting() {
        Runnable output = () -> {
            System.out.println("Server Writer started....");
          while (true && !socket.isClosed()){
              try {
                  BufferedReader serverToClient = new BufferedReader(new InputStreamReader(System.in));
                  String content = serverToClient.readLine();
                  out.println(content);
                  out.flush();
                  if(content.equalsIgnoreCase("bye")){
                      socket.close();
                      break;
                  }
              } catch (IOException e) {
                  System.out.println("Connection with client is closed!!");
              }
          }
        };
        new Thread(output).start();
    }

    public static void main(String[] args) {
        System.out.println("Server is starting....");
        new Server();
    }
}
