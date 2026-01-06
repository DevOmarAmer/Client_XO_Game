module com.mycompany.client_xo_game {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;
    requires javafx.media;
    opens com.mycompany.client_xo_game to javafx.fxml;
    exports com.mycompany.client_xo_game;
    requires org.json;
    requires com.google.gson;
    requires jakarta.json;
     // Open packages to JavaFX for FXML reflection

    opens com.mycompany.client_xo_game.navigation to javafx.fxml;
    
    
    opens com.mycompany.client_xo_game.model to com.google.gson;
}
