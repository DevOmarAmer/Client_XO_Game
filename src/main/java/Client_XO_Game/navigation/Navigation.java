package Client_XO_Game.navigation;

import com.mycompany.client_xo_game.App;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class Navigation {

    public static void goTo(String fxml) {
        try {
            App.setRoot(fxml);
        } catch (IOException e) {
            throw new RuntimeException("Failed to navigate to: " + fxml, e);
        }
    }

    public static <T> T loadController(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
            loader.load();
            return loader.getController();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load controller for: " + fxml, e);
        }
    }

    public static void openModal(String fxml, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(title == null ? "" : title);
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            throw new RuntimeException("Failed to open modal: " + fxml, e);
        }
    }
}
