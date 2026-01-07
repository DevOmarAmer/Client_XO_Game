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
import javafx.application.Platform;
import org.json.JSONArray;
import org.json.JSONObject;

public class OnlinePlayersController implements Runnable {

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
                    case "invitation":
                        handleInvitation(response);
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
                }
            });
        });
    }
    
    private void handleAvailablePlayers(JSONObject response) {
        JSONArray array = response.optJSONArray("players");
        if (array == null) return;

        playersObservableList.clear();
        for (int i = 0; i < array.length(); i++) {
            JSONObject playerObj = array.getJSONObject(i);
            String name = playerObj.getString("username");
            String status = playerObj.optString("status", "ONLINE");
            boolean isOnline = "ONLINE".equals(status);
            playersObservableList.add(new Player(name, isOnline));
        }
    }
    
    private void handleInvitation(JSONObject response) {
        String from = response.getString("from");
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Game Invitation");
        alert.setHeaderText("Invitation from " + from);
        alert.setContentText(from + " wants to play with you!");
        
        ButtonType acceptBtn = new ButtonType("Accept");
        ButtonType declineBtn = new ButtonType("Decline", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(acceptBtn, declineBtn);
        
        alert.showAndWait().ifPresent(button -> {
            JSONObject inviteResponse = new JSONObject();
            inviteResponse.put("type", "invite_response");
            inviteResponse.put("from", from);
            inviteResponse.put("accepted", button == acceptBtn);
            NetworkConnection.getInstance().sendMessage(inviteResponse);
        });
    }
    
    private void handleInviteResponse(JSONObject response) {
        boolean accepted = response.getBoolean("accepted");
        String from = response.optString("from", "Player");
        
        if (accepted) {
//            showAlert(Alert.AlertType.INFORMATION, "Invitation Accepted", 
//                     from + " accepted your invitation! Starting game...");
        } else {
            showAlert(Alert.AlertType.WARNING, "Invitation Declined", 
                     from + " declined your invitation.");
        }
    }
    /*JSONObject penalty = new JSONObject(); 
        penalty.put("type", "penalty");
        penalty.put("to", quitter);
        NetworkConnection.getInstance().sendMessage(penalty);
        System.out.println("----------BAD Player---------");*/
    
    private void handleGameStart(JSONObject response) {
        stop(); // Stop refresh thread
        
        String opponent = response.getString("opponent");
        String yourSymbol = response.getString("yourSymbol");
        boolean yourTurn = response.getBoolean("yourTurn");
        
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

    private void handleInvite(String playerName) {
        System.out.println("Sending invitation to: " + playerName);
        
        JSONObject invite = new JSONObject();
        invite.put("type", "send_invite");
        invite.put("to", playerName);
        NetworkConnection.getInstance().sendMessage(invite);
        
        showAlert(Alert.AlertType.INFORMATION, "Invitation Sent", 
                 "Waiting for " + playerName + " to respond...");
    }
    
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
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
}