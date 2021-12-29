package com.ruppyrup.bigfun.server;

import com.ruppyrup.bigfun.client.EchoClientResult;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.ruppyrup.bigfun.clientcommands.EchoCommands.ADD_PLAYER;
import static com.ruppyrup.bigfun.clientcommands.EchoCommands.CO_ORD;
import static com.ruppyrup.bigfun.clientcommands.EchoCommands.REMOVE_PLAYER;

public class EchoMultiServer extends Service<EchoClientResult>  {
    private boolean enableServer = true;
    private final ExecutorService executorService;
    private Map<String, PrintWriter> clients = new HashMap<>();

    public EchoMultiServer() {
        this.executorService = Executors.newFixedThreadPool(200);
    }

    @Override
    protected Task<EchoClientResult> createTask() {
        return new Task<>() {
            @Override
            protected EchoClientResult call() throws Exception {
                return startServer(6666);
            }
        };
    }

//    public static void main(String[] args) {
//        EchoMultiServer server = new EchoMultiServer();
//        server.start(6666);
//
//    }

    public EchoClientResult startServer(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)){
            System.out.println("Server is running");
            while (enableServer)
                executorService.execute(new EchoClientHandler(serverSocket.accept()));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("Server stopped");
            return EchoClientResult.SUCCESS;
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
        }

        public void run() {
            try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

                clients.forEach((key, value) -> {
                    value.println(ADD_PLAYER + ">" + clientId);
                    out.println(ADD_PLAYER + ">" + key);
                });

                clients.put(clientId, out);

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    System.out.println("Client says :: " + inputLine);
                    if (".".equals(inputLine)) {
                        out.println("bye");
                        break;
                    }
                    String sendMessage = CO_ORD + ">" +  clientId + "%" + inputLine;
                    clients.forEach((key, value) -> value.println(sendMessage));
//                    out.println("[Server] " + inputLine);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                System.out.println("Client closed:: " + clientId);
                clients.remove(clientId);
                clients.forEach((key, value) -> value.println(REMOVE_PLAYER + ">" + clientId));
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
