package com.mycompany.client_xo_game.navigation;

import com.mycompany.client_xo_game.App;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class Navigation {

    public static void goTo(String route) {
        try {
            App.setRoot(route);
        } catch (IOException e) {
            throw new RuntimeException("Failed to navigate to: " + route, e);
        }
    }

    public static <T> T loadController(String route) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource(route + ".fxml"));
            loader.load();
            return loader.getController();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load controller for: " + route, e);
        }
    }

    public static <T> T loadAndGoTo(String route) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource(route + ".fxml"));
            Parent root = loader.load();
            App.setRoot(route);
            return loader.getController();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load and navigate to: " + route, e);
        }
    }

    public static void openModal(String route, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource(route + ".fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(title == null ? "" : title);
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            throw new RuntimeException("Failed to open modal: " + route, e);
        }
    }
}
