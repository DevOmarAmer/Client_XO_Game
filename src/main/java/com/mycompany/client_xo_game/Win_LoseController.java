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
    
   
 
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        currentMode = GameSession.getGameMode();
    }
    
   
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    

    public void show() {
        if (stage != null) {
            stage.showAndWait();
        }
    }
 
    public void setResult(boolean won) {
        System.out.println("setResult called with won=" + won); 
        
        if (won) {
            stateText.setText("You Won !!!!!");
            stateText.getStyleClass().clear();
            stateText.getStyleClass().add("win-text");
//            playWinMedia();
        } else {
            stateText.setText("You Lost :(");
            stateText.getStyleClass().clear();
            stateText.getStyleClass().add("lose-text");
//            playLoseMedia();
        }
        

        show();
    }
   
  
    public void setResultDraw() {
        System.out.println("setResultDraw called"); 
        
        stateText.setText("It's a Draw!");
        stateText.getStyleClass().clear();
        stateText.getStyleClass().add("draw-text");
//        playDrawMedia();
  
        show();
    }
    
   
    public void setResultLocalMode(String winnerName) {
        System.out.println("setResultLocalMode called with winner=" + winnerName); // Debug
        
        stateText.setText(winnerName + " Wins!");
        stateText.getStyleClass().clear();
        stateText.getStyleClass().add("win-text");
//        playWinMedia();
        
   
        show();
    }
   
//    private void playWinMedia() {
//        try {
//            stopCurrentMedia();
//            String mediaPath = getClass().getResource("/assets/win.mp4").toExternalForm();
//            Media media = new Media(mediaPath);
//            mediaPlayer = new MediaPlayer(media);
//            mediaView.setMediaPlayer(mediaPlayer);
//            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
//            mediaPlayer.play();
//        } catch (Exception e) {
//            System.out.println("Win media not found (this is okay for now): " + e.getMessage());
//        }
//    }
    
  
//    private void playLoseMedia() {
//        try {
//            stopCurrentMedia();
//            String mediaPath = getClass().getResource("/assets/lose.mp4").toExternalForm();
//            Media media = new Media(mediaPath);
//            mediaPlayer = new MediaPlayer(media);
//            mediaView.setMediaPlayer(mediaPlayer);
//            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
//            mediaPlayer.play();
//        } catch (Exception e) {
//            System.out.println("Lose media not found (this is okay for now): " + e.getMessage());
//        }
//    }
    
  
//    private void playDrawMedia() {
//        try {
//            stopCurrentMedia();
//            String mediaPath = getClass().getResource("/assets/draw.mp4").toExternalForm();
//            Media media = new Media(mediaPath);
//            mediaPlayer = new MediaPlayer(media);
//            mediaView.setMediaPlayer(mediaPlayer);
//            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
//            mediaPlayer.play();
//        } catch (Exception e) {
//            System.out.println("Draw media not found (this is okay for now): " + e.getMessage());
//        }
//    }
    
   
//    private void stopCurrentMedia() {
//        if (mediaPlayer != null) {
//            mediaPlayer.stop();
//            mediaPlayer.dispose();
//            mediaPlayer = null;
//        }
//    }
    
    @FXML
    private void onCloseButtonPressed(ActionEvent event) {
//        stopCurrentMedia();
        
        if (stage != null) {
            stage.close();
        } else {
            Stage stg = (Stage) closeButton.getScene().getWindow();
            stg.close();
        }
     
        GameMode mode = GameSession.getGameMode();
        GameSession.clearSession();
        Navigation.goTo(Routes.MODE_SELECTION);
    }
    
    @FXML
    private void onPlayAgainButtonPressed(ActionEvent event) {
//        stopCurrentMedia();
        
        if (stage != null) {
            stage.close();
        } else {
            Stage stg = (Stage) playAgainButton.getScene().getWindow();
            stg.close();
        }
        
        Navigation.goTo(Routes.GAMEBOARD);
    }
}