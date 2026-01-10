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
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.IOException;
import java.util.Optional;

public class App extends Application {

    private static Scene scene;
    private static Stage stage;

    // --- AUDIO SYSTEM ---
    private static MediaPlayer backgroundMusicPlayer;
    private static boolean isMuted = false;
    private static double lastVolume = 0.3;

    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void start(Stage stage) throws IOException {
        App.stage = stage;

        stage.initStyle(StageStyle.UNDECORATED);
        scene = new Scene(loadFXML("Splash"), 1400, 800);

        // Load CSS
        var cssUrl = getClass().getResource("/styles/styles.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        } else {
            System.out.println("⚠️ WARNING: Could not find styles.css");
        }

        setupGlobalKeys();
        setupWindowDragging();

        // Start Music
        playBackgroundMusic();

        stage.setScene(scene);
        stage.setTitle("XO Game");
        stage.show();
    }

    // --- AUDIO LOGIC ---
    private void playBackgroundMusic() {
        try {
            var musicFile = getClass().getResource("/assets/game_sound.mp3");
            if (musicFile != null) {
                Media media = new Media(musicFile.toExternalForm());
                backgroundMusicPlayer = new MediaPlayer(media);
                backgroundMusicPlayer.setVolume(lastVolume);
                backgroundMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                backgroundMusicPlayer.play();
            } else {
                System.out.println("⚠️ WARNING: Could not find /sounds/game_music.mp3");
            }
        } catch (Exception e) {
            System.out.println("❌ Error loading music: " + e.getMessage());
        }
    }

    public static void setVolume(double volume) {
        if (backgroundMusicPlayer != null) {
            backgroundMusicPlayer.setVolume(volume);
            if (volume > 0) {
                isMuted = false;
                lastVolume = volume;
            }
        }
    }

    public static boolean toggleMute() {
        if (backgroundMusicPlayer == null) {
            return false;
        }

        isMuted = !isMuted;
        if (isMuted) {
            lastVolume = backgroundMusicPlayer.getVolume();
            backgroundMusicPlayer.setMute(true);
        } else {
            backgroundMusicPlayer.setMute(false);
            backgroundMusicPlayer.setVolume(lastVolume);
        }
        return isMuted;
    }

    public static double getVolume() {
        return (backgroundMusicPlayer != null && !isMuted) ? backgroundMusicPlayer.getVolume() : 0.0;
    }

    public static boolean isMuted() {
        return isMuted;
    }

    // --- WINDOW & NAV LOGIC ---
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

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setId("xo-alert");

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
