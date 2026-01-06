package com.mycompany.client_xo_game;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import com.mycompany.client_xo_game.navigation.Navigation;
import com.mycompany.client_xo_game.navigation.Routes;

public class ProfileController {

    @FXML
    private StackPane rootPane;
    @FXML
    private Label usernameLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Label scoreLabel;
    @FXML
    private Label winsLabel, drawsLabel, lossesLabel;

    @FXML
    public void initialize() {
        // 1. Entrance Animation
        rootPane.setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(800), rootPane);
        fadeIn.setToValue(1);
        fadeIn.play();

        // 2. Load User Data
        loadUserData();
    }

    private void loadUserData() {
        // Mock Data
        usernameLabel.setText("Ahmed Tayseer");
        emailLabel.setText("ahmed.t@example.com");
        scoreLabel.setText("2500"); // Just the number, handled styling in CSS

        winsLabel.setText("42");
        drawsLabel.setText("15");
        lossesLabel.setText("8");
    }

    @FXML
    private void handleEdit() {
        System.out.println("Edit Profile Clicked");
        // Navigation.goTo(Routes.EDIT_PROFILE);
    }

    @FXML
    private void handleLogout() {
        System.out.println("Logging out...");
        playExitTransition(() -> Navigation.goTo(Routes.LOGIN));
    }

    @FXML
    private void goBack() {
        playExitTransition(() -> Navigation.goTo(Routes.ONLINE_PLAYERS));
    }

    private void playExitTransition(Runnable onFinished) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), rootPane);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> onFinished.run());
        fadeOut.play();
    }
}
