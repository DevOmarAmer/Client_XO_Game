package com.mycompany.client_xo_game;

import com.mycompany.client_xo_game.navigation.Navigation;
import com.mycompany.client_xo_game.navigation.Routes;
import javafx.fxml.FXML;
import javafx.scene.Parent; // Import Parent or the specific layout you use (e.g., VBox)
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane; // Generic pane
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

import java.net.URL;
import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.util.Duration;
import org.json.JSONObject;

public class GameOverDialogController {

    // 1. ADD THIS: Reference to the root node (e.g., VBox or AnchorPane) in your FXML
    // Make sure to add fx:id="rootPane" to the top-level element in GameOverDialog.fxml
    @FXML
    private Pane rootPane;

    @FXML
    private Label headerLabel;

    @FXML
    private Label messageLabel;

    @FXML
    private MediaView mediaView;

    @FXML
    private Button playAgainBtn;

    @FXML
    private Button closeBtn;

    private MediaPlayer mediaPlayer;
    private Stage dialogStage;

    // Variables for Dragging
    private double xOffset = 0;
    private double yOffset = 0;

    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
        // 2. ENABLE DRAGGING: Call this when stage is set
        makeDraggable();
    }

    private void makeDraggable() {
        if (rootPane != null && dialogStage != null) {
            rootPane.setOnMousePressed(event -> {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            });
            rootPane.setOnMouseDragged(event -> {
                dialogStage.setX(event.getScreenX() - xOffset);
                dialogStage.setY(event.getScreenY() - yOffset);
            });
        }
    }

    public void initData(boolean won, boolean draw, boolean isForfeit, String opponentName) {
        String videoFile = "/assets/win.mp4";
        if (isForfeit && won) {
            headerLabel.setText("Opponent Forfeited!");
            messageLabel.setText(opponentName + " forfeited. You win!");
            videoFile = "/assets/win.mp4";
        } else if (draw) {
            headerLabel.setText("It's a Draw!");
            messageLabel.setText("Would you like to play again?");
            videoFile = "/assets/draw.mp4";
        } else if (won) {
            headerLabel.setText("You Win!");
            messageLabel.setText("Would you like to play again?");
            videoFile = "/assets/win.mp4";
        } else if (isForfeit) {
            headerLabel.setText("You Lost by Forfeit!");
            messageLabel.setText("You forfeited. Would you like to play again?");
            videoFile = "/assets/lose.mp4";
        } else {
            headerLabel.setText("You Lost!");
            messageLabel.setText("Would you like to play again?");
            videoFile = "/assets/lose.mp4";
        }

        try {
            URL videoUrl = getClass().getResource(videoFile);
            if (videoUrl != null) {
                Media media = new Media(videoUrl.toExternalForm());
                mediaPlayer = new MediaPlayer(media);
                mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                mediaPlayer.play();
                mediaView.setMediaPlayer(mediaPlayer);
            } else {
                System.err.println("Video not found: " + videoFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        playAgainBtn.setOnAction(e -> {
            JSONObject playAgain = new JSONObject();
            playAgain.put("type", "play_again");
            NetworkConnection.getInstance().sendMessage(playAgain);
            stopVideo();
            dialogStage.close();
        });

        closeBtn.setOnAction(e -> {
            JSONObject disconnect = new JSONObject();
            disconnect.put("type", "end_session");
            NetworkConnection.getInstance().sendMessage(disconnect);
            System.out.println("----------No Penalty (Game Already Ended)--------------");
            stopVideo();
            dialogStage.close();
            // 3. FIX TRANSITION: Pass the rootPane
            playExitTransition(() -> Navigation.goTo(Routes.ONLINE_PLAYERS));
        });
    }

    private void playExitTransition(Runnable onFinished) {
        // FIXED: Transition needs a target Node. Used rootPane.
        if (rootPane != null) {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), rootPane);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(e -> onFinished.run());
            fadeOut.play();
        } else {
            onFinished.run(); // Fallback if rootPane is missing
        }
    }

    private void stopVideo() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }
}
