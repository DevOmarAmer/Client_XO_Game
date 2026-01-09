package com.mycompany.client_xo_game;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import com.mycompany.client_xo_game.navigation.Navigation;
import com.mycompany.client_xo_game.navigation.Routes;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.scene.layout.VBox;
import org.json.JSONObject;

public class ProfileController {

    @FXML
    private StackPane rootPane;
    @FXML
    private Label usernameLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Label scoreLabel;
    @FXML
    private Label winsLabel, drawsLabel, lossesLabel;
    @FXML
    private VBox contentBox;
    @FXML
    private Button deleteAccountBtn;

    public void initialize() {
        // 1. Entrance Animation
        rootPane.setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(800), rootPane);
        fadeIn.setToValue(1);
        fadeIn.play();

        // 2. Setup Network Listener
        NetworkConnection.getInstance().setListener(this::onServerResponse);

        // 3. Request Data from Server
        requestProfileData();
    }

    private void requestProfileData() {
        JSONObject req = new JSONObject();
        req.put("type", "get_profile");
        NetworkConnection.getInstance().sendMessage(req);
    }

    private void onServerResponse(JSONObject json) {
        String type = json.optString("type");

        Platform.runLater(() -> {
            switch (type) {
                case "profile_response":
                    if ("success".equals(json.optString("status"))) {
                        updateUI(json);
                    }
                    break;
                case "update_profile_response":
                    if ("success".equals(json.optString("status"))) {
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Profile Updated Successfully!");
                        requestProfileData(); // Refresh data to show changes
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Update Failed", "Could not update profile. Try again.");
                    }
                    break;
                case "delete_account_response":
                    if ("success".equals(json.optString("status"))) {
                        showAlert(Alert.AlertType.INFORMATION, "Account Deleted", "Your account has been permanently deleted.");
                        playExitTransition(() -> Navigation.goTo(Routes.LOGIN));
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete account.");
                    }
                    break;
            }
        });
    }

    private void updateUI(JSONObject data) {
        usernameLabel.setText(data.optString("username"));
        emailLabel.setText(data.optString("email"));
        scoreLabel.setText(String.valueOf(data.optInt("score")));
        winsLabel.setText(String.valueOf(data.optInt("wins")));
        drawsLabel.setText(String.valueOf(data.optInt("draws")));
        lossesLabel.setText(String.valueOf(data.optInt("losses")));
    }

    // ==========================================
    //  EDIT PROFILE LOGIC
    // ==========================================
    @FXML
    private void handleEdit() {
        // Create a Custom Dialog
        Dialog<JSONObject> dialog = new Dialog<>();
        dialog.setTitle("Edit Profile");
        dialog.setHeaderText("Update your account details");

        // 1. APPLY STYLING HERE using the new helper method
        styleDialog(dialog);

        // Set the button types
        ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        // Create the layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Optional: Style the internal grid if needed
        // grid.setStyle("-fx-text-fill: white;"); 
        // Create fields
        TextField usernameField = new TextField();
        usernameField.setPromptText("New Username");
        usernameField.setText(usernameLabel.getText()); // Pre-fill current data
        // Add style class to text fields if you have one in your CSS (e.g., "neon-input")
        usernameField.getStyleClass().add("text-field");

        TextField emailField = new TextField();
        emailField.setPromptText("New Email");
        emailField.setText(emailLabel.getText()); // Pre-fill current data

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("New Password (Optional)");

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm Password");

        // Add to grid
        // Note: Labels inside the grid might need white text styling depending on your CSS
        grid.add(new Label("Username:"), 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(new Label("Password:"), 0, 2);
        grid.add(passwordField, 1, 2);
        grid.add(new Label("Confirm Pass:"), 0, 3);
        grid.add(confirmPasswordField, 1, 3);

        dialog.getDialogPane().setContent(grid);

        // Request focus on the username field by default
        Platform.runLater(usernameField::requestFocus);

        // Validate Result Converter
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
                String user = usernameField.getText().trim();
                String email = emailField.getText().trim();
                String pass = passwordField.getText().trim();
                String confirm = confirmPasswordField.getText().trim();

                // Basic Validation
                if (user.isEmpty() || email.isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "Validation Error", "Username and Email cannot be empty.");
                    return null;
                }

                // Password Validation
                if (!pass.isEmpty() && !pass.equals(confirm)) {
                    showAlert(Alert.AlertType.ERROR, "Validation Error", "Passwords do not match!");
                    return null;
                }

                // Create JSON for server
                JSONObject newDetails = new JSONObject();
                newDetails.put("username", user);
                newDetails.put("email", email);
                newDetails.put("password", pass); // Send empty string if no change
                return newDetails;
            }
            return null;
        });

        // Show Dialog and Process Result
        Optional<JSONObject> result = dialog.showAndWait();
        result.ifPresent(newDetails -> {
            JSONObject req = new JSONObject();
            req.put("type", "update_profile");
            req.put("username", newDetails.getString("username"));
            req.put("email", newDetails.getString("email"));
            req.put("password", newDetails.getString("password"));

            NetworkConnection.getInstance().sendMessage(req);
        });
    }

    // ==========================================
    //  DELETE ACCOUNT LOGIC
    // ==========================================
    private void handleDelete() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        styleDialog(alert); // Updated to use the generic method

        alert.setTitle("Delete Account");
        alert.setHeaderText("CRITICAL WARNING!");
        alert.setContentText("Are you sure you want to permanently delete your account?\n\nThis action cannot be undone and you will lose all your progress.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            JSONObject req = new JSONObject();
            req.put("type", "delete_account");
            NetworkConnection.getInstance().sendMessage(req);
        }
    }

    @FXML
    private void handleLogout() {
        JSONObject req = new JSONObject();
        req.put("type", "logout");
        NetworkConnection.getInstance().sendMessage(req);

        playExitTransition(() -> Navigation.goTo(Routes.LOGIN));
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

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        styleDialog(alert); // Updated to use the generic method

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // ==========================================
    //  STYLING METHODS
    // ==========================================
    // Original method redirected to new one
    private void styleAlert(Alert alert) {
        styleDialog(alert);
    }

    // NEW GENERIC METHOD: Works for both Alerts and Dialogs
    private void styleDialog(Dialog<?> dialog) {
        var dialogPane = dialog.getDialogPane();
        dialogPane.setId("xo-alert"); // Reuses the ID from your CSS
        dialogPane.getStylesheets().add(
                getClass().getResource("/styles/alert.css").toExternalForm()
        );
    }

    @FXML
    private void handleDeleteAccount(ActionEvent event) {
        handleDelete();
    }
}
