///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
// */
//package com.mycompany.client_xo_game;
//
//
//import com.mycompany.client_xo_game.model.GameSession;
//import javafx.scene.control.Alert;
//import javafx.scene.control.ButtonBar;
//import javafx.scene.control.ButtonType;
//import javafx.scene.control.CheckBox;
//import org.json.JSONObject;
//import java.net.URL;
//import java.util.ResourceBundle;
//import javafx.fxml.Initializable;
//
//import javafx.fxml.FXML;
//import javafx.scene.control.Alert;
//import javafx.scene.control.Button;
//import javafx.scene.control.ButtonBar;
//import javafx.scene.control.ButtonType;
//import javafx.scene.control.CheckBox;
//import javafx.scene.control.Label;
//import javafx.stage.Stage;
//import org.json.JSONObject;
//import com.mycompany.client_xo_game.model.GameSession;
//
///**
// * FXML Controller class
// *
// * @author Alaa
// */
//public class InvitieDialogController implements Initializable {
//
//     @FXML
//    private Label inviteLabel;
//    @FXML
//    private CheckBox recordGameCheckbox;
//    @FXML
//    private Button sendButton;
//    @FXML
//    private Button cancelButton;
//    
//    private String opponentName;
//    private String myUsername;
//    private Stage dialogStage;
//    @Override
//    public void initialize(URL url, ResourceBundle rb) {
//        // TODO
//    }    
//    public void setOpponentInfo(String opponent, String username) {
//        this.opponentName = opponent;
//        this.myUsername = username;
//        if (inviteLabel != null) {
//            inviteLabel.setText("Send game invitation to " + opponent);
//        }
//    }
//    
//    public void setDialogStage(Stage stage) {
//        this.dialogStage = stage;
//    }
//    
//    @FXML
//    private void handleSendInvite() {
//        boolean recordGame = recordGameCheckbox.isSelected();
//        
//        // Send invitation to opponent with recording preference
//        JSONObject invite = new JSONObject();
//        invite.put("type", "game_invite");
//        invite.put("to", opponentName);
//        invite.put("from", myUsername);
//        invite.put("recordGame", recordGame);
//        
//        NetworkConnection.getInstance().sendMessage(invite);
//        
//        System.out.println("Invitation sent to " + opponentName + 
//                          (recordGame ? " with recording enabled" : ""));
//        
//        if (dialogStage != null) {
//            dialogStage.close();
//        }
//    }
//    
//    @FXML
//    private void handleCancel() {
//        if (dialogStage != null) {
//            dialogStage.close();
//        }
//    }
//}
//
//
//class OnlineInviteReceivedController {
//    
//    @FXML
//    private Label messageLabel;
//    @FXML
//    private CheckBox recordGameCheckbox;
//    @FXML
//    private Button acceptButton;
//    @FXML
//    private Button declineButton;
//    
//    private String inviterName;
//    private String myUsername;
//    private boolean inviterWantsRecording;
//    private Stage dialogStage;
//    
//    public void setInviteInfo(String inviter, String username, boolean recordingRequested) {
//        this.inviterName = inviter;
//        this.myUsername = username;
//        this.inviterWantsRecording = recordingRequested;
//        
//        if (messageLabel != null) {
//            String recordingNote = recordingRequested ? " (wants to record game)" : "";
//            messageLabel.setText(inviter + " wants to play a game with you" + recordingNote);
//        }
//        
//       
//        if (recordGameCheckbox != null && recordingRequested) {
//            recordGameCheckbox.setSelected(true);
//        }
//    }
//    
//    public void setDialogStage(Stage stage) {
//        this.dialogStage = stage;
//    }
//    
//@FXML
//private void handleDecline() {
//  
//    JSONObject response = new JSONObject();
//    response.put("type", "invite_response");
//    response.put("from", inviterName);
//    response.put("accepted", false);
//
//    
//    NetworkConnection.getInstance().sendMessage(response);
//    
//    if (dialogStage != null) {
//        dialogStage.close();
//    }
//}}
//
//
