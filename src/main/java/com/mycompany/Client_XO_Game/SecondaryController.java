package com.mycompany.Client_XO_Game;

import com.mycompany.Client_XO_Game.Client_XO_Game;
import java.io.IOException;
import javafx.fxml.FXML;

public class SecondaryController {

    @FXML
    private void switchToPrimary() throws IOException {
        Client_XO_Game.setRoot("primary");
    }
}