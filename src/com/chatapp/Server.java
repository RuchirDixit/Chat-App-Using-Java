package com.chatapp;

import java.io.BufferedReader;
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

            startReading();
            startWriting();

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void startReading() {
    }

    private void startWriting() {
    }

    public static void main(String[] args) {
        System.out.println("Server is starting....");
        new Server();
    }
}
