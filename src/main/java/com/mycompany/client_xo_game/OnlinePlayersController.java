package com.mycompany.client_xo_game;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import com.mycompany.client_xo_game.navigation.Navigation;
import com.mycompany.client_xo_game.navigation.Routes;

public class OnlinePlayersController {

    @FXML
    private StackPane rootPane;
    @FXML
    private VBox contentBox;
    @FXML
    private ListView<String> playersList;

    @FXML
    public void initialize() {
        // 1. Fade In Animation
        rootPane.setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(1000), rootPane);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        // 2. Mock Data (Styled with circle emojis for status)
        playersList.getItems().addAll(
                "Ahmed   🟢 Online",
                "Omar    🔴 In Game",
                "Sara    🟢 Online",
                "Mona    🟡 Away",
                "Youssef 🟢 Online",
                "Hassan  🔴 In Game",
                "Laila   🟢 Online"
        );

        // 3. Responsive Scaling Logic
        // Ensures the glass panel doesn't get too small or too big
        rootPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            double w = newVal.doubleValue();
            contentBox.setMaxWidth(Math.max(400, w * 0.6));
        });

        rootPane.heightProperty().addListener((obs, oldVal, newVal) -> {
            double h = newVal.doubleValue();
            contentBox.setMaxHeight(Math.max(400, h * 0.8));
        });
    }

    private void playExitTransition(Runnable onFinished) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), rootPane);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> onFinished.run());
        fadeOut.play();
    }

    @FXML
    private void goBack() {
        // Assuming you want to go back to a Mode Selection or Main Menu
        playExitTransition(() -> Navigation.goTo(Routes.MODE_SELECTION));
    }

    @FXML
    private void goToReplays() {
        playExitTransition(() -> Navigation.goTo(Routes.GAME_REPLAYS));
        // Ensure you have Routes.GAME_REPLAYS defined in your Routes enum
    }

    @FXML
    private void goToLeaderboard() {
        playExitTransition(() -> Navigation.goTo(Routes.LEADERBOARD));
        // Ensure you have Routes.LEADERBOARD defined in your Routes enum
    }
}
