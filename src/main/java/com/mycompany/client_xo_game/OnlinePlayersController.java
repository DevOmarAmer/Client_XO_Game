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

public class OnlinePlayersController {

    @FXML
    private StackPane rootPane;
    @FXML
    private VBox contentBox;
    @FXML
    private ListView<Player> playersList;
    @FXML
    private Button profileBtn;

    // Simple Player Model
    private static class Player {

        String name;
        boolean isOnline;

        public Player(String name, boolean isOnline) {
            this.name = name;
            this.isOnline = isOnline;
        }
    }

    @FXML
    public void initialize() {
        // 1. Entrance Animation
        rootPane.setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(1000), rootPane);
        fadeIn.setToValue(1);
        fadeIn.play();

        // 2. Setup List Data
        ObservableList<Player> data = FXCollections.observableArrayList(
                new Player("Ahmed", true),
                new Player("Omar", false),
                new Player("Sara", true),
                new Player("Mona", false),
                new Player("Youssef", true),
                new Player("Laila", true),
                new Player("Hassan", false)
        );
        playersList.setItems(data);

        // 3. Custom Cell Factory for 3-Column Layout
        playersList.setCellFactory(listView -> new ListCell<Player>() {
            @Override
            protected void updateItem(Player player, boolean empty) {
                super.updateItem(player, empty);

                if (empty || player == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    // --- UI Construction ---

                    // Col 1: Name
                    Label nameLabel = new Label(player.name);
                    nameLabel.getStyleClass().add("player-name");
                    nameLabel.setPrefWidth(250);

                    // Col 2: Status
                    Label statusLabel = new Label(player.isOnline ? "●  Online" : "●  In Game");                    // Note: Emojis provide the color for the circle, CSS provides color for text.
                    // Changed In-Game emoji to yellow/orange circle if desired, or keep red.
                    // If you want purely CSS circles, you'd use Shapes, but emojis are simpler here.

                    statusLabel.getStyleClass().add(player.isOnline ? "status-online" : "status-busy");
                    statusLabel.setPrefWidth(150);

                    // --- ALIGNMENT: Start from beginning of column ---
                    statusLabel.setAlignment(Pos.CENTER_LEFT);
                    statusLabel.setPadding(new Insets(0, 0, 0, 10));

                    // Col 3: Action (Spacer + Button)
                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    Button inviteBtn = new Button("SEND INVITE");
                    inviteBtn.getStyleClass().add("invite-btn");

                    if (!player.isOnline) {
                        inviteBtn.setVisible(false);
                    } else {
                        inviteBtn.setOnAction(e -> handleInvite(player.name));
                    }

                    // Container
                    HBox container = new HBox(10, nameLabel, statusLabel, spacer, inviteBtn);
                    container.setAlignment(Pos.CENTER_LEFT);
                    container.setPadding(new Insets(5, 10, 5, 10));

                    setGraphic(container);
                }
            }
        });

        // 4. Scaling Logic
        rootPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            double w = newVal.doubleValue();
            contentBox.setMaxWidth(Math.max(600, w * 0.7));
        });
    }

    private void handleInvite(String playerName) {
        System.out.println("Sending invitation to: " + playerName);
    }

    private void playExitTransition(Runnable onFinished) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), rootPane);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> onFinished.run());
        fadeOut.play();
    }

    // --- Navigation Actions ---
    @FXML
    private void goToProfile() {
        playExitTransition(() -> Navigation.goTo(Routes.Profile));
    }

    @FXML
    private void goToReplays() {
        playExitTransition(() -> Navigation.goTo(Routes.GAME_REPLAYS));
    }

    @FXML
    private void goToLeaderboard() {
        playExitTransition(() -> Navigation.goTo(Routes.LEADERBOARD));
    }
}
