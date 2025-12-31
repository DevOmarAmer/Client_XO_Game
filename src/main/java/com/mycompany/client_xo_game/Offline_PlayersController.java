package com.mycompany.client_xo_game;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import com.mycompany.client_xo_game.navigation.Navigation;
import com.mycompany.client_xo_game.navigation.Routes;

public class Offline_PlayersController {

    @FXML
    private TextField player_one_id;
    @FXML
    private TextField Player_two_id;

    @FXML
    private void goBack() {
        Navigation.goTo(Routes.MODE_SELECTION);
    }

    @FXML
    private void playGame() {
        String p1 = player_one_id.getText();
        String p2 = Player_two_id.getText();

        if (p1.isEmpty() || p2.isEmpty()) {
            System.out.println("Both players must enter names");
            return;
        }

        System.out.println("Offline game: " + p1 + " vs " + p2);
        // Next: Navigation.goTo(GameBoard);
    }
}
