/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.client_xo_game;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;

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

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void onCloseButtonPressed(ActionEvent event) {
    }

    @FXML
    private void onPlayAgainButtonPressed(ActionEvent event) {
    }
    
}
