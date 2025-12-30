package com.mycompany.client_xo_game;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label messageLabel;

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please fill all fields");
            return;
        }

        if (username.equals("admin") && password.equals("1234")) {
            try {
                App.setRoot("OnlinePlayers");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            messageLabel.setText("Invalid username or password");
        }
    }

    @FXML
    private void goToRegister() {
        try {
            App.setRoot("Register");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
