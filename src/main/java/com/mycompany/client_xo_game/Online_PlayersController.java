package com.mycompany.client_xo_game;

import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;
import com.mycompany.client_xo_game.navigation.Navigation;
import com.mycompany.client_xo_game.navigation.Routes;
import com.mycompany.client_xo_game.model.GameSession;
import javafx.application.Platform;
import org.json.JSONArray;
import org.json.JSONObject;

public class Online_PlayersController implements Runnable {

    @FXML
    private StackPane rootPane;
    @FXML
    private VBox contentBox;
    @FXML
    private ListView<Player> playersList;
    @FXML
    private Button profileBtn;

    private boolean running = true;
    private Thread refreshThread;
    private ObservableList<Player> playersObservableList;
    private String currentUsername;

    private static class Player {

        String name;
        boolean isOnline;

        public Player(String name, boolean isOnline) {
            this.name = name;
            this.isOnline = isOnline;
        }
    }

    private void setupNetworkListener() {
        NetworkConnection.getInstance().setListener(response -> {
            String type = response.optString("type");

            Platform.runLater(() -> {
                switch (type) {
                    case "available_players":
                        handleAvailablePlayers(response);
                        break;
                    case "invitation": // Server sends "invitation" when you receive an invite
                    case "game_invite":
                        handleGameInvite(response);
                        break;
                    case "invite_response": // Server sends this when someone responds to YOUR invite
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
                }
            });
        });
    }

    private void handleAvailablePlayers(JSONObject response) {
        JSONArray array = response.optJSONArray("players");
        if (array == null) {
            return;
        }

        playersObservableList.clear();
        for (int i = 0; i < array.length(); i++) {
            JSONObject playerObj = array.getJSONObject(i);
            String name = playerObj.getString("username");
            String status = playerObj.optString("status", "ONLINE");
            boolean isOnline = "ONLINE".equals(status);
            playersObservableList.add(new Player(name, isOnline));
        }
    }

    /**
     * Handle receiving a game invite - show dialog with recording option
     */
    private void handleGameInvite(JSONObject response) {
        String from = response.getString("from");
        boolean inviterWantsRecording = response.optBoolean("recordGame", false);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        styleAlert(alert);
        alert.setTitle("Game Invitation");

        String recordingNote = inviterWantsRecording
                ? " and wants to record the game"
                : "";
        alert.setHeaderText(from + " wants to play with you" + recordingNote);

        // Create checkbox for receiver's recording preference
        CheckBox recordCheckbox = new CheckBox("I also want to record this game (saved to MY records)");
        recordCheckbox.setSelected(false);

        VBox dialogContent = new VBox(10);
        dialogContent.getChildren().add(recordCheckbox);

        // Add explanation text
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

                // CRITICAL: Start recording IMMEDIATELY if receiver wants to record
                // This ensures recording is active BEFORE game starts
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

    /**
     * Handle invite response - when someone accepts/declines YOUR invite
     */
    private void handleInviteResponse(JSONObject response) {
        boolean accepted = response.getBoolean("accepted");
        String from = response.optString("from", "Player");

        if (accepted) {
            // Use the locally stored recording preference (not from server response)
            boolean receiverWantsRecording = response.optBoolean("receiverWantsRecording", false);

            // CRITICAL: If YOU (inviter) wanted recording, start it NOW using stored preference
            if (inviterRequestedRecording) {
                GameSession.startOnlineRecording(currentUsername, pendingOpponentName);
                System.out.println("Recording started for " + currentUsername + " (YOU are player1 in your file)");
            }

            String recordingInfo = "";
            if (inviterRequestedRecording && receiverWantsRecording) {
                recordingInfo = "\n(Both players will have separate recordings with their own names first)";
            } else if (inviterRequestedRecording) {
                recordingInfo = "\n(Game will be saved to YOUR records with your name first)";
            } else if (receiverWantsRecording) {
                recordingInfo = "\n(" + from + " will record this game with their name first)";
            }

            System.out.println(from + " accepted your invitation! Starting game..." + recordingInfo);

            // Reset pending state
            inviterRequestedRecording = false;
            pendingOpponentName = null;
        } else {
            showAlert(Alert.AlertType.WARNING, "Invitation Declined",
                    from + " declined your invitation.");

            // Reset pending state
            inviterRequestedRecording = false;
            pendingOpponentName = null;
        }
    }

    private void handleGameStart(JSONObject response) {
        stop(); // Stop refresh thread

        String opponent = response.getString("opponent");
        String yourSymbol = response.getString("yourSymbol");
        boolean yourTurn = response.getBoolean("yourTurn");

        System.out.println("Game starting! Opponent: " + opponent + ", Symbol: " + yourSymbol + ", Turn: " + yourTurn);

        // Navigate to online gameboard
        playExitTransition(() -> {
            Navigation.goToOnlineGame(opponent, yourSymbol, yourTurn);
        });
    }

    private void handleInviteSent(JSONObject response) {
        String status = response.getString("status");
        if ("failed".equals(status)) {
            String reason = response.optString("reason", "Unknown error");
            showAlert(Alert.AlertType.ERROR, "Invitation Failed", reason);
        }
    }

    private void handleError(JSONObject response) {
        String message = response.optString("message", "An error occurred");
        showAlert(Alert.AlertType.ERROR, "Error", message);
    }

    private void requestAvailablePlayers() {
        JSONObject request = new JSONObject();
        request.put("type", "get_available_players");
        NetworkConnection.getInstance().sendMessage(request);
    }

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(5000); // 5 seconds
                if (running) {
                    requestAvailablePlayers();
                }
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public void stop() {
        running = false;
        if (refreshThread != null) {
            refreshThread.interrupt();
        }
    }

    @FXML
    public void initialize() {
        // Get current username
        currentUsername = NetworkConnection.getInstance().getCurrentUsername();

        if (currentUsername == null || currentUsername.isEmpty()) {
            System.err.println("ERROR: No username found in NetworkConnection!");
            currentUsername = "UnknownPlayer";
        }

        System.out.println("OnlinePlayersController initialized for user: " + currentUsername);

        rootPane.setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(1000), rootPane);
        fadeIn.setToValue(1);
        fadeIn.play();

        playersObservableList = FXCollections.observableArrayList();
        playersList.setItems(playersObservableList);

        setupNetworkListener();
        requestAvailablePlayers();

        refreshThread = new Thread(this);
        refreshThread.setDaemon(true);
        refreshThread.start();

        playersList.setCellFactory(listView -> new ListCell<Player>() {
            @Override
            protected void updateItem(Player player, boolean empty) {
                super.updateItem(player, empty);

                if (empty || player == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    Label nameLabel = new Label(player.name);
                    nameLabel.getStyleClass().add("player-name");
                    nameLabel.setPrefWidth(250);

                    Label statusLabel = new Label(player.isOnline ? "●  Online" : "●  In Game");
                    statusLabel.getStyleClass().add(player.isOnline ? "status-online" : "status-busy");
                    statusLabel.setPrefWidth(150);
                    statusLabel.setAlignment(Pos.CENTER_LEFT);
                    statusLabel.setPadding(new Insets(0, 0, 0, 10));

                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    Button inviteBtn = new Button("SEND INVITE");
                    inviteBtn.getStyleClass().add("invite-btn");

                    if (!player.isOnline) {
                        inviteBtn.setVisible(false);
                    } else {
                        inviteBtn.setOnAction(e -> handleInvite(player.name));
                    }

                    HBox container = new HBox(10, nameLabel, statusLabel, spacer, inviteBtn);
                    container.setAlignment(Pos.CENTER_LEFT);
                    container.setPadding(new Insets(5, 10, 5, 10));

                    setGraphic(container);
                }
            }
        });

        rootPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            double w = newVal.doubleValue();
            contentBox.setMaxWidth(Math.max(600, w * 0.7));
        });
    }

    /**
     * Handle sending an invite - show dialog with recording option
     */
