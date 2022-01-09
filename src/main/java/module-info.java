module com.ruppyrup.bigfun {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.jfoenix;

    opens com.ruppyrup.bigfun to javafx.fxml;
    exports com.ruppyrup.bigfun;

    exports com.ruppyrup.bigfun.controllers;
    opens com.ruppyrup.bigfun.controllers to javafx.fxml;
}

