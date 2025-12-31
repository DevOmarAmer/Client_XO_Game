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

    public void setLevel(String level) {
        this.level = level;
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
        System.out.println("Game started on level: " + level);

        if (level != null) {
            turnLabel.setText(level + " MODE");
        }
    }

    @FXML
    private void goBack() {
        Navigation.goTo(Routes.MODE_SELECTION);
    }
}
