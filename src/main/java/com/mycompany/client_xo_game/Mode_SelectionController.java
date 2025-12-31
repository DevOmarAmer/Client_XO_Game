package com.mycompany.client_xo_game;

import javafx.fxml.FXML;
import com.mycompany.client_xo_game.navigation.Navigation;
import com.mycompany.client_xo_game.navigation.Routes;

public class Mode_SelectionController {

    @FXML
    private void goToLevelSelection() {
        Navigation.goTo(Routes.LEVEL_SELECTION);
    }

    @FXML
    private void goToOfflinePlayers() {
        Navigation.goTo(Routes.OFFLINE_PLAYERS);
    }

    @FXML
    private void goToLogin() {
        Navigation.goTo(Routes.LOGIN);
    }
}
