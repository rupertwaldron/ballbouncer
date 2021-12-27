package com.ruppyrup.bigfun.clientcommands;

import com.ruppyrup.bigfun.AnimationController;
import com.ruppyrup.bigfun.Command;

public class MovePosition implements Command {

    private final AnimationController animationController;
    private final String id;
    private final String coordinates;

    public MovePosition(AnimationController animationController, String input) {
        String[] inputs = input.split("%");
        id = inputs[0];
        coordinates = inputs[1];
        this.animationController = animationController;
    }

    @Override
    public void execute() {
//        animationController.addNewButton(id);
        System.out.println("Executed MovePosition to coordinate: " + coordinates);
    }
}
