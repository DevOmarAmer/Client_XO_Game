package com.mycompany.client_xo_game;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.IOException;
import java.util.Optional;

public class App extends Application {

    private static Scene scene;
    private static Stage stage;

    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void start(Stage stage) throws IOException {
        App.stage = stage;

        stage.initStyle(StageStyle.UNDECORATED);
        scene = new Scene(loadFXML("Splash"), 1400, 800);

        // --- SAFELY LOAD CSS ---
        // Looks for file in src/main/resources/styles/styles.css
        var cssUrl = getClass().getResource("/styles/styles.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        } else {
            System.out.println("⚠️ WARNING: Could not find styles.css in /styles/ folder.");
        }

        setupGlobalKeys();
        setupWindowDragging();

        stage.setScene(scene);
        stage.setTitle("XO Game");
        stage.show();
    }

    private void setupGlobalKeys() {
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.F11) {
                stage.setFullScreen(!stage.isFullScreen());
                event.consume();
            } else if (event.getCode() == KeyCode.ESCAPE) {
                showExitAlert();
                event.consume();
            }
        });
    }

    private void setupWindowDragging() {
        scene.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        scene.setOnMouseDragged(event -> {
            if (!stage.isFullScreen()) {
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            }
        });
    }

    // --- EXIT ALERT WITH CUSTOM THEME ---
    public static void showExitAlert() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit Game");
        alert.setHeaderText("EXIT GAME?");
        alert.setContentText("Are you sure you want to quit?");

        alert.initStyle(StageStyle.UNDECORATED);
        alert.initOwner(stage);

        ButtonType buttonExit = new ButtonType("EXIT");
        ButtonType buttonStay = new ButtonType("STAY");
        alert.getButtonTypes().setAll(buttonExit, buttonStay);

        // --- APPLY CSS ID TO ALERT ---
        DialogPane dialogPane = alert.getDialogPane();
        // This ID matches the "#xo-alert" in your CSS file
        dialogPane.setId("xo-alert");

        // Add the stylesheet to the alert explicitly
        var cssUrl = App.class.getResource("/styles/styles.css");
        if (cssUrl != null) {
            dialogPane.getStylesheets().add(cssUrl.toExternalForm());
        }

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == buttonExit) {
            Platform.exit();
            System.exit(0);
        }
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return loader.load();
    }

    public static Scene getScene() {
        return scene;
    }

    public static Stage getStage() {
        return stage;
    }

    public static void main(String[] args) {
        launch();
    }
}
