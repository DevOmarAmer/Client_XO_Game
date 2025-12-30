package com.mycompany.client_xo_game;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class OnlinePlayersController {

    @FXML
    private ListView<String> playersList;

    @FXML
    public void initialize() {
        // 🔹 MOCK ONLINE PLAYERS
        playersList.getItems().addAll(
                "Ahmed 🟢",
                "Omar 🟢",
                "Sara 🟢",
                "Mona 🟢",
                "Youssef 🟢"
        );
    }
}