// Track if the inviter wants to record (needed when response comes back)
    private boolean inviterRequestedRecording = false;
    private String pendingOpponentName = null;

    /**
     * Handle sending an invite - show dialog with recording option
     */
    private void handleInvite(String playerName) {
        System.out.println("Preparing to send invitation to: " + playerName);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        styleAlert(alert);
        alert.setTitle("Send Game Invitation");
        alert.setHeaderText("Invite " + playerName + " to play");

        // Create checkbox for recording option
        CheckBox recordCheckbox = new CheckBox("Record this game (saved to MY records)");
        recordCheckbox.setSelected(false);

        VBox dialogContent = new VBox(10);
        dialogContent.getChildren().add(recordCheckbox);

        Label explanationLabel = new Label(
                "If you enable recording, this game will be saved to YOUR records.\n"
                + "The filename will have YOUR name first (e.g., ONLINE_" + currentUsername + "_VS_" + playerName + "_...)\n"
                + playerName + " can also choose to record it separately with THEIR name first."
        );
        explanationLabel.setWrapText(true);
        dialogContent.getChildren().add(explanationLabel);

        alert.getDialogPane().setContent(dialogContent);

        ButtonType sendBtn = new ButtonType("Send Invite");
        ButtonType cancelBtn = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(sendBtn, cancelBtn);

        alert.showAndWait().ifPresent(button -> {
            if (button == sendBtn) {
                boolean recordGame = recordCheckbox.isSelected();

                // CRITICAL: Store recording preference locally for when response comes back
                inviterRequestedRecording = recordGame;
                pendingOpponentName = playerName;

                // Send invitation with recording preference
                JSONObject invite = new JSONObject();
                invite.put("type", "send_invite");
                invite.put("to", playerName);
                invite.put("recordGame", recordGame);

                NetworkConnection.getInstance().sendMessage(invite);

                String recordingNote = recordGame
                        ? " (you will record with your name first)"
                        : "";
                System.out.println("Invitation sent to " + playerName + recordingNote);

                showAlert(Alert.AlertType.INFORMATION, "Invitation Sent",
                        "Waiting for " + playerName + " to respond..." + recordingNote);
            }
        });
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        styleAlert(alert);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    private void playExitTransition(Runnable onFinished) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), rootPane);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> onFinished.run());
        fadeOut.play();
    }

    @FXML
    private void goToProfile() {
        stop();
        playExitTransition(() -> Navigation.goTo(Routes.Profile));
    }

    @FXML
    private void goToReplays() {
        stop();
        playExitTransition(() -> Navigation.goTo(Routes.GAME_REPLAYS));
    }

    @FXML
    private void goToLeaderboard() {
        stop();
        playExitTransition(() -> Navigation.goTo(Routes.LEADERBOARD));
    }

    private void styleAlert(Alert alert) {
        var dialogPane = alert.getDialogPane();

        dialogPane.setId("xo-alert");

        dialogPane.getStylesheets().add(
                getClass().getResource("/styles/alert.css").toExternalForm()
        );
    }

}
