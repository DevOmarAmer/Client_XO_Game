module com.mycompany.client_xo_game {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.mycompany.client_xo_game to javafx.fxml;
    exports com.mycompany.client_xo_game;
}
