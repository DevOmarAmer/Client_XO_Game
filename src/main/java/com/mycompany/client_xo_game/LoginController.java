package com.mycompany.client_xo_game;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import com.mycompany.client_xo_game.navigation.Navigation;
import com.mycompany.client_xo_game.navigation.Routes;

public class LoginController {

    @FXML private StackPane rootPane;
    @FXML private VBox contentBox;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;
    @FXML private Button back_id;
    @FXML private Button loginBtn;
    @FXML private Label titleLabel;
    @FXML private Hyperlink registerLink;

    @FXML
    public void initialize() {
        // --- PAGE ENTRANCE ANIMATION ---
        rootPane.setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(800), rootPane);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        // --- RESPONSIVE LOGIC ---
        rootPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            double w = newVal.doubleValue();
            titleLabel.setStyle("-fx-font-size: " + Math.min(60, Math.max(30, w / 12)) + "px;");
            double inputSize = Math.max(14, w / 45);
            usernameField.setStyle("-fx-font-size: " + inputSize + "px;");
            passwordField.setStyle("-fx-font-size: " + inputSize + "px;");
            double inputHeight = Math.max(45, w / 15);
            usernameField.setPrefHeight(inputHeight);
            passwordField.setPrefHeight(inputHeight);
            loginBtn.setStyle("-fx-font-size: " + Math.max(14, w / 40) + "px;");
            loginBtn.setPrefHeight(inputHeight);
            back_id.setStyle("-fx-font-size: " + Math.max(12, w / 50) + "px;");
        });
    }

    private void playExitTransition(Runnable onFinished) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), rootPane);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> onFinished.run());
        fadeOut.play();
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please fill all fields");
            return;
        }

        if (username.equals("admin") && password.equals("1234")) {
            playExitTransition(() -> Navigation.goTo(Routes.ONLINE_PLAYERS));
        } else {
            messageLabel.setText("Invalid username or password");
        }
    }

    @FXML
    private void goToRegister() {
        playExitTransition(() -> Navigation.goTo(Routes.REGISTER));
    }

    @FXML
    private void goBack() {
        playExitTransition(() -> Navigation.goTo(Routes.MODE_SELECTION));
    }
}