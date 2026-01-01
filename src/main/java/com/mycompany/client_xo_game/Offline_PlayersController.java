package com.mycompany.client_xo_game;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import com.mycompany.client_xo_game.navigation.Navigation;
import com.mycompany.client_xo_game.navigation.Routes;

public class Offline_PlayersController {

    @FXML private StackPane rootPane;
    @FXML private VBox contentBox;
    @FXML private VBox inputContainer;
    @FXML private TextField player_one_id;
    @FXML private TextField Player_two_id;
    @FXML private Button back_id;
    @FXML private Button play_id;
    @FXML private Label titleLabel;

    @FXML
    public void initialize() {

        // Responsive scaling based on window width
        rootPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            double w = newVal.doubleValue();

            // TITLE font scaling
            double titleFont = Math.min(48, Math.max(26, w / 20));
            titleLabel.setStyle("-fx-font-size: " + titleFont + "px;");

            // TextField font scaling
            double inputFont = Math.max(12, w / 50);
            player_one_id.setStyle("-fx-font-size: " + inputFont + "px;");
            Player_two_id.setStyle("-fx-font-size: " + inputFont + "px;");

            // TextField height scaling
            double inputHeight = Math.max(36, w / 20);
            player_one_id.setPrefHeight(inputHeight);
            Player_two_id.setPrefHeight(inputHeight);

            // Button font scaling
            double btnFont = Math.max(14, w / 50);
            back_id.setStyle("-fx-font-size: " + btnFont + "px;");
            play_id.setStyle("-fx-font-size: " + btnFont + "px;");

            // Button height scaling
            double btnHeight = Math.max(36, w / 20);
            back_id.setPrefHeight(btnHeight);
            play_id.setPrefHeight(btnHeight);

            // VBox & HBox spacing scaling
            contentBox.setSpacing(w * 0.04);
            inputContainer.setSpacing(w * 0.03);
            ((HBox) back_id.getParent()).setSpacing(w * 0.03);
        });

        // Height-based scaling (optional)
        rootPane.heightProperty().addListener((obs, oldVal, newVal) -> {
            double h = newVal.doubleValue();
            contentBox.setMaxHeight(h * 0.85);
        });
    }

    @FXML
    private void goBack() {
        Navigation.goTo(Routes.MODE_SELECTION);
    }

    @FXML
    private void playGame() {
        String p1 = player_one_id.getText().trim();
        String p2 = Player_two_id.getText().trim();

        if (p1.isEmpty() || p2.isEmpty()) {
            System.out.println("Both players must enter names");
            return;
        }

        System.out.println("Offline game: " + p1 + " vs " + p2);

        GameboardController controller = Navigation.loadAndGoTo(Routes.GAMEBOARD);
        controller.setPlayerNames(p1, p2);
    }
}
