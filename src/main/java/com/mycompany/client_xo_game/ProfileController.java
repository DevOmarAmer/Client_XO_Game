package com.mycompany.client_xo_game;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import com.mycompany.client_xo_game.navigation.Navigation;
import com.mycompany.client_xo_game.navigation.Routes;

public class ProfileController {

    @FXML
    private StackPane rootPane;
    @FXML
    private Circle avatarCircle;
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

        // 2. Load User Data (Mock Data for now)
        loadUserData();
    }

    private void loadUserData() {
        // In a real app, you would fetch this from your UserSession or Database
        usernameLabel.setText("Ahmed Tayseer");
        emailLabel.setText("ahmed.t@example.com");
        scoreLabel.setText("Total Score: 2500 XP");

        winsLabel.setText("42");
        drawsLabel.setText("15");
        lossesLabel.setText("8");

        // You can set an image to the circle here using:
        // avatarCircle.setFill(new ImagePattern(new Image("path/to/image.png")));
    }

    @FXML
    private void handleEdit() {
        System.out.println("Edit Profile Clicked");
        // Navigation.goTo(Routes.EDIT_PROFILE);
    }

    @FXML
    private void handleLogout() {
        System.out.println("Logging out...");
        // Clear session logic here
        playExitTransition(() -> Navigation.goTo(Routes.LOGIN));
    }

    @FXML
    private void goBack() {
        // Return to Online Players list
        playExitTransition(() -> Navigation.goTo(Routes.ONLINE_PLAYERS));
    }

    private void playExitTransition(Runnable onFinished) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), rootPane);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> onFinished.run());
        fadeOut.play();
    }
}
