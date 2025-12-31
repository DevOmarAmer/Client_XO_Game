package com.mycompany.client_xo_game;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import com.mycompany.client_xo_game.navigation.Navigation;
import com.mycompany.client_xo_game.navigation.Routes;

public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label messageLabel;

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please fill all fields");
            return;
        }

        if (username.equals("admin") && password.equals("1234")) {
            Navigation.goTo(Routes.ONLINE_PLAYERS);
        } else {
            messageLabel.setText("Invalid username or password");
        }
    }

    @FXML
    private void goToRegister() {
        Navigation.goTo(Routes.REGISTER);
    }

    @FXML
    private void goBack() {
        Navigation.goTo(Routes.MODE_SELECTION);
    }

}
