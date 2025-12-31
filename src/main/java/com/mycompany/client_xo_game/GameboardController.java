package com.mycompany.client_xo_game;

import com.mycompany.client_xo_game.navigation.Navigation;
import com.mycompany.client_xo_game.navigation.Routes;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class GameboardController implements Initializable {

    @FXML
    private BorderPane rootPane;
    @FXML
    private Label turnLabel;
    @FXML
    private HBox playersBar;
    @FXML
    private Label scoreP1;
    @FXML
    private Label scoreP2;
    @FXML
    private StackPane boardContainer;
    @FXML
    private GridPane board;

    private String level;
    private String player1;
    private String player2;

    public void setLevel(String level) {
        this.level = level;
    }

    public void setPlayerNames(String p1, String p2) {
        this.player1 = p1;
        this.player2 = p2;
        updatePlayerLabels();
    }

    private void updatePlayerLabels() {
        if (scoreP1 != null && scoreP2 != null) {
            scoreP1.setText(player1 + " Score: 0");
            scoreP2.setText(player2 + " Score: 0");
            turnLabel.setText(player1 + " Turn");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        rootPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                onSceneShown();
            }
        });
    }

    private void onSceneShown() {
        if (level != null) {
            turnLabel.setText(level + " MODE");
        } else if (player1 != null && player2 != null) {
            scoreP1.setText(player1 + " Score: 0");
            scoreP2.setText(player2 + " Score: 0");
            turnLabel.setText(player1 + "'s Turn");
        }
    }

    @FXML
    private void goBack() {
        Navigation.goTo(Routes.MODE_SELECTION);
    }
}
