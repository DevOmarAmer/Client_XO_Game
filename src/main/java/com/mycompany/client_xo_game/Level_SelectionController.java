package com.mycompany.client_xo_game;

import javafx.fxml.FXML;
import com.mycompany.client_xo_game.navigation.Navigation;
import com.mycompany.client_xo_game.navigation.Routes;

public class Level_SelectionController {

    @FXML
    private void easy() {
        System.out.println("Easy level selected");
    }

    @FXML
    private void medium() {
        System.out.println("Medium level selected");
    }

    @FXML
    private void hard() {
        System.out.println("Hard level selected");
    }

    @FXML
    private void goBack() {
        Navigation.goTo(Routes.MODE_SELECTION);
    }
}
