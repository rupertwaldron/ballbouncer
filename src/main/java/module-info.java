module com.ruppyrup.bigfun {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires com.jfoenix;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;

    requires org.testfx;
    requires org.junit.jupiter.api;
    requires org.testfx.junit5;

    opens com.ruppyrup.bigfun to javafx.fxml;
    exports com.ruppyrup.bigfun;

//    opens com.ruppyrup.bigfun.clientcommands to javafx.fxml;
    exports com.ruppyrup.bigfun.controllers;
    opens com.ruppyrup.bigfun.controllers to javafx.fxml;
}

