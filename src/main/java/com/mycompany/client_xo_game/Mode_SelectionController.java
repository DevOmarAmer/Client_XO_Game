package com.mycompany.client_xo_game;

import com.mycompany.client_xo_game.enums.GameMode;
import com.mycompany.client_xo_game.model.GameSession;
import javafx.fxml.FXML;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.animation.*;
import javafx.util.Duration;
import com.mycompany.client_xo_game.navigation.Navigation;
import com.mycompany.client_xo_game.navigation.Routes;

public class Mode_SelectionController {

    @FXML
    private StackPane rootPane;
    @FXML
    private VBox contentBox;
    @FXML
    private VBox buttonContainer;
    @FXML
    private Label titleLabel;
    @FXML
    private Button btnComputer, btnOffline, btnOnline, btnviewPreviousMatches;

    // --- NEW AUDIO CONTROLS ---
    @FXML
    private Slider volumeSlider;
    @FXML
    private Button btnMute;

    @FXML
    public void initialize() {
        setupAnimations();
        setupResponsiveLayout();
        setupAudioControls();
    }

    private void setupAudioControls() {
        // Init state from App (so it remembers if you muted it previously)
        boolean currentMute = App.isMuted();
        volumeSlider.setValue(App.getVolume());
        volumeSlider.setDisable(currentMute);
        updateMuteIcon(currentMute);

        // Slider Listener: Change volume as user drags
        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (!btnMute.getText().equals("🔇")) { // Only update if not strictly muted
                App.setVolume(newVal.doubleValue());
            }
        });
    }

    @FXML
    private void toggleMute() {
        boolean isNowMuted = App.toggleMute();
        updateMuteIcon(isNowMuted);

        // Disable slider to visually indicate mute
        if (isNowMuted) {
            volumeSlider.setDisable(true);
        } else {
            volumeSlider.setDisable(false);
            volumeSlider.setValue(App.getVolume());
        }
    }

    private void updateMuteIcon(boolean isMuted) {
        if (isMuted) {
            btnMute.setText("🔇");
            btnMute.setStyle("-fx-text-fill: #ff007f; -fx-border-color: #ff007f;");
        } else {
            btnMute.setText("🔊");
            btnMute.setStyle("-fx-text-fill: #00fff0; -fx-border-color: #00fff0;");
        }
    }

    private void setupAnimations() {
        // 1. Entrance Fade-in
        rootPane.setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(1200), rootPane);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();

        // 2. Title breathing animation
        ScaleTransition pulse = new ScaleTransition(Duration.millis(2000), titleLabel);
        pulse.setFromX(1.0);
        pulse.setFromY(1.0);
        pulse.setToX(1.06);
        pulse.setToY(1.06);
        pulse.setCycleCount(Animation.INDEFINITE);
        pulse.setAutoReverse(true);
        pulse.play();

        // 4. Add hover animations to buttons
        addHoverAnimation(btnComputer, "#00d2ff");
        addHoverAnimation(btnOffline, "#00d2ff");
        addHoverAnimation(btnOnline, "#ff007f");
        addHoverAnimation(btnviewPreviousMatches, "#ff007f");
    }

    private void setupResponsiveLayout() {
        rootPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            double width = newVal.doubleValue();

            // Font scaling
            double titleFontSize = Math.max(24, width / 20);
            double btnFontSize = Math.max(16, width / 35);

            titleLabel.setStyle("-fx-font-size: " + titleFontSize + "px;");

            String buttonFontStyle = "-fx-font-size: " + btnFontSize + "px;";
            btnComputer.setStyle(buttonFontStyle);
            btnOffline.setStyle(buttonFontStyle);
            btnOnline.setStyle(buttonFontStyle);
            btnviewPreviousMatches.setStyle(buttonFontStyle);

            // Dynamic spacing
            contentBox.setSpacing(width * 0.06);
            buttonContainer.setSpacing(width * 0.03);

            // Container width
            contentBox.setMaxWidth(width * 0.65);
        });
    }

    // -----------------------------
    // Button navigation
    // -----------------------------
    @FXML
    private void goToLevelSelection() {
        animateButton(btnComputer);
        GameSession.setGameMode(GameMode.HUMAN_VS_COMPUTER_MODE);
        NetworkConnection.setActiveIP("127.0.0.1");
        Navigation.goTo(Routes.LEVEL_SELECTION);
    }

    @FXML
    private void goToOfflinePlayers() {
        animateButton(btnOffline);
        GameSession.setGameMode(GameMode.LOCAL_MODE);
        NetworkConnection.setActiveIP("127.0.0.1");
        Navigation.goTo(Routes.OFFLINE_PLAYERS);
    }

    @FXML
    private void goToLogin() {
        animateButton(btnOnline);
        GameSession.setGameMode(GameMode.ONLINE_MODE);
        Navigation.goTo(Routes.LOGIN);
    }

    @FXML
    private void onViewPreviousMatches() {
        animateButton(btnviewPreviousMatches);
        Navigation.goTo(Routes.GAME_RECORDS_OFFLINE);
    }

    // -----------------------------
    // Helper Animations
    // -----------------------------
    private void animateButton(Button btn) {
        ScaleTransition st = new ScaleTransition(Duration.millis(150), btn);
        st.setFromX(1.0);
        st.setFromY(1.0);
        st.setToX(0.9);
        st.setToY(0.9);
        st.setAutoReverse(true);
        st.setCycleCount(2);
        st.play();
    }

    private void addHoverAnimation(Button btn, String color) {
        btn.setOnMouseEntered(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), btn);
            st.setToX(1.08);
            st.setToY(1.08);
            st.play();

            TranslateTransition tt = new TranslateTransition(Duration.millis(200), btn);
            tt.setToY(-12);
            tt.play();
        });

        btn.setOnMouseExited(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), btn);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();

            TranslateTransition tt = new TranslateTransition(Duration.millis(200), btn);
            tt.setToY(0);
            tt.play();
        });
    }
}
