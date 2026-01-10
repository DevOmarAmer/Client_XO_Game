package com.mycompany.client_xo_game;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import com.mycompany.client_xo_game.navigation.Navigation;
import com.mycompany.client_xo_game.navigation.Routes;
import com.mycompany.client_xo_game.model.CurrentUser;
import org.json.JSONObject;

public class LoginController {

    @FXML
    private StackPane rootPane;
    @FXML
    private VBox contentBox;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField serverIpField;
    @FXML
    private Label messageLabel;
    @FXML
    private Button back_id, loginBtn;
    @FXML
    private Label titleLabel;
    @FXML
    private Hyperlink registerLink;

    @FXML
    public void initialize() {
        rootPane.setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(1200), rootPane);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        ScaleTransition pulse = new ScaleTransition(Duration.millis(2000), titleLabel);
        pulse.setFromX(1.0);
        pulse.setFromY(1.0);
        pulse.setToX(1.06);
        pulse.setToY(1.06);
        pulse.setCycleCount(Animation.INDEFINITE);
        pulse.setAutoReverse(true);
        pulse.play();

        rootPane.widthProperty().addListener((obs, oldVal, newVal) -> scaleComponents(newVal.doubleValue()));
        rootPane.heightProperty().addListener((obs, oldVal, newVal) -> contentBox.setMaxHeight(newVal.doubleValue() * 0.85));

        // -----------------------------
        // 4. Add hover animations
        // -----------------------------
        addHoverAnimation(loginBtn);
    }

    private void scaleComponents(double w) {
        if (w > 1000) {
            contentBox.setMaxWidth(w * 0.4);
        } else {
            contentBox.setMaxWidth(Math.max(400, w * 0.8));
        }

        titleLabel.setStyle("-fx-font-size: " + Math.min(48, Math.max(22, w / 25)) + "px;");

        double inputHeight = Math.max(45, w / 18);
        usernameField.setPrefHeight(inputHeight);
        passwordField.setPrefHeight(inputHeight);
        serverIpField.setPrefHeight(inputHeight);

        double inputFont = Math.max(14, w / 50);
        usernameField.setStyle("-fx-font-size:" + inputFont + "px;");
        passwordField.setStyle("-fx-font-size:" + inputFont + "px;");
        serverIpField.setStyle("-fx-font-size:" + inputFont + "px;");

        double btnHeight = inputHeight + 5;
        loginBtn.setPrefHeight(btnHeight);
        back_id.setPrefHeight(btnHeight);

        double btnFont = Math.max(14, w / 50);
        loginBtn.setStyle("-fx-font-size:" + btnFont + "px;");
        back_id.setStyle("-fx-font-size:" + btnFont + "px;");
    }

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

    private void playExitTransition(Runnable onFinished) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), rootPane);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> onFinished.run());
        fadeOut.play();
    }

    @FXML
    private void handleLogin() {
        if (usernameField.getText().trim().isEmpty() || passwordField.getText().trim().isEmpty()) {
            messageLabel.setText("Arena credentials required!");
            return;
        }

        if (serverIpField.getText().trim().isEmpty()) {
            messageLabel.setText("Server IP address required!");
            return;
        }

        String serverIp = serverIpField.getText().trim();
        NetworkConnection.setActiveIP(serverIp);
        NetworkConnection nc = NetworkConnection.getInstance();
        nc.setListener(this::onServerResponse);

        JSONObject json = new JSONObject();
        json.put("type", "login");
        json.put("username", usernameField.getText().trim());
        json.put("password", passwordField.getText().trim());
        nc.sendMessage(json);
    }

    private void onServerResponse(JSONObject json) {
        if ("login_response".equals(json.optString("type"))) {
            if ("success".equals(json.optString("status"))) {

                String username = usernameField.getText().trim();
                NetworkConnection.getInstance().setCurrentUsername(username);

                System.out.println("Login successful for user: " + username);

                CurrentUser.setUsername(usernameField.getText().trim());
                playExitTransition(() -> Navigation.goTo(Routes.ONLINE_PLAYERS));
            } else {
                messageLabel.setText("Login Failed: " + json.optString("reason", "Unknown error"));
            }
        }
    }

    @FXML
    private void goToRegister() {
        playExitTransition(() -> Navigation.goTo(Routes.REGISTER));
    }

    @FXML
    private void goBack() {
        playExitTransition(() -> Navigation.goTo(Routes.MODE_SELECTION));
    }
}
