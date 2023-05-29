module com.example.rsa {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.rsa to javafx.fxml;
    exports com.example.rsa;
    exports com.example.rsa.controller;
    opens com.example.rsa.controller to javafx.fxml;
}