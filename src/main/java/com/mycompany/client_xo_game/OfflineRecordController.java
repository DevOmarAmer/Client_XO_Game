package com.mycompany.client_xo_game;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import com.mycompany.client_xo_game.navigation.Navigation;
import com.mycompany.client_xo_game.navigation.Routes;

public class OfflineRecordController {

    @FXML
    private StackPane rootPane;
    @FXML
    private ListView<String> recordsList;

    @FXML
    public void initialize() {
        // Animation (Same as Replays)
        rootPane.setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(800), rootPane);
        fadeIn.setToValue(1);
        fadeIn.play();

        // Mock Data for Offline
        recordsList.getItems().addAll(
                "Vs Computer (Easy) - 09:00 AM - WIN 🏆",
                "Vs Computer (Hard) - 10:15 AM - LOSS ❌",
                "Vs Player 2 (Local) - Yesterday - DRAW ➖",
                "Vs Computer (Medium) - Sunday - WIN 🏆"
        );
    }

    @FXML
    private void handleViewRecord() {
        String selected = recordsList.getSelectionModel().getSelectedItem();
        if (selected != null) {
            System.out.println("Loading offline record for: " + selected);
            // Logic to load offline game board
        }
    }

    @FXML
    private void goBack() {
        // IMPORTANT: Change 'Routes.OFFLINE_MENU' to whatever your Offline Mode menu is named
        playExitTransition(() -> Navigation.goTo(Routes.MODE_SELECTION)); 
    }

    private void playExitTransition(Runnable onFinished) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), rootPane);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> onFinished.run());
        fadeOut.play();
    }
}
