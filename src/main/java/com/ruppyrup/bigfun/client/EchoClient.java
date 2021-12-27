package com.ruppyrup.bigfun.client;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class EchoClient extends Service<EmailLoginResult> {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private final String ipAddress;
    private final int port;

    public EchoClient(String ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;
    }

//    public static void main(String[] args) {
//        EchoClient echoClient = new EchoClient(ipAddress, port);
//        echoClient.startConnection("127.0.0.1", 6666);
////        String response = echoClient.sendMessage("Hello from echo client");
////        System.out.println("Response from server -> " + response);
//    }

    private EmailLoginResult startConnection() {
        try {
            clientSocket = new Socket(ipAddress, port);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));


            while(true) {
                System.out.println("Reading from server.... " + in.readLine());
            }

//            Scanner keyboard = new Scanner(System.in);
//            System.out.print("% ");
//            while (keyboard.hasNext()) {
//                String next = keyboard.nextLine();
//                if (".".equals(next)) break;
//                String response = sendMessage(next);
//                System.out.println("Response from server -> " + response);
//                System.out.print("% ");
//            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            stopConnection();
        }
        return EmailLoginResult.FAILED_BY_CREDENTIALS;
    }

    public String getOtherClients() {
        try {
            return in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String sendMessage(String msg) {
        out.println(msg);
//        String resp = null;
//        try {
//            resp = in.readLine();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return msg;
    }

    public void stopConnection() {
        try {
            in.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        out.close();
    }

    @Override
    protected Task<EmailLoginResult> createTask() {
        return new Task<>() {
            @Override
            protected EmailLoginResult call() throws Exception {
                return startConnection();
            }
        };
    }
}
