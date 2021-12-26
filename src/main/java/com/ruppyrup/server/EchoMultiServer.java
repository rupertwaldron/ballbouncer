package com.ruppyrup.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EchoMultiServer {
    private boolean enableServer = true;
    private final ExecutorService executorService;
    private Map<String, Socket> clients = new HashMap<>();

    public EchoMultiServer() {
        this.executorService = Executors.newFixedThreadPool(200);
    }

    public static void main(String[] args) {
        EchoMultiServer server = new EchoMultiServer();
        server.start(6666);

    }

    public void start(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)){
            System.out.println("Server is running");
            while (enableServer)
                executorService.execute(new EchoClientHandler(serverSocket.accept()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        enableServer = false;
    }

    private class EchoClientHandler implements Runnable {
        private Socket clientSocket;
        private String clientId;

        public EchoClientHandler(Socket socket) {
            this.clientSocket = socket;
            clientId = socket.getInetAddress() + "::" + socket.getPort();
            System.out.println("Connected to client " + clientId);
            clients.put(clientId, socket);
        }

        public void run() {
            try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

//                clients.keySet().stream()
//                        .filter(k -> !clientId.equals(k))
//                        .forEach(out::print);

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    System.out.println("Client says :: " + inputLine);
                    if (".".equals(inputLine)) {
                        out.println("bye");
                        break;
                    }
                    out.println("[Server] " + inputLine);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                System.out.println("Client closed:: " + clientId);
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
