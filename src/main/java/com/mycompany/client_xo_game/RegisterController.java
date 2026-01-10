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
import org.json.JSONObject;
import javafx.application.Platform;

public class RegisterController {

    @FXML private StackPane rootPane;
    @FXML private VBox contentBox;
    @FXML private GridPane inputGrid;
    @FXML private TextField usernameField, emailField;
    @FXML private PasswordField passwordField, confirmPasswordField;
    @FXML private Label messageLabel;
    @FXML private Button registerBtn;
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

        // Email validation
        if (!emailField.getText().trim().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            messageLabel.setText("Invalid email format!");
            return;
        }

        // Password length validation
        if (passwordField.getText().trim().length() < 6) {
            messageLabel.setText("Password must be at least 6 characters long!");
            return;
        }

        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            messageLabel.setText("Passwords do not match!");
            return;
        }

        // 1. Set Listener for Server Response
        NetworkConnection.getInstance().setListener(this::onServerResponse);

        // 2. Send Register Request
        JSONObject json = new JSONObject();
        json.put("type", "register");
        json.put("username", usernameField.getText().trim());
        json.put("email", emailField.getText().trim());
        json.put("password", passwordField.getText().trim());
        
        System.out.println("[Client] Sending Register Request: " + json.toString());
        NetworkConnection.getInstance().sendMessage(json);
    }

    private void onServerResponse(JSONObject json) {
        System.out.println("[Client] Received Response: " + json.toString());
        if ("register_response".equals(json.optString("type"))) {
            if ("success".equals(json.optString("status"))) {
                System.out.println("[Client] Registration Success. Navigating to Login.");
                // Only navigate on success
                playExitTransition(() -> Navigation.goTo(Routes.LOGIN));
            } else {
                String reason = json.optString("reason", "Unknown Error");
                System.out.println("[Client] Registration Failed: " + reason);
                Platform.runLater(() -> messageLabel.setText("Failed: " + reason));
            }
        }
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
