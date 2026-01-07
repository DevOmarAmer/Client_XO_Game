//package com.mycompany.client_xo_game;
//
//import com.mycompany.client_xo_game.model.GameSession;
//import javafx.scene.control.Alert;
//import javafx.scene.control.ButtonBar;
//import javafx.scene.control.ButtonType;
//import javafx.scene.control.CheckBox;
//import org.json.JSONObject;
//
//public class InviteDialogHelper {
//    
//
//    public static void showSendInviteDialog(String opponentName, String myUsername) {
//        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//        alert.setTitle("Send Game Invitation");
//        alert.setHeaderText("Invite " + opponentName + " to play");
//        
//        // Create checkbox for recording option
//        CheckBox recordCheckbox = new CheckBox("Record this game (saved to your records)");
//        recordCheckbox.setSelected(false);
//        
//        alert.getDialogPane().setContent(recordCheckbox);
//        
//        ButtonType sendBtn = new ButtonType("Send Invite");
//        ButtonType cancelBtn = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
//        alert.getButtonTypes().setAll(sendBtn, cancelBtn);
//        
//        alert.showAndWait().ifPresent(button -> {
//            if (button == sendBtn) {
//                boolean recordGame = recordCheckbox.isSelected();
//                
//                JSONObject invite = new JSONObject();
//                invite.put("type", "game_invite");
//                invite.put("to", opponentName);
//                invite.put("from", myUsername);
//                invite.put("recordGame", recordGame);
//                
//     
//                if (recordGame) {
//                    System.out.println("You requested recording - game will be saved to your records");
//                }
//                
//                NetworkConnection.getInstance().sendMessage(invite);
//                
//                System.out.println("Invitation sent to " + opponentName + 
//                                  (recordGame ? " with recording enabled" : ""));
//            }
//        });
//    }
//    
//  
//    public static void showReceiveInviteDialog(String inviterName, String myUsername, 
//                                               boolean recordingRequested) {
//        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//        alert.setTitle("Game Invitation");
//        
//        String recordingNote = recordingRequested 
//            ? " and wants to record the game" 
//            : "";
//        alert.setHeaderText(inviterName + " wants to play with you" + recordingNote);
//        
//        CheckBox recordCheckbox = new CheckBox("I also want to record this game (saved to my records)");
//        recordCheckbox.setSelected(false); // Don't pre-check - let receiver decide
//        
//        // Show explanation if inviter requested recording
//        String contentText;
//        if (recordingRequested) {
//            contentText = inviterName + " will have this game saved to their records.\n" +
//                         "You can also save it to your records by checking the box above.\n\n" +
//                         "Do you want to accept?";
//        } else {
//            contentText = "Do you want to accept this invitation?";
//        }
//        
//        alert.getDialogPane().setContent(recordCheckbox);
//        alert.setContentText(contentText);
//        
//        ButtonType acceptBtn = new ButtonType("Accept");
//        ButtonType declineBtn = new ButtonType("Decline", ButtonBar.ButtonData.CANCEL_CLOSE);
//        alert.getButtonTypes().setAll(acceptBtn, declineBtn);
//        
//        alert.showAndWait().ifPresent(button -> {
//            if (button == acceptBtn) {
//                boolean iWantToRecord = recordCheckbox.isSelected();
//                
//                JSONObject accept = new JSONObject();
//                accept.put("type", "accept_invite");
//                accept.put("from", inviterName);
//                accept.put("inviterWantsRecording", recordingRequested);
//                accept.put("receiverWantsRecording", iWantToRecord);
//                
//                NetworkConnection.getInstance().sendMessage(accept);
//                
//              
//                if (iWantToRecord) {
//                    
//                    GameSession.startOnlineRecording(myUsername, inviterName);
//                    System.out.println("You requested recording - game will be saved to your records");
//                }
//                
//                System.out.println("Invitation accepted" + 
//                                  (iWantToRecord ? " with your recording enabled" : ""));
//            } else {
//                JSONObject decline = new JSONObject();
//                decline.put("type", "decline_invite");
//                decline.put("from", inviterName);
//                
//                NetworkConnection.getInstance().sendMessage(decline);
//                
//                System.out.println("Invitation declined");
//            }
//        });
//    }
//}