package com.mycompany.client_xo_game;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;

public class RegisterController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Label messageLabel;

    @FXML
    private void handleRegister() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirm = confirmPasswordField.getText();

        if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            messageLabel.setText("All fields are required");
            return;
        }

        if (!password.equals(confirm)) {
            messageLabel.setText("Passwords do not match");
            return;
        }

        messageLabel.setText("Account created successfully!");
        messageLabel.setStyle("-fx-text-fill: green;");
    }

    @FXML
    private void goToLogin() {
        try {
            App.setRoot("login"); // 🔥 FIXED
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
