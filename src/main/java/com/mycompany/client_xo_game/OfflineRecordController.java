package com.mycompany.client_xo_game;

import com.mycompany.client_xo_game.util.GameRecorder;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Dialog; // Import Dialog for the helper method
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import com.mycompany.client_xo_game.navigation.Navigation;
import com.mycompany.client_xo_game.navigation.Routes;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import jakarta.json.JsonObject;
import javafx.stage.StageStyle;
import com.mycompany.client_xo_game.util.ClientUtils;

public class OfflineRecordController {

    @FXML
    private StackPane rootPane;
    @FXML
    private ListView<String> recordsList;

    private File[] gameFiles;
    private DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
    private DateTimeFormatter isoFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @FXML
    public void initialize() {
        rootPane.setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(800), rootPane);
        fadeIn.setToValue(1);
        fadeIn.play();

        loadGameRecords();
    }

    private void loadGameRecords() {
        recordsList.getItems().clear();

        // 1. Get all raw files
        File[] allFiles = GameRecorder.getAllGameRecords();

        // 2. Filter: Exclude any file that starts with "ONLINE"
        List<File> offlineFiles = new ArrayList<>();
        if (allFiles != null) {
            for (File file : allFiles) {
                if (!file.getName().startsWith("ONLINE")) {
                    offlineFiles.add(file);
                }
            }
        }

        // 3. Assign filtered list to class variable
        gameFiles = offlineFiles.toArray(new File[0]);

        if (gameFiles.length == 0) {
            recordsList.getItems().add("No offline game records found");
            return;
        }

        // 4. Sort by date (Newest first)
        Arrays.sort(gameFiles, (f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));

        // 5. Display
        for (File file : gameFiles) {
            JsonObject record = GameRecorder.loadGameRecord(file);

            if (record == null) {
                System.err.println("Error: Invalid record in " + file.getName());
                continue;
            }

            if (!record.containsKey("player1Name") || !record.containsKey("result")) {
                System.err.println("Error: Missing required fields in " + file.getName());
                continue;
            }

            LocalDateTime gameTime = LocalDateTime.parse(
                    record.getString("gameStartTime"),
                    isoFormatter
            );
            String dateStr = gameTime.format(displayFormatter);

            String displayText = String.format("%s vs %s - %s - %s",
                    record.getString("player1Name"),
                    record.getString("player2Name"),
                    dateStr,
                    record.getString("result")
            );

            recordsList.getItems().add(displayText);
        }
    }

    @FXML
    private void handleViewRecord() {
        int selectedIndex = recordsList.getSelectionModel().getSelectedIndex();

        if (selectedIndex < 0 || gameFiles == null || selectedIndex >= gameFiles.length) {
            showAlert("Please select a game record to view");
            return;
        }

        File selectedFile = gameFiles[selectedIndex];
        JsonObject record = GameRecorder.loadGameRecord(selectedFile);

        if (record == null) {
            showAlert("Error loading game record");
            return;
        }

        playExitTransition(() -> {
            try {
                javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                        getClass().getResource("/com/mycompany/client_xo_game/Gameboard.fxml")
                );

                javafx.scene.Parent root = loader.load();

                GameboardController controller = loader.getController();

                controller.setReplayMode(record);

                if (rootPane.getScene() != null) {
                    rootPane.getScene().setRoot(root);
                }

            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error loading replay: " + e.getMessage());
            }
        });
    }

    @FXML
    private void handleDelete() {
        int selectedIndex = recordsList.getSelectionModel().getSelectedIndex();

        if (selectedIndex < 0 || gameFiles == null || selectedIndex >= gameFiles.length) {
            showAlert("Please select a game record to delete");
            return;
        }

        File selectedFile = gameFiles[selectedIndex];

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);

        // --- STYLE APPLIED HERE ---
        ClientUtils.styleAlert(confirmAlert);
        // --------------------------

        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText("Delete Game Record");
        confirmAlert.setContentText("Are you sure you want to delete this game record?");

        confirmAlert.showAndWait().ifPresent(response -> {
            // Using standard ButtonType check which works reliably with AlertType.CONFIRMATION
            if (response.getButtonData().isDefaultButton()) {
                if (selectedFile.delete()) {
                    showAlert("Game record deleted successfully");
                    loadGameRecords();
                } else {
                    showAlert("Failed to delete game record");
                }
            }
        });
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);

        // --- STYLE APPLIED HERE ---
        ClientUtils.styleAlert(alert);
        // --------------------------

        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ==========================================
    //  STYLING HELPER
    // ==========================================




    @FXML
    private void handleRefresh() {
        loadGameRecords();
    }

    @FXML
    private void goBack() {
        playExitTransition(() -> Navigation.goTo(Routes.MODE_SELECTION));
    }

    private void playExitTransition(Runnable onFinished) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), rootPane);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> onFinished.run());
        fadeOut.play();
    }
}
