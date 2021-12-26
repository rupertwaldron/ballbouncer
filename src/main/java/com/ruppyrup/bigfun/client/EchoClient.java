package com.ruppyrup.bigfun.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class EchoClient {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public static void main(String[] args) {
        EchoClient echoClient = new EchoClient();
        echoClient.startConnection("127.0.0.1", 6666);
//        String response = echoClient.sendMessage("Hello from echo client");
//        System.out.println("Response from server -> " + response);
    }

    public void startConnection(String ip, int port) {
        try {
            clientSocket = new Socket(ip, port);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String response = sendMessage("Hello from echo client");
            System.out.println("Response from server -> " + response);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String sendMessage(String msg) {
        out.println(msg);
        String resp = null;
        try {
            resp = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resp;
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
}
