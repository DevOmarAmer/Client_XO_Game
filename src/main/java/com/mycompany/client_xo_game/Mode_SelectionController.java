package com.mycompany.client_xo_game;

import javafx.fxml.FXML;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.animation.*;
import javafx.util.Duration;
import com.mycompany.client_xo_game.navigation.Navigation;
import com.mycompany.client_xo_game.navigation.Routes;

public class Mode_SelectionController {

    @FXML private StackPane rootPane;
    @FXML private VBox contentBox;
    @FXML private VBox buttonContainer;
    @FXML private Label titleLabel;

    @FXML private Button btnComputer, btnOffline, btnOnline;

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
        pulse.setFromX(1.0); pulse.setFromY(1.0);
        pulse.setToX(1.06); pulse.setToY(1.06);
        pulse.setCycleCount(Animation.INDEFINITE);
        pulse.setAutoReverse(true);
        pulse.play();

        // -----------------------------
        // 3. Responsive scaling
        // -----------------------------
        rootPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            double width = newVal.doubleValue();

            // Font scaling
            double titleFontSize = Math.max(24, width / 13);
            double btnFontSize = Math.max(16, width / 35);

            titleLabel.setStyle("-fx-font-size: " + titleFontSize + "px;");

            String buttonFontStyle = "-fx-font-size: " + btnFontSize + "px;";
            btnComputer.setStyle(buttonFontStyle);
            btnOffline.setStyle(buttonFontStyle);
            btnOnline.setStyle(buttonFontStyle);

            // Dynamic spacing
            contentBox.setSpacing(width * 0.06);
            buttonContainer.setSpacing(width * 0.03);

            // Container width
            contentBox.setMaxWidth(width * 0.65);
        });

        // -----------------------------
        // 4. Add hover animations to buttons
        // -----------------------------
        addHoverAnimation(btnComputer, "#00d2ff");
        addHoverAnimation(btnOffline, "#00d2ff");
        addHoverAnimation(btnOnline, "#ff007f");
    }

    // -----------------------------
    // Button navigation
    // -----------------------------
    @FXML private void goToLevelSelection() { 
        animateButton(btnComputer); 
        Navigation.goTo(Routes.LEVEL_SELECTION); 
    }

    @FXML private void goToOfflinePlayers() { 
        animateButton(btnOffline); 
        Navigation.goTo(Routes.OFFLINE_PLAYERS); 
    }

    @FXML private void goToLogin() { 
        animateButton(btnOnline); 
        Navigation.goTo(Routes.LOGIN); 
    }

    // -----------------------------
    // Smooth button press animation
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

    // -----------------------------
    // Smooth hover animation
    // -----------------------------
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
