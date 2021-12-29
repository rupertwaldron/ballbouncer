package com.ruppyrup.bigfun.clientcommands;

import com.ruppyrup.bigfun.ClientController;
import com.ruppyrup.bigfun.Command;

public class CommandFactory {

    private final ClientController animationController;

    public CommandFactory(ClientController animationController) {
        this.animationController = animationController;
    }

    public Command getCommand(EchoCommands echoCommands, String input) {
        return switch(echoCommands) {
            case ADD_PLAYER -> new AddPlayer(animationController, input);
            case CO_ORD -> new MovePosition(animationController, input);
            default -> throw new IllegalStateException("Unexpected value: " + echoCommands);
        };
    }
}
