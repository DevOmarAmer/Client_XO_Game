package com.mycompany.client_xo_game;

import com.mycompany.client_xo_game.enums.AIDifficulty;
import com.mycompany.client_xo_game.enums.GameMode;
import com.mycompany.client_xo_game.model.GameSession;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.animation.*;
import javafx.util.Duration;

import com.mycompany.client_xo_game.navigation.Navigation;
import com.mycompany.client_xo_game.navigation.Routes;

public class Level_SelectionController {

    @FXML
    private StackPane rootPane;
    @FXML 
    private VBox contentBox;
    @FXML 
    private VBox buttonContainer;
    @FXML 
    private Label titleLabel;
    @FXML 
    private Button btnEasy, btnMedium, btnHard, btnBack;

    @FXML
    public void initialize() {
        /* ===============================
           Entrance fade + slide
           =============================== */
        rootPane.setOpacity(0);
        rootPane.setTranslateY(40);

        FadeTransition fade = new FadeTransition(Duration.millis(900), rootPane);
        fade.setFromValue(0);
        fade.setToValue(1);

        TranslateTransition slide = new TranslateTransition(Duration.millis(900), rootPane);
        slide.setFromY(40);
        slide.setToY(0);

        new ParallelTransition(fade, slide).play();

        /* ===============================
           Title glow breathing
           =============================== */
        ScaleTransition pulse = new ScaleTransition(Duration.seconds(2), titleLabel);
        pulse.setFromX(1.0);
        pulse.setFromY(1.0);
        pulse.setToX(1.07);
        pulse.setToY(1.07);
        pulse.setAutoReverse(true);
        pulse.setCycleCount(Animation.INDEFINITE);
        pulse.play();

        /* ===============================
           Responsive scaling
           =============================== */
        rootPane.widthProperty().addListener((obs, o, n) -> {
            double w = n.doubleValue();

            titleLabel.setStyle("-fx-font-size: " + Math.max(26, w / 20) + "px;");
            String btnFont = "-fx-font-size: " + Math.max(15, w / 36) + "px;";

            btnEasy.setStyle(btnFont);
            btnMedium.setStyle(btnFont);
            btnHard.setStyle(btnFont);
            btnBack.setStyle("-fx-font-size: " + Math.max(12, w / 45) + "px;");

            contentBox.setSpacing(w * 0.06);
            buttonContainer.setSpacing(w * 0.035);
            contentBox.setMaxWidth(w * 0.6);
        });

        /* ===============================
           Hover animations
           =============================== */
        addHover(btnEasy);
        addHover(btnMedium);
        addHover(btnHard);
    }

    /* ===============================
       Button actions
       =============================== */
    @FXML
    private void easy() {
        press(btnEasy);
        GameSession.setDifficulty(AIDifficulty.EASY);
        Navigation.goTo(Routes.GAMEBOARD);
    }

    @FXML
    private void medium() {
        press(btnMedium);
        GameSession.setDifficulty(AIDifficulty.MEDIUM);
        Navigation.goTo(Routes.GAMEBOARD);
    }

    @FXML
    private void hard() {
        press(btnHard);
        GameSession.setDifficulty(AIDifficulty.HARD);
        Navigation.goTo(Routes.GAMEBOARD);
    }

    @FXML
    private void goBack() {
        press(btnBack);
        Navigation.goTo(Routes.MODE_SELECTION);
    }

    /* ===============================
       Animations helpers
       =============================== */
    private void press(Button btn) {
        ScaleTransition st = new ScaleTransition(Duration.millis(140), btn);
        st.setFromX(1);
        st.setFromY(1);
        st.setToX(0.9);
        st.setToY(0.9);
        st.setAutoReverse(true);
        st.setCycleCount(2);
        st.play();
    }

    private void addHover(Button btn) {
        btn.setOnMouseEntered(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), btn);
            st.setToX(1.1);
            st.setToY(1.1);
            st.play();

            TranslateTransition tt = new TranslateTransition(Duration.millis(200), btn);
            tt.setToY(-10);
            tt.play();
        });

        btn.setOnMouseExited(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), btn);
            st.setToX(1);
            st.setToY(1);
            st.play();

            TranslateTransition tt = new TranslateTransition(Duration.millis(200), btn);
            tt.setToY(0);
            tt.play();
        });
    }
}