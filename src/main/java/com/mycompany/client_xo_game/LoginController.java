package com.mycompany.client_xo_game;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import com.mycompany.client_xo_game.navigation.Navigation;
import com.mycompany.client_xo_game.navigation.Routes;

public class LoginController {

    @FXML private StackPane rootPane;
    @FXML private VBox contentBox;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;
    @FXML private Button back_id, loginBtn;
    @FXML private Label titleLabel;
    @FXML private Hyperlink registerLink;

    @FXML
    public void initialize() {
        // -----------------------------
        // 1. Entrance Fade-in
        // -----------------------------
        rootPane.setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(1200), rootPane);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
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
        rootPane.widthProperty().addListener((obs, oldVal, newVal) -> scaleComponents(newVal.doubleValue()));
        rootPane.heightProperty().addListener((obs, oldVal, newVal) -> contentBox.setMaxHeight(newVal.doubleValue() * 0.85));

        // -----------------------------
        // 4. Add hover animations
        // -----------------------------
        addHoverAnimation(back_id);
        addHoverAnimation(loginBtn);
    }

    private void scaleComponents(double w) {
        // Panel width
        if (w > 1000) contentBox.setMaxWidth(w * 0.4);
        else contentBox.setMaxWidth(Math.max(400, w * 0.8));

        // Title font
        titleLabel.setStyle("-fx-font-size: " + Math.min(48, Math.max(22, w / 25)) + "px;");

        // Input fields
        double inputHeight = Math.max(45, w / 18);
        usernameField.setPrefHeight(inputHeight);
        passwordField.setPrefHeight(inputHeight);

        double inputFont = Math.max(14, w / 50);
        usernameField.setStyle("-fx-font-size:" + inputFont + "px;");
        passwordField.setStyle("-fx-font-size:" + inputFont + "px;");

        // Buttons
        double btnHeight = inputHeight + 5;
        loginBtn.setPrefHeight(btnHeight);
        back_id.setPrefHeight(btnHeight);

        double btnFont = Math.max(14, w / 50);
        loginBtn.setStyle("-fx-font-size:" + btnFont + "px;");
        back_id.setStyle("-fx-font-size:" + btnFont + "px;");
    }

    // -----------------------------
    // Button hover animation
    // -----------------------------
    private void addHoverAnimation(Button btn) {
        btn.setOnMouseEntered(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), btn);
            st.setToX(1.05); st.setToY(1.05);
            st.play();
        });
        btn.setOnMouseExited(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), btn);
            st.setToX(1.0); st.setToY(1.0);
            st.play();
        });
    }

    // -----------------------------
    // Exit transition
    // -----------------------------
    private void playExitTransition(Runnable onFinished) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), rootPane);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> onFinished.run());
        fadeOut.play();
    }

    // -----------------------------
    // Actions
    // -----------------------------
    @FXML
    private void handleLogin() {
        if (usernameField.getText().trim().isEmpty() || passwordField.getText().trim().isEmpty()) {
            messageLabel.setText("Arena credentials required!");
            return;
        }
        playExitTransition(() -> Navigation.goTo(Routes.ONLINE_PLAYERS));
    }

    @FXML private void goToRegister() { playExitTransition(() -> Navigation.goTo(Routes.REGISTER)); }
    @FXML private void goBack() { playExitTransition(() -> Navigation.goTo(Routes.MODE_SELECTION)); }
}
