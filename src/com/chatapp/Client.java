package com.chatapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

    Socket socket;
    BufferedReader bufferedReader;
    PrintWriter out;

    public Client(){
        try {
            System.out.println("Sending connection request to server...");
            socket = new Socket("127.0.0.1",8887);
            System.out.println("Connected with Server!! Start chatting..");
            // get input stream from socket (server) convert bytes into chars using InputStreamReader and assign it to BUfferedReader
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
            System.out.println("Client Reader started....");
            try {
            while(true && !socket.isClosed()){
                    String messageFromServer = bufferedReader.readLine();
                    if(messageFromServer.equalsIgnoreCase("Bye")){
                        System.out.println("Server has ended the chat!");
                        socket.close();
                        break;
                    }
                    System.out.println("Server:"+messageFromServer);

            }
            } catch (IOException e) {
                System.out.println("Connection with server is closed!!");
            }
        };
        new Thread(input).start();
    }

    private void startWriting() {
        Runnable output = () -> {
            System.out.println("Client Writer started....");
            try {
            while (true && !socket.isClosed()){

                    BufferedReader clientToServer = new BufferedReader(new InputStreamReader(System.in));
                    String content = clientToServer.readLine();
                    out.println(content);
                    out.flush();
                    if(content.equalsIgnoreCase("bye")){
                        socket.close();
                        break;
                    }
            }
            } catch (IOException e) {
                System.out.println("Connection with server is closed!!");
            }
        };
        new Thread(output).start();
    }


    public static void main(String[] args) {
        System.out.println("Client started...");

        new Client();
    }
}
