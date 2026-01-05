package com.mycompany.client_xo_game;

import com.mycompany.client_xo_game.enums.GameMode;
import com.mycompany.client_xo_game.model.GameSession;
import com.mycompany.client_xo_game.navigation.Navigation;
import com.mycompany.client_xo_game.navigation.Routes;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Alaa
 */
public class Win_LoseController implements Initializable {
    
    @FXML
    private Text stateText;
    @FXML
    private Button closeButton;
    @FXML
    private Button playAgainButton;
    @FXML
    private MediaView mediaView;
    
    private MediaPlayer mediaPlayer;
    private GameMode currentMode;
    private Stage stage;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        currentMode = GameSession.getGameMode();
    }
    
    /**
     * Set the stage for this controller (called by Navigation)
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    /**
     * Show the modal window (call this after configuring the controller)
     */
    public void show() {
        if (stage != null) {
            stage.showAndWait();
        }
    }
    
    /**
     * Set result for Human vs Computer mode - Win or Lose
     * @param won true if player won, false if player lost
     */
    public void setResult(boolean won) {
        System.out.println("setResult called with won=" + won); // Debug
        
        if (won) {
            stateText.setText("You Won !!!!!");
            stateText.getStyleClass().clear();
            stateText.getStyleClass().add("win-text");
            playWinMedia();
        } else {
            stateText.setText("You Lost :(");
            stateText.getStyleClass().clear();
            stateText.getStyleClass().add("lose-text");
            playLoseMedia();
        }
        
        // Show the stage after setting the content
        show();
    }
   
    /**
     * Set result for Draw (both modes)
     */
    public void setResultDraw() {
        System.out.println("setResultDraw called"); // Debug
        
        stateText.setText("It's a Draw!");
        stateText.getStyleClass().clear();
        stateText.getStyleClass().add("draw-text");
        playDrawMedia();
        
        // Show the stage after setting the content
        show();
    }
    
    /**
     * Set result for Local Mode - displays winner name
     * @param winnerName name of the winning player
     */
    public void setResultLocalMode(String winnerName) {
        System.out.println("setResultLocalMode called with winner=" + winnerName); // Debug
        
        stateText.setText(winnerName + " Wins!");
        stateText.getStyleClass().clear();
        stateText.getStyleClass().add("win-text");
        playWinMedia();
        
        // Show the stage after setting the content
        show();
    }
    
    /**
     * Play winning animation/video
     */
    private void playWinMedia() {
        try {
            stopCurrentMedia();
            String mediaPath = getClass().getResource("/assets/win.mp4").toExternalForm();
            Media media = new Media(mediaPath);
            mediaPlayer = new MediaPlayer(media);
            mediaView.setMediaPlayer(mediaPlayer);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            mediaPlayer.play();
        } catch (Exception e) {
            System.out.println("Win media not found (this is okay for now): " + e.getMessage());
        }
    }
    
    /**
     * Play losing animation/video
     */
    private void playLoseMedia() {
        try {
            stopCurrentMedia();
            String mediaPath = getClass().getResource("/assets/lose.mp4").toExternalForm();
            Media media = new Media(mediaPath);
            mediaPlayer = new MediaPlayer(media);
            mediaView.setMediaPlayer(mediaPlayer);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            mediaPlayer.play();
        } catch (Exception e) {
            System.out.println("Lose media not found (this is okay for now): " + e.getMessage());
        }
    }
    
    /**
     * Play draw animation/video
     */
    private void playDrawMedia() {
        try {
            stopCurrentMedia();
            String mediaPath = getClass().getResource("/assets/draw.mp4").toExternalForm();
            Media media = new Media(mediaPath);
            mediaPlayer = new MediaPlayer(media);
            mediaView.setMediaPlayer(mediaPlayer);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            mediaPlayer.play();
        } catch (Exception e) {
            System.out.println("Draw media not found (this is okay for now): " + e.getMessage());
        }
    }
    
    /**
     * Stop currently playing media
     */
    private void stopCurrentMedia() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
        }
    }
    
    @FXML
    private void onCloseButtonPressed(ActionEvent event) {
        stopCurrentMedia();
        
        if (stage != null) {
            stage.close();
        } else {
            Stage stg = (Stage) closeButton.getScene().getWindow();
            stg.close();
        }
     
        GameMode mode = GameSession.getGameMode();
        GameSession.clearSession();
        if (mode == GameMode.HUMAN_VS_COMPUTER_MODE) {
            Navigation.goTo(Routes.LEVEL_SELECTION);
        } else {
            Navigation.goTo(Routes.MODE_SELECTION);
        }
    }
    
    @FXML
    private void onPlayAgainButtonPressed(ActionEvent event) {
        stopCurrentMedia();
        
        if (stage != null) {
            stage.close();
        } else {
            Stage stg = (Stage) playAgainButton.getScene().getWindow();
            stg.close();
        }
        
        Navigation.goTo(Routes.GAMEBOARD);
    }
}