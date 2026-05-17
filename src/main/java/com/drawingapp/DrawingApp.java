package com.drawingapp;

import com.drawingapp.logger.LoggerFactory;
import com.drawingapp.logger.LoggingStrategy;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DrawingApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Initialiser le logger AVANT tout
        LoggerFactory.getLogger(LoggingStrategy.CONSOLE).log("=== Démarrage Application ===");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/drawingapp/view/drawing-view.fxml"));
        Parent root = loader.load();

        primaryStage.setTitle("Application de Dessin - Design Patterns");
        primaryStage.setScene(new Scene(root, 850, 700));
        primaryStage.show();

        LoggerFactory.getLogger(LoggingStrategy.CONSOLE).log("Interface graphique chargée");
    }

    public static void main(String[] args) {
        launch(args);
    }
}