package com.drawing;

import com.drawing.utils.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Classe principale de l'application de dessin JavaFX
 */
public class DrawingApplication extends Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        Logger.getInstance().info("Démarrage de l'application de dessin");
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root, 1200, 800);
            
            primaryStage.setTitle("Application de Dessin - Design Patterns");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(600);
            
            // Gérer la fermeture de l'application
            primaryStage.setOnCloseRequest(event -> {
                Logger.getInstance().info("Fermeture de l'application");
                Logger.getInstance().close();
            });
            
            primaryStage.show();
            
            Logger.getInstance().info("Application démarrée avec succès");
            
        } catch (Exception e) {
            Logger.getInstance().error("Erreur lors du démarrage de l'application: " + e.getMessage());
            throw e;
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
