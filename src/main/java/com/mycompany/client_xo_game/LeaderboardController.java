package com.mycompany.client_xo_game;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;
import com.mycompany.client_xo_game.navigation.Navigation;
import com.mycompany.client_xo_game.navigation.Routes;
import com.mycompany.client_xo_game.model.CurrentUser;
import org.json.JSONArray;
import org.json.JSONObject;

public class LeaderboardController extends AbstractNetworkController {

    @FXML
    private StackPane rootPane;
    @FXML
    private VBox contentBox;
    @FXML
    private ListView<PlayerScore> scoreList;
    @FXML
    private ProgressIndicator loadingSpinner;

    private static class PlayerScore {

        int rank;
        String name;
        int score;

        public PlayerScore(int rank, String name, int score) {
            this.rank = rank;
            this.name = name;
            this.score = score;
        }
    }

    @FXML
    public void initialize() {
        super.setupNetworkListener(); // Setup network listener from the base class

        rootPane.setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(800), rootPane);
        fadeIn.setToValue(1);
        fadeIn.play();

        if (loadingSpinner == null) {
            loadingSpinner = new ProgressIndicator();
            loadingSpinner.setMaxSize(50, 50);
            rootPane.getChildren().add(loadingSpinner);
        }
        loadingSpinner.setVisible(true);
        scoreList.setVisible(false);

        JSONObject req = new JSONObject();
        req.put("type", "get_leaderboard");
        NetworkConnection.getInstance().sendMessage(req);

        setupCellFactory();

        rootPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            contentBox.setMaxWidth(Math.max(550, newVal.doubleValue() * 0.65));
        });
    }



    @Override
    protected void handleGenericResponse(JSONObject json) {
        String type = json.optString("type");
        if ("leaderboard_response".equals(type)) {
            Platform.runLater(() -> {
                if ("success".equals(json.optString("status"))) {
                    updateList(json.getJSONArray("leaderboard"));
                } else {
                    System.out.println("Failed to fetch leaderboard");
                }
                loadingSpinner.setVisible(false);
                scoreList.setVisible(true);
            });
        }
    }

    private void updateList(JSONArray jsonList) {
        ObservableList<PlayerScore> data = FXCollections.observableArrayList();

        for (int i = 0; i < jsonList.length(); i++) {
            JSONObject player = jsonList.getJSONObject(i);
            String name = player.getString("username");
            int score = player.getInt("score");
            data.add(new PlayerScore(i + 1, name, score));
        }

        scoreList.setItems(data);
    }

    private void setupCellFactory() {
        scoreList.setCellFactory(listView -> new ListCell<PlayerScore>() {
            @Override
            protected void updateItem(PlayerScore player, boolean empty) {
                super.updateItem(player, empty);

                // Always clear styles first
                getStyleClass().remove("current-user-cell");
                setGraphic(null);
                setText(null);

                if (empty || player == null) {
                    // No content in empty cells
                } else {
                    // Rank Logic
                    String rankText;
                    String rankColor = "#bdc3c7"; // Default gray

                    switch (player.rank) {
                        case 1:
                            rankText = "🥇";
                            rankColor = "#f1c40f";
                            break;
                        case 2:
                            rankText = "🥈";
                            rankColor = "#95a5a6";
                            break;
                        case 3:
                            rankText = "🥉";
                            rankColor = "#d35400";
                            break;
                        default:
                            rankText = "#" + player.rank;
                            break;
                    }

                    Label rankLbl = new Label(rankText);
                    rankLbl.setStyle("-fx-text-fill: " + rankColor + "; -fx-font-size: 18px; -fx-font-weight: bold;");
                    rankLbl.setPrefWidth(60);
                    rankLbl.setAlignment(Pos.CENTER);

                    // Name
                    Label nameLbl = new Label(player.name);
                    nameLbl.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");

                    // Spacer
                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    // Score
                    Label scoreLbl = new Label(player.score + " pts");
                    scoreLbl.setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold; -fx-font-size: 14px;");

                    HBox container = new HBox(15, rankLbl, nameLbl, spacer, scoreLbl);
                    container.setAlignment(Pos.CENTER_LEFT);
                    container.setPadding(new javafx.geometry.Insets(10));

                    setGraphic(container);

                    // Highlight current user
                    if (player.name.equals(CurrentUser.getUsername())) {
                        getStyleClass().add("current-user-cell");
                    }
                }
            }
        });
    }

    @FXML
    private void goBack() {
        playExitTransition(() -> Navigation.goTo(Routes.ONLINE_PLAYERS));
    }

    private void playExitTransition(Runnable onFinished) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), rootPane);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> onFinished.run());
        fadeOut.play();
    }
}
