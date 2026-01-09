package com.mycompany.client_xo_game;

import com.mycompany.client_xo_game.navigation.Navigation;
import com.mycompany.client_xo_game.navigation.Routes;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

import java.net.URL;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import org.json.JSONObject;

public class GameOverDialogController {

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

    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
    }

    public void initData(boolean won, boolean draw, boolean isForfeit, String opponentName) {
    // Set header and message
    String videoFile = "/assets/win.mp4";
    if (isForfeit && won) {
        headerLabel.setText("Opponent Forfeited!");
        messageLabel.setText(opponentName + " forfeited. You win!");
        videoFile = "/assets/win.mp4"; // Winning video
    } else if (draw) {
        headerLabel.setText("It's a Draw!");
        messageLabel.setText("Would you like to play again?");
        videoFile = "/assets/draw.mp4"; // Draw video
    } else if (won) {
        headerLabel.setText("You Win!");
        messageLabel.setText("Would you like to play again?");
        videoFile = "/assets/win.mp4"; // Winning video
    } else if (isForfeit) {
        headerLabel.setText("You Lost by Forfeit!");
        messageLabel.setText("You forfeited. Would you like to play again?");
        videoFile = "/assets/lose.mp4"; // Losing video
    } else {
        headerLabel.setText("You Lost!");
        messageLabel.setText("Would you like to play again?");
        videoFile = "/assets/lose.mp4"; // Losing video
    }

    // Load video
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

    // Button actions remain the same
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
        playExitTransition(() -> Navigation.goTo(Routes.ONLINE_PLAYERS));
    });
}
    private void playExitTransition(Runnable onFinished) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300));
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> onFinished.run());
        fadeOut.play();
    }


    private void stopVideo() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }
}
