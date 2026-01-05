package com.mycompany.client_xo_game;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import com.mycompany.client_xo_game.navigation.Navigation;
import com.mycompany.client_xo_game.navigation.Routes;

public class LeaderboardController {

    @FXML
    private StackPane rootPane;
    @FXML
    private ListView<String> scoreList;

    @FXML
    public void initialize() {
        // Animation
        rootPane.setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(800), rootPane);
        fadeIn.setToValue(1);
        fadeIn.play();

        // Mock Data with Medals
        scoreList.getItems().addAll(
                "🥇  1.  MasterYoda        2500 pts",
                "🥈  2.  Skywalker         2350 pts",
                "🥉  3.  ObiWan            2100 pts",
                "      4.  DarthV            1900 pts",
                "      5.  R2D2              1800 pts",
                "      6.  C3PO              1500 pts",
                "      7.  Chewie            1200 pts"
        );
    }

    @FXML
    private void goBack() {
        // Return to Online Players
        playExitTransition(() -> Navigation.goTo(Routes.ONLINE_PLAYERS));
    }

    private void playExitTransition(Runnable onFinished) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), rootPane);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> onFinished.run());
        fadeOut.play();
    }
}
