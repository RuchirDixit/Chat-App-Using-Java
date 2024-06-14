package com.chatapp;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client extends JFrame{

    Socket socket;
    BufferedReader bufferedReader;
    PrintWriter out;

    // Declare Swing components
    private JLabel heading = new JLabel("Client Chat");
    private JTextArea messageArea = new JTextArea();
    private JTextField input = new JTextField();
    private Font font = new Font("Roboto",Font.PLAIN,20);
    JScrollPane jScrollPane = new JScrollPane(messageArea);

    public Client(){
        try {
            System.out.println("Sending connection request to server...");
            socket = new Socket("127.0.0.1",8887);
            System.out.println("Connected with Server!! Start chatting..");
            // get input stream from socket (server) convert bytes into chars using InputStreamReader and assign it to BUfferedReader
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());
            createGUI();
            handleChatEvents();
            // Start reading and writing messages
            startReading();
            //startWriting();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void handleChatEvents() {
        input.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                // enter key code == 10
                if(e.getKeyCode()==10){
                    String contentTOSendToServer = input.getText();
                    messageArea.append("Me:"+contentTOSendToServer+"\n");
                    out.println(contentTOSendToServer);
                    out.flush();
                    input.setText("");
                    input.requestFocus();
                }
            }
        });
    }

    private void createGUI() {
        // windows properties
        this.setTitle("Client Messenger[END]");
        this.setSize(600,600);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // adding components
        heading.setFont(font);
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        messageArea.setFont(font);

        input.setFont(font);
        input.setHorizontalAlignment(SwingConstants.CENTER);
        messageArea.setEditable(false);

        // set layout of frame
        this.setLayout(new BorderLayout());
        this.add(heading,BorderLayout.NORTH);

        jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        this.add(jScrollPane,BorderLayout.CENTER);
        messageArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                scrollToBottom();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                scrollToBottom();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                scrollToBottom();
            }
        });
        this.add(input,BorderLayout.SOUTH);
        this.setVisible(true);
    }

    private void scrollToBottom() {
        SwingUtilities.invokeLater(() -> {
            JScrollBar verticalBar = jScrollPane.getVerticalScrollBar();
            verticalBar.setValue(verticalBar.getMaximum());
        });
    }

    private void startReading() {
        Runnable input = () -> {
            System.out.println("Client Reader started....");
            try {
            while(true && !socket.isClosed()){
                    String messageFromServer = bufferedReader.readLine();
                    if(messageFromServer.equalsIgnoreCase("Bye")){
                        System.out.println("Server has ended the chat!");
                        JOptionPane.showMessageDialog(this,"Server has ended the chat!",
                                "Chat End",JOptionPane.WARNING_MESSAGE);
                        this.input.setEnabled(false);
                        socket.close();
                        break;
                    }
                    //System.out.println("Server:"+messageFromServer);
                messageArea.append("Server:"+messageFromServer+"\n");

            }
            } catch (IOException e) {
                System.out.println("Connection with server is closed!!");
            }
        };
        new Thread(input).start();
    }

    // not required as swing gui will handle it
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
