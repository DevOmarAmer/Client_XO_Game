package com.mycompany.client_xo_game;

import com.mycompany.client_xo_game.enums.Cell;
import com.mycompany.client_xo_game.enums.GameMode;
import com.mycompany.client_xo_game.model.GameSession;
import com.mycompany.client_xo_game.model.Player_Offline;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import com.mycompany.client_xo_game.navigation.Navigation;
import com.mycompany.client_xo_game.navigation.Routes;

public class Offline_PlayersController {

    @FXML
    private StackPane rootPane;
    @FXML
    private VBox contentBox;
    @FXML
    private VBox inputContainer;
    @FXML
    private TextField player_one_id;
    @FXML
    private TextField player_two_id;
    @FXML
    private Button back_id;
    @FXML
    private Button play_id;
    @FXML
    private Label titleLabel;

    @FXML
    public void initialize() {
        // -----------------------------
        // 1. Entrance Fade-in
        // -----------------------------
        rootPane.setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(1200), rootPane);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();

        // -----------------------------
        // 2. Title breathing animation
        // -----------------------------
        ScaleTransition pulse = new ScaleTransition(Duration.millis(2000), titleLabel);
        pulse.setFromX(1.0);
        pulse.setFromY(1.0);
        pulse.setToX(1.06);
        pulse.setToY(1.06);
        pulse.setCycleCount(Animation.INDEFINITE);
        pulse.setAutoReverse(true);
        pulse.play();

        // -----------------------------
        // 3. Responsive scaling
        // -----------------------------
        rootPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            double w = newVal.doubleValue();

            // TITLE font scaling
            double titleFont = Math.min(48, Math.max(26, w / 20));
            titleLabel.setStyle("-fx-font-size: " + titleFont + "px;");

            // TextField scaling
            double inputFont = Math.max(14, w / 50);
            String inputStyle = "-fx-font-size: " + inputFont + "px;";
            player_one_id.setStyle(inputStyle);
            player_two_id.setStyle(inputStyle);

            double inputHeight = Math.max(45, w / 15);
            player_one_id.setPrefHeight(inputHeight);
            player_two_id.setPrefHeight(inputHeight);

            // Button font scaling
            double btnFont = Math.max(14, w / 50);
            String btnStyle = "-fx-font-size: " + btnFont + "px;";
            back_id.setStyle(btnStyle);
            play_id.setStyle(btnStyle);

            // Button height scaling
            double btnHeight = Math.max(45, w / 15);
            back_id.setPrefHeight(btnHeight);
            play_id.setPrefHeight(btnHeight);

            // VBox spacing scaling
            contentBox.setSpacing(w * 0.04);
            inputContainer.setSpacing(w * 0.03);

            // Check if parent is HBox before casting to avoid ClassCastException
            if (back_id.getParent() instanceof HBox) {
                ((HBox) back_id.getParent()).setSpacing(w * 0.03);
            }
        });

        rootPane.heightProperty().addListener((obs, oldVal, newVal) -> {
            double h = newVal.doubleValue();
            contentBox.setMaxHeight(h * 0.85);
        });

        // -----------------------------
        // 4. Add hover animations to buttons
        // -----------------------------
        addHoverAnimation(back_id);
        addHoverAnimation(play_id);
    }

    // -----------------------------
    // Navigation Actions
    // -----------------------------
    @FXML
    private void goBack() {
        animateButton(back_id);
        Navigation.goTo(Routes.MODE_SELECTION);
    }

    @FXML
    private void playGame() {//on playGame button pressed
        String p1Name = player_one_id.getText().trim();
        String p2Name = player_two_id.getText().trim();

        if (p1Name.isEmpty() || p2Name.isEmpty()) {
            System.out.println("Both players must enter names");
            return;
        }
        Player_Offline player1 = new Player_Offline(p1Name, Cell.X);
        Player_Offline player2 = new Player_Offline(p2Name, Cell.O);

        GameSession.setPlayers(player1, player2);
        Navigation.goTo(Routes.GAMEBOARD);
    }

    // -----------------------------
    // Smooth button press animation
    // -----------------------------
    private void animateButton(Button btn) {
        ScaleTransition st = new ScaleTransition(Duration.millis(150), btn);
        st.setFromX(1.0);
        st.setFromY(1.0);
        st.setToX(0.92);
        st.setToY(0.92);
        st.setAutoReverse(true);
        st.setCycleCount(2);
        st.play();
    }

    // -----------------------------
    // Smooth hover animation
    // -----------------------------
    private void addHoverAnimation(Button btn) {
        btn.setOnMouseEntered(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), btn);
            st.setToX(1.05);
            st.setToY(1.05);
            st.play();
        });

        btn.setOnMouseExited(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), btn);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
        });
    }
}
