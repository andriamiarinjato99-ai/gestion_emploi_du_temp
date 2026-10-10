package com.edt.app;

import com.edt.dao.DatabaseConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;


public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/edt/view/main.fxml"));
        Scene scene = new Scene(loader.load(), 1180, 720);

        stage.setTitle("Gestion Emploi du Temps");
        stage.setScene(scene);
        stage.setMinWidth(980);
        stage.setMinHeight(620);
        try {
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/com/edt/view/icon.png")));
        } catch (Exception ignored) {
            
        }
        stage.show();
    }

    @Override
    public void stop() {
        DatabaseConnection.closeConnection();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
