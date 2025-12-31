package com.mycompany.client_xo_game;

import javafx.fxml.FXML;
import com.mycompany.client_xo_game.navigation.Navigation;
import com.mycompany.client_xo_game.navigation.Routes;

public class Level_SelectionController {

    @FXML
    private void easy() {
        goToGameboard("EASY");
    }

    @FXML
    private void medium() {
        goToGameboard("MEDIUM");
    }

    @FXML
    private void hard() {
        goToGameboard("HARD");
    }

    private void goToGameboard(String level) {
        GameboardController controller
                = Navigation.loadController(Routes.GAMEBOARD);

        controller.setLevel(level);
        Navigation.goTo(Routes.GAMEBOARD);
    }

    @FXML
    private void goBack() {
        Navigation.goTo(Routes.MODE_SELECTION);
    }
}
