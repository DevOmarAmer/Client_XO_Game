module com.mycompany.client_xo_game {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;
    requires javafx.media;
    opens com.mycompany.client_xo_game to javafx.fxml;
    exports com.mycompany.client_xo_game;
    requires org.json;
     requires com.google.gson;
     // Open packages to JavaFX for FXML reflection

    opens com.mycompany.client_xo_game.navigation to javafx.fxml;
    
    // CRITICAL: Open model package to Gson for JSON serialization
    // Without this line, Gson cannot access private fields in GameRecord and MoveRecord
    opens com.mycompany.client_xo_game.model to com.google.gson;
}
