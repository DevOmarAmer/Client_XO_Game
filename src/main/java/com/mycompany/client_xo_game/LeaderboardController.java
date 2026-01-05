package com.mycompany.client_xo_game;

import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;
import com.mycompany.client_xo_game.navigation.Navigation;
import com.mycompany.client_xo_game.navigation.Routes;

public class LeaderboardController {

    @FXML
    private StackPane rootPane;
    @FXML
    private VBox contentBox;
    @FXML
    private ListView<PlayerScore> scoreList;

    // Data Model
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
        // 1. Entrance Animation
        rootPane.setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(800), rootPane);
        fadeIn.setToValue(1);
        fadeIn.play();

        // 2. Mock Data
        ObservableList<PlayerScore> data = FXCollections.observableArrayList(
                new PlayerScore(1, "MasterYoda", 2500),
                new PlayerScore(2, "Skywalker", 2350),
                new PlayerScore(3, "ObiWan", 2100),
                new PlayerScore(4, "DarthV", 1900),
                new PlayerScore(5, "R2D2", 1800),
                new PlayerScore(6, "C3PO", 1500),
                new PlayerScore(7, "Chewie", 1200)
        );
        scoreList.setItems(data);

        // 3. Custom Cell Factory (Columns)
        scoreList.setCellFactory(listView -> new ListCell<PlayerScore>() {
            @Override
            protected void updateItem(PlayerScore player, boolean empty) {
                super.updateItem(player, empty);

                if (empty || player == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    // Rank (Icon/Text)
                    String rankText = String.valueOf(player.rank);
                    if (player.rank == 1) {
                        rankText = "🥇 " + player.rank;
                    } else if (player.rank == 2) {
                        rankText = "🥈 " + player.rank;
                    } else if (player.rank == 3) {
                        rankText = "🥉 " + player.rank;
                    } else {
                        rankText = "   " + player.rank;
                    }

                    Label rankLbl = new Label(rankText);
                    rankLbl.getStyleClass().add("rank-text");
                    rankLbl.setPrefWidth(80);
                    rankLbl.setAlignment(Pos.CENTER);

                    // Name
                    Label nameLbl = new Label(player.name);
                    nameLbl.getStyleClass().add("player-text");
                    nameLbl.setMaxWidth(Double.MAX_VALUE);

                    // Spacer
                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    // Score
                    Label scoreLbl = new Label(player.score + " pts");
                    scoreLbl.getStyleClass().add("score-text");
                    scoreLbl.setPrefWidth(100);
                    scoreLbl.setAlignment(Pos.CENTER_RIGHT);

                    // Layout
                    HBox container = new HBox(10, rankLbl, nameLbl, spacer, scoreLbl);
                    container.setAlignment(Pos.CENTER_LEFT);
                    setGraphic(container);
                }
            }
        });

        // 4. Responsive Scaling
        rootPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            double w = newVal.doubleValue();
            contentBox.setMaxWidth(Math.max(550, w * 0.65));
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
