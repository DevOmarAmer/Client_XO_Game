package com.mycompany.client_xo_game;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import com.mycompany.client_xo_game.navigation.Navigation;
import com.mycompany.client_xo_game.navigation.Routes;

public class Game_replaysController {

    @FXML
    private StackPane rootPane;
    @FXML
    private VBox contentBox;
    @FXML
    private ListView<String> replaysList;

    @FXML
    public void initialize() {
        // 1. Animation
        rootPane.setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(800), rootPane);
        fadeIn.setToValue(1);
        fadeIn.play();

        // 2. Mock Data
        replaysList.getItems().addAll(
                "Vs Ahmed - 10:30 AM - WIN 🏆",
                "Vs Sara  - 11:15 AM - LOSS ❌",
                "Vs Omar  - Yesterday - DRAW ➖",
                "Vs Computer (Hard) - Mon - WIN 🏆"
        );

        // 3. Responsive Scaling
        rootPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            double w = newVal.doubleValue();
            contentBox.setMaxWidth(Math.max(600, w * 0.6));
        });
    }

    @FXML
    private void handleWatch() {
        String selected = replaysList.getSelectionModel().getSelectedItem();
        if (selected != null) {
            System.out.println("Loading replay for: " + selected);
            // Logic to load gameboard in replay mode would go here
        }
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
