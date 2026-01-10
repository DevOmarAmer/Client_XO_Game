package com.mycompany.client_xo_game;

import com.mycompany.client_xo_game.navigation.Navigation;
import com.mycompany.client_xo_game.navigation.Routes;
import com.mycompany.client_xo_game.util.ClientUtils;
import javafx.application.Platform;
import javafx.scene.control.Alert;
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
        // Default empty implementation, subclasses can override
    }

    protected void handleInviteResponse(JSONObject response) {
        // Default empty implementation, subclasses can override
    }

    protected void handleGameStart(JSONObject response) {
        // Default empty implementation, subclasses can override
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
