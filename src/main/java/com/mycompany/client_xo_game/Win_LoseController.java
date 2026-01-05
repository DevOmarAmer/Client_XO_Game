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
    private boolean isWin;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
       
    }
    
    public void setResult(boolean won) {
        isWin = won;
        if (won) {
            stateText.setText("You Won !!!!!");

        } else {
            stateText.setText("You Lost :(");

        }
    }
    
    public void setResultDraw() {
        stateText.setText("It's a Draw!");

    }
    
    public void setResultLocalMode(String winnerName) {
        stateText.setText(winnerName + " Wins!");

    }

    
    @FXML
    private void onCloseButtonPressed(ActionEvent event) {
      
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
     
        GameMode mode = GameSession.getGameMode();
        if (mode == GameMode.HUMAN_VS_COMPUTER_MODE) {
            Navigation.goTo(Routes.LEVEL_SELECTION);
        } else {
            Navigation.goTo(Routes.MODE_SELECTION);
        }
    }
    
    @FXML
    private void onPlayAgainButtonPressed(ActionEvent event) {

        Stage stage = (Stage) playAgainButton.getScene().getWindow();
        stage.close();
        
     
        Navigation.goTo(Routes.GAMEBOARD);
    }
    

}