package com.mycompany.client_xo_game;

import com.mycompany.client_xo_game.util.GameRecorder;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Dialog; 
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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
import org.json.JSONObject;

public class Game_ReplaysController extends AbstractNetworkController {

    @FXML
    private StackPane rootPane;
    @FXML
    private VBox contentBox;
    @FXML
    private ListView<String> replaysList;

    private File[] gameFiles;
    private final DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
    private final DateTimeFormatter isoFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    

    @FXML
    public void initialize() {
        super.setupNetworkListener(); // Setup network listener from the base class

        rootPane.setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(800), rootPane);
        fadeIn.setToValue(1);
        fadeIn.play();

        // 2. Responsive Scaling
        rootPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            double w = newVal.doubleValue();
            contentBox.setMaxWidth(Math.max(600, w * 0.6));
        });

        // 3. Get Username & Load Data
        // currentUsername is now managed by AbstractNetworkController
        System.out.println("Loading replays for user: " + currentUsername);
        loadGameRecords();
    }

    private void loadGameRecords() {
        replaysList.getItems().clear();

        File[] allFiles = GameRecorder.getAllGameRecords();

        if (allFiles.length == 0) {
            replaysList.getItems().add("No game records found");
            gameFiles = new File[0];
            return;
        }

        // Filter records: show ONLY games where current user is player1
        List<File> filteredFiles = new ArrayList<>();

        for (File file : allFiles) {
            JsonObject record = GameRecorder.loadGameRecord(file);

            if (record == null) {
                continue;
            }

            if (!record.containsKey("player1Name") || !record.containsKey("result")) {
                continue;
            }

      
            String player1Name = record.getString("player1Name");

            if (player1Name.equals(currentUsername)) {
                filteredFiles.add(file);
            }
        }

        if (filteredFiles.isEmpty()) {
            replaysList.getItems().add("No replays found for " + currentUsername);
            gameFiles = new File[0];
            return;
        }

        // Convert to array and sort by date (newest first)
        gameFiles = filteredFiles.toArray(new File[0]);
        Arrays.sort(gameFiles, (f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));

        // Display records
        for (File file : gameFiles) {
            JsonObject record = GameRecorder.loadGameRecord(file);

            LocalDateTime gameTime = LocalDateTime.parse(
                    record.getString("gameStartTime"),
                    isoFormatter
            );
            String dateStr = gameTime.format(displayFormatter);

            // Clean up Player 2 name if it has [ONLINE] tag
            String player2 = record.getString("player2Name");
            if (player2.startsWith("[ONLINE]")) {
                player2 = player2.substring(8).trim();
            }

         
            String resultText = record.getString("result");
            String icon = "➖"; // Default for draw
            if (resultText.toLowerCase().contains("win")) {
                icon = "🏆";
            } else if (resultText.toLowerCase().contains("lose") || resultText.toLowerCase().contains("lost")) {
                icon = "❌";
            }

            // Format: "Vs OpponentName - Date - Result"
            String displayText = String.format("Vs %s - %s - %s %s",
                    player2,
                    dateStr,
                    resultText,
                    icon
            );

            replaysList.getItems().add(displayText);
        }
    }

   
@FXML
private void handleWatch() {
    int selectedIndex = replaysList.getSelectionModel().getSelectedIndex();

    // Check if no selection or invalid index
    if (selectedIndex < 0 || gameFiles == null || selectedIndex >= gameFiles.length) {
        showAlert(Alert.AlertType.INFORMATION, "Info", "Please select a replay to watch");
        return;
    }

    File selectedFile = gameFiles[selectedIndex];
    JsonObject record = GameRecorder.loadGameRecord(selectedFile);

    if (record == null) {
        showAlert(Alert.AlertType.ERROR, "Error", "Error loading replay");
        return;
    }

    System.out.println("Loading replay: " + selectedFile.getName());

    // Navigate to Gameboard with Replay Data
    playExitTransition(() -> {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/client_xo_game/Gameboard.fxml"));
            Parent root = loader.load();

            GameboardController controller = loader.getController();
            controller.setReplayMode(record);

            if (rootPane.getScene() != null) {
                rootPane.getScene().setRoot(root);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Error loading replay: " + e.getMessage());
        }
    });
}

    @FXML
    private void handleDelete() {
        int selectedIndex = replaysList.getSelectionModel().getSelectedIndex();

        if (selectedIndex < 0 || gameFiles == null || selectedIndex >= gameFiles.length) {
          
             showAlert(Alert.AlertType.NONE, "Info", "Please select a replay to delete");

            return;
        }

        File selectedFile = gameFiles[selectedIndex];

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);

        ClientUtils.styleAlert(confirmAlert);
       

        confirmAlert.setTitle("Delete Replay");
        confirmAlert.setHeaderText("Delete this replay?");
        confirmAlert.setContentText("This cannot be undone.");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response.getButtonData().isDefaultButton()) {
                if (selectedFile.delete()) {
                    loadGameRecords(); // Refresh list
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete file.");
                }
            }
        });
    }



    @Override
    protected void handleGenericResponse(JSONObject response) {
        // No specific handling needed for generic responses in Game_ReplaysController
        // super.handleGenericResponse(response); // Call super if you want default logging
    }

    // ==========================================
    //  STYLING HELPER
    // ==========================================




    @FXML
    private void goBack() {
        // Navigates back to Online Players (Lobby)
        playExitTransition(() -> Navigation.goTo(Routes.ONLINE_PLAYERS));
    }

    private void playExitTransition(Runnable onFinished) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), rootPane);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> onFinished.run());
        fadeOut.play();
    }
}
