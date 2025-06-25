module com.drawing {
    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    requires transitive javafx.graphics;
    requires transitive javafx.base;
    requires java.sql;
    requires com.h2database;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.core;
    requires org.slf4j;
    requires ch.qos.logback.classic;
    requires ch.qos.logback.core;
    requires javafx.swing;
    requires java.desktop;

    opens com.drawing to javafx.fxml;
    opens com.drawing.view to javafx.fxml;
    opens com.drawing.controller to javafx.fxml;
    opens com.drawing.model.shapes to com.fasterxml.jackson.databind;
    opens com.drawing.model.drawing to com.fasterxml.jackson.databind;
    
    exports com.drawing;
    exports com.drawing.view;
    exports com.drawing.controller;
    exports com.drawing.model.shapes;
    exports com.drawing.model.drawing;
    exports com.drawing.factory;
    exports com.drawing.strategy;
    exports com.drawing.utils;
    exports com.drawing.observer;
    exports com.drawing.command;
    exports com.drawing.database;
}