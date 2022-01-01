package com.ruppyrup.bigfun.client;

import com.ruppyrup.bigfun.controllers.ClientController;
import com.ruppyrup.bigfun.clientcommands.Command;
import com.ruppyrup.bigfun.clientcommands.CommandFactory;
import com.ruppyrup.bigfun.clientcommands.EchoCommands;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class EchoClient extends Service<EchoClientResult> {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private final String ipAddress;
    private final int port;
    private Command command;
    private final CommandFactory commandFactory;

    public EchoClient(ClientController clientController, String ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.commandFactory = new CommandFactory(clientController);
    }

    private EchoClientResult startConnection() {
        try {
            clientSocket = new Socket(ipAddress, port);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));


            while(true) {
                String[] serverInput = in.readLine().split(">");
                System.out.println(serverInput[0] + ">" + serverInput[1]);
                command = commandFactory.getCommand(EchoCommands.valueOf(serverInput[0]), serverInput[1]);
                command.execute();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("Closing connection on port :: " + port);
            stopConnection();
            System.exit(0);
        }
        return EchoClientResult.SUCCESS;
    }

    public String sendMessage(String msg) {
        out.println(msg);
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
    protected Task<EchoClientResult> createTask() {
        return new Task<>() {
            @Override
            protected EchoClientResult call() throws Exception {
                return startConnection();
            }
        };
    }
}
