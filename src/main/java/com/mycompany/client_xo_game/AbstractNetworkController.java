package com.mycompany.client_xo_game;

import com.mycompany.client_xo_game.model.GameSession;
import com.mycompany.client_xo_game.navigation.Navigation;
import com.mycompany.client_xo_game.navigation.Routes;
import com.mycompany.client_xo_game.util.ClientUtils;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.json.JSONObject;

public abstract class AbstractNetworkController {

    protected String currentUsername;

    public AbstractNetworkController() {
        // Initialize currentUsername if needed, or get it dynamically
        this.currentUsername = NetworkConnection.getInstance().getCurrentUsername();
        if (this.currentUsername == null || this.currentUsername.isEmpty()) {
            System.err.println("WARNING: No username found in NetworkConnection. Defaulting to 'UnknownPlayer'.");
            this.currentUsername = "UnknownPlayer";
        }
    }

    protected void setupNetworkListener() {
        NetworkConnection.getInstance().setListener(response -> {
            String type = response.optString("type");

            Platform.runLater(() -> {
                switch (type) {
                    case "available_players":
                        handleAvailablePlayers(response);
                        break;
                    case "invitation":
                    case "game_invite":
                        handleGameInvite(response);
                        break;
                    case "invite_response":
                        handleInviteResponse(response);
                        break;
                    case "game_start":
                        handleGameStart(response);
                        break;
                    case "invite_sent":
                        handleInviteSent(response);
                        break;
                    case "error":
                        handleError(response);
                        break;
                    case "server_shutdown":
                        handleServerDisconnection();
                        break;
                    // Add other common cases here if they arise
                    default:
                        handleGenericResponse(response);
                        break;
                }
            });
        });
    }

    protected void handleAvailablePlayers(JSONObject response) {
        // Default empty implementation, subclasses can override
    }

    
    protected void handleGameInvite(JSONObject response) {
        String from = response.getString("from");
        boolean inviterWantsRecording = response.optBoolean("recordGame", false);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        ClientUtils.styleAlert(alert);
        alert.setTitle("Game Invitation");

        String recordingNote = inviterWantsRecording
                ? " and wants to record the game"
                : "";
        alert.setHeaderText(from + " wants to play with you" + recordingNote);

        CheckBox recordCheckbox = new CheckBox("I also want to record this game (saved to MY records)");
        recordCheckbox.setSelected(false);
        recordCheckbox.setStyle("-fx-text-fill: white;"); // Apply style here

        VBox dialogContent = new VBox(10);
        dialogContent.getChildren().add(recordCheckbox);

        Label explanationLabel = new Label();
        if (inviterWantsRecording) {
            explanationLabel.setText(from + " will have this game saved to their records.\n"
                    + "You can also save it to YOUR records by checking the box above.\n"
                    + "Each player gets their own recording with their name first.");
        } else {
            explanationLabel.setText("Check the box if you want to save this game to YOUR records.\n"
                    + "The file will have YOUR name first in the filename.");
        }
        explanationLabel.setWrapText(true);
        explanationLabel.setStyle("-fx-text-fill: white;"); // Apply style here
        dialogContent.getChildren().add(explanationLabel);

        alert.getDialogPane().setContent(dialogContent);

        ButtonType acceptBtn = new ButtonType("Accept");
        ButtonType declineBtn = new ButtonType("Decline", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(acceptBtn, declineBtn);

        alert.showAndWait().ifPresent(button -> {
            boolean receiverWantsRecording = recordCheckbox.isSelected();

            JSONObject inviteResponse = new JSONObject();
            inviteResponse.put("type", "invite_response");
            inviteResponse.put("from", from);
            inviteResponse.put("accepted", button == acceptBtn);

            if (button == acceptBtn) {
                inviteResponse.put("inviterWantsRecording", inviterWantsRecording);
                inviteResponse.put("receiverWantsRecording", receiverWantsRecording);

                if (receiverWantsRecording) {
                    GameSession.startOnlineRecording(currentUsername, from);
                    System.out.println("Recording started for " + currentUsername + " (YOU are player1 in your file)");
                }

                System.out.println("Invitation accepted"
                        + (receiverWantsRecording ? " with your recording enabled" : ""));
            } else {
                System.out.println("Invitation declined");
            }

            NetworkConnection.getInstance().sendMessage(inviteResponse);
        });
    }

    protected void handleInviteResponse(JSONObject response) {
        // Default empty implementation, subclasses can override
    }

    protected void handleGameStart(JSONObject response) {
        String opponent = response.getString("opponent");
        String yourSymbol = response.getString("yourSymbol");
        boolean yourTurn = response.getBoolean("yourTurn");

        System.out.println("Game starting! Opponent: " + opponent + ", Symbol: " + yourSymbol + ", Turn: " + yourTurn);

        // Navigate to online gameboard
        Navigation.goToOnlineGame(opponent, yourSymbol, yourTurn);
    }

    protected void handleInviteSent(JSONObject response) {
        // Default empty implementation, subclasses can override
    }

    protected void handleError(JSONObject response) {
        // Default empty implementation for error, subclasses can override
    }

    protected void handleGenericResponse(JSONObject response) {
        // Default empty implementation for generic responses, subclasses can override
        System.out.println("Unhandled network response type: " + response.optString("type") + " - " + response.toString());
    }

    /**
     * Handles server disconnection, common for all controllers that need to react to it.
     */
    protected void handleServerDisconnection() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            ClientUtils.styleAlert(alert);
            alert.setTitle("Server Disconnected");
            alert.setHeaderText("Connection Lost");
            alert.setContentText("The server has shut down or lost connection. You will be returned to the login page.");
            alert.showAndWait();
            Navigation.goTo(Routes.LOGIN);
        });
    }

    /**
     * Utility method to show alerts, styled using ClientUtils.
     */
    protected void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        ClientUtils.styleAlert(alert);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
}
