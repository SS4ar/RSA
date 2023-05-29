package com.example.rsa;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class RsaApplication extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(RsaApplication.class.getResource("rsa-view.fxml"));
        Parent root = fxmlLoader.load();


        // Установка стилей для корневого контейнера
        root.setStyle("-fx-background-color: #555555; -fx-text-fill: white;");

        // Создание сцены с корневым контейнером
        Scene scene = new Scene(root);

        // Установка стилей для сцены (если необходимо)
        // scene.setFill(Color.DARKGRAY);

        stage.setTitle("RSA-App");
        stage.setScene(scene);
        stage.show();
    }



    public static void main(String[] args) {
        launch();
    }
}