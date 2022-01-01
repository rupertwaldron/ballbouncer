package com.ruppyrup.bigfun.server;

import com.ruppyrup.bigfun.controllers.ServerController;
import javafx.application.Platform;
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
import static com.ruppyrup.bigfun.clientcommands.EchoCommands.BALL_POSITION;
import static com.ruppyrup.bigfun.clientcommands.EchoCommands.CO_ORD;
import static com.ruppyrup.bigfun.clientcommands.EchoCommands.REMOVE_PLAYER;

public class EchoMultiServer extends Service<EchoServerResult>  {
    private boolean enableServer = true;
    private final ExecutorService executorService;
    private Map<String, PrintWriter> clients = new HashMap<>();
    private final ServerController serverController;

    public EchoMultiServer(ServerController serverController) {
        this.serverController = serverController;
        this.executorService = Executors.newFixedThreadPool(20);
    }

    @Override
    protected Task<EchoServerResult> createTask() {
        return new Task<EchoServerResult>() {
            @Override
            protected EchoServerResult call() throws Exception {
                return startServer(6666);
            }
        };
    }

//    public static void main(String[] args) {
//        EchoMultiServer server = new EchoMultiServer();
//        server.start(6666);
//
//    }

    public EchoServerResult startServer(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)){
            System.out.println("Server is running");
            while (enableServer)
                executorService.execute(new EchoClientHandler(serverSocket.accept()));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("Server stopped");
            stop();
            return EchoServerResult.SUCCESS;
        }
    }

    public void stop() {
        enableServer = false;
    }

    public void sendBallPosition(double newXPosition, double newYPosition) {
        clients.forEach((id, writer) -> writer.println(BALL_POSITION + ">" + "all" + "%" + newXPosition + ":" + newYPosition));
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

                addNewPlayerToServerAndExistingPlayers(out);

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    System.out.println("Client says :: " + inputLine);
                    if (".".equals(inputLine)) {
                        out.println("bye");
                        break;
                    }
                    updateServerAndPlayersWithUpdatedPlayerLocation(inputLine);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                System.out.println("Client closed:: " + clientId);
                clients.remove(clientId);
                clients.forEach((key, value) -> value.println(REMOVE_PLAYER + ">" + clientId));
                serverController.removePlayer(clientId);
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void updateServerAndPlayersWithUpdatedPlayerLocation(String inputLine) {
            String sendMessage = CO_ORD + ">" +  clientId + "%" + inputLine;
            String[] xyValues = inputLine.split(":");
            Double xValue = Double.valueOf(xyValues[0]);
            Double yValue = Double.valueOf(xyValues[1]);
            Platform.runLater(() -> serverController.moveButton(clientId, xValue, yValue));
            clients.forEach((id, writer) -> writer.println(sendMessage));
        }

        private void addNewPlayerToServerAndExistingPlayers(PrintWriter out) {
            Platform.runLater(() -> serverController.addNewPlayer(clientId));

            clients.forEach((id, writer) -> {
                writer.println(ADD_PLAYER + ">" + clientId);
                out.println(ADD_PLAYER + ">" + id);
            });

            clients.put(clientId, out);
        }
    }
}
