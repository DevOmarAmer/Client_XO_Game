package com.mycompany.client_xo_game;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import com.mycompany.client_xo_game.navigation.Navigation;
import com.mycompany.client_xo_game.navigation.Routes;

public class RegisterController {

    @FXML private StackPane rootPane;
    @FXML private VBox contentBox;
    @FXML private GridPane inputGrid;
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label messageLabel;
    @FXML private Button registerBtn;
    @FXML private Label titleLabel;
    @FXML private Hyperlink loginLink;

    @FXML
    public void initialize() {
        // --- ENTRANCE ANIMATION (Fade + Slide Up) ---
        contentBox.setOpacity(0);
        contentBox.setTranslateY(30);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(700), contentBox);
        fadeIn.setToValue(1);

        TranslateTransition slideUp = new TranslateTransition(Duration.millis(700), contentBox);
        slideUp.setToY(0);

        fadeIn.play();
        slideUp.play();

        // --- RESPONSIVE LOGIC ---
        rootPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            double w = newVal.doubleValue();
            if (w > 1200) contentBox.setPrefWidth(w * 0.7);
            else contentBox.setPrefWidth(w * 0.9);

            titleLabel.setStyle("-fx-font-size: " + Math.min(56, Math.max(28, w / 18)) + "px;");
            double inputSize = Math.max(14, w / 55);
            usernameField.setStyle("-fx-font-size: " + inputSize + "px;");
            emailField.setStyle("-fx-font-size: " + inputSize + "px;");
            passwordField.setStyle("-fx-font-size: " + inputSize + "px;");
            confirmPasswordField.setStyle("-fx-font-size: " + inputSize + "px;");
            
            double inputHeight = Math.max(45, w / 18);
            usernameField.setPrefHeight(inputHeight);
            emailField.setPrefHeight(inputHeight);
            passwordField.setPrefHeight(inputHeight);
            confirmPasswordField.setPrefHeight(inputHeight);

            registerBtn.setStyle("-fx-font-size: " + Math.max(16, w / 45) + "px;");
            registerBtn.setPrefHeight(inputHeight + 10);
            
            contentBox.setSpacing(w / 20);
            inputGrid.setHgap(w / 30);
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
    private void goToLogin() {
        playExitTransition(() -> Navigation.goTo(Routes.LOGIN));
    }

    @FXML
    private void handleRegister() {
        // Your registration logic here
        System.out.println("Register button clicked");
    }
}