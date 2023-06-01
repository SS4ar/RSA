package com.example.rsa;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.StageStyle;
import java.io.IOException;

public class RsaApplication extends javafx.application.Application {
    private double xOffset = 0;
    private double yOffset = 0;
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(RsaApplication.class.getResource("rsa-view.fxml"));
        Parent root = fxmlLoader.load();
        HBox header = createCustomHeader(stage);

        VBox rootContainer = new VBox(header, root);

        Scene scene = new Scene(rootContainer);

        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("RSA-App");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    private HBox createCustomHeader(Stage stage) {
        Label titleLabel = new Label("RSA-App");
        titleLabel.setStyle("-fx-text-fill: white");
        Button minimizeButton = new Button("-");
        minimizeButton.setOnAction(event -> stage.setIconified(true));
        Button closeButton = new Button("X");
        closeButton.setOnAction(event -> stage.close());


        HBox header = new HBox(titleLabel, new Region(), minimizeButton, closeButton);
        header.setStyle("-fx-background-color: #555555;");
        header.setPadding(new Insets(10));
        header.setSpacing(10);
        header.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        header.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });

        HBox.setHgrow(header.getChildren().get(1), Priority.ALWAYS);

        return header;
    }

    public static void main(String[] args) {
        launch();
    }
}