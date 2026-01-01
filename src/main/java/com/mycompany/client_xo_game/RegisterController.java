package com.mycompany.client_xo_game;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import com.mycompany.client_xo_game.navigation.Navigation;
import com.mycompany.client_xo_game.navigation.Routes;

public class RegisterController {

    @FXML private StackPane rootPane;
    @FXML private VBox contentBox;
    @FXML private GridPane inputGrid;
    @FXML private TextField usernameField, emailField;
    @FXML private PasswordField passwordField, confirmPasswordField;
    @FXML private Label messageLabel;
    @FXML private Button registerBtn, back_id;
    @FXML private Label titleLabel;
    @FXML private Hyperlink loginLink;

    @FXML
    public void initialize() {
        // Fade-in entrance
        rootPane.setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(1200), rootPane);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        // Title pulse animation
        ScaleTransition pulse = new ScaleTransition(Duration.millis(2000), titleLabel);
        pulse.setFromX(1.0); pulse.setFromY(1.0);
        pulse.setToX(1.06); pulse.setToY(1.06);
        pulse.setCycleCount(Animation.INDEFINITE);
        pulse.setAutoReverse(true);
        pulse.play();

        // Responsive scaling
        rootPane.widthProperty().addListener((obs, oldVal, newVal) -> scaleComponents(newVal.doubleValue()));
        rootPane.heightProperty().addListener((obs, oldVal, newVal) -> contentBox.setMaxHeight(newVal.doubleValue() * 0.85));

        // Hover animations
        addHoverAnimation(registerBtn);
        addHoverAnimation(back_id);
    }

    private void scaleComponents(double w) {
        contentBox.setMaxWidth(Math.max(500, w * 0.85));
        titleLabel.setStyle("-fx-font-size:" + Math.min(48, Math.max(24, w / 25)) + "px;");

        double inputHeight = Math.max(45, w / 18);
        double inputFont = Math.max(14, w / 55);

        usernameField.setPrefHeight(inputHeight);
        emailField.setPrefHeight(inputHeight);
        passwordField.setPrefHeight(inputHeight);
        confirmPasswordField.setPrefHeight(inputHeight);

        String inputStyle = "-fx-font-size:" + inputFont + "px;";
        usernameField.setStyle(inputStyle);
        emailField.setStyle(inputStyle);
        passwordField.setStyle(inputStyle);
        confirmPasswordField.setStyle(inputStyle);

        registerBtn.setPrefHeight(inputHeight + 5);
        registerBtn.setStyle("-fx-font-size:" + Math.max(14, w / 50) + "px;");

        contentBox.setSpacing(Math.min(20, w / 40));
        inputGrid.setHgap(Math.min(15, w / 50));
        inputGrid.setVgap(Math.min(15, w / 50));
    }

    private void addHoverAnimation(Button btn) {
        btn.setOnMouseEntered(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), btn);
            st.setToX(1.05); st.setToY(1.05); st.play();
        });
        btn.setOnMouseExited(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), btn);
            st.setToX(1.0); st.setToY(1.0); st.play();
        });
    }

    private void playExitTransition(Runnable onFinished) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), rootPane);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> onFinished.run());
        fadeOut.play();
    }

    @FXML
    private void handleRegister() {
        if (usernameField.getText().trim().isEmpty() || emailField.getText().trim().isEmpty() ||
            passwordField.getText().trim().isEmpty() || confirmPasswordField.getText().trim().isEmpty()) {
            messageLabel.setText("All fields are required!");
            return;
        }

        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            messageLabel.setText("Passwords do not match!");
            return;
        }

        // Add registration logic here
        playExitTransition(() -> Navigation.goTo(Routes.LOGIN));
    }

    @FXML
    private void goToLogin() {
        playExitTransition(() -> Navigation.goTo(Routes.LOGIN));
    }

    @FXML
    private void goBack() {
        playExitTransition(() -> Navigation.goTo(Routes.MODE_SELECTION));
    }
}
