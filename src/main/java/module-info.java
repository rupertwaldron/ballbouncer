module com.ruppyrup.bigfun {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.jfoenix;
    requires org.testfx;
    requires org.junit.jupiter.api;
    requires org.testfx.junit5;

    opens com.ruppyrup.bigfun to javafx.fxml;
    exports com.ruppyrup.bigfun;

    exports com.ruppyrup.bigfun.controllers;
    opens com.ruppyrup.bigfun.controllers to javafx.fxml;
}

