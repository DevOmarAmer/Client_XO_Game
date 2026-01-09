package com.mycompany.client_xo_game;

import com.mycompany.client_xo_game.enums.AIDifficulty;
import com.mycompany.client_xo_game.enums.Cell;
import com.mycompany.client_xo_game.enums.GameMode;
import com.mycompany.client_xo_game.game_engine.Board;
import com.mycompany.client_xo_game.game_engine.Minimax;
import com.mycompany.client_xo_game.model.GameSession;
import com.mycompany.client_xo_game.model.Move;
import com.mycompany.client_xo_game.model.Player_Offline;
import com.mycompany.client_xo_game.navigation.Navigation;
import com.mycompany.client_xo_game.navigation.Routes;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.animation.Animation;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import org.json.JSONObject;

public class GameboardController implements Initializable {

    @FXML
    private StackPane rootPane;
    @FXML
    private Label turnLabel;
    @FXML
    private HBox playersBar;
    @FXML
    private Label playerNameP1;
    @FXML
    private Label playerNameP2;
    @FXML
    private Label scoreP1;
    @FXML
    private Label scoreP2;
    @FXML
    private StackPane boardContainer;
    @FXML
    private GridPane board;
    @FXML
    private HBox replayControls;
    @FXML
    private Button playButton;
    @FXML
    private Button pauseButton;
    @FXML
    private Button resetButton;
    @FXML
    private HBox cardP1;
    @FXML
    private HBox cardP2;

    private Board gameBoard;
    private Minimax ai;
    private GameMode mode;
    private AIDifficulty difficulty;
    private boolean Xturn = true;
    private Image xImage;
    private Image oImage;
    private Player_Offline player1;
    private Player_Offline player2;
    private boolean gameEnded = false;
    //online session scoring
    private int mySessionScore = 0;
    private int opponentSessionScore = 0;
    // Replay mode fields
    private boolean isReplayMode = false;
    private JsonObject replayRecord;
    private int currentReplayIndex = 0;
    private boolean isReplaying = false;
    private PauseTransition currentTransition = null;
    private static final double REPLAY_DELAY = 1.0;
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    // Online mode fields
    private boolean isOnlineMode = false;
    private String opponentName;
    private String mySymbol;
    private boolean isMyTurn;
    private String myUsername;

    //online mode functions
    //called when navigating
    public void setOnlineMode(String opponent, String symbol, boolean myTurn) {
        this.isOnlineMode = true;
        this.opponentName = opponent;
        this.mySymbol = symbol;
        this.isMyTurn = myTurn;
        this.mode = GameMode.ONLINE_MODE;

        this.myUsername = NetworkConnection.getInstance().getCurrentUsername();
        //in case am not able to get players username
        if (this.myUsername == null || this.myUsername.isEmpty()) {
            System.err.println("ERROR: Username not found!");
            this.myUsername = "Player";
        }

        System.out.println("Online mode set for: " + myUsername + " vs " + opponent);
        //allows background threads to update ui
        Platform.runLater(() -> {
            updateOnlinePlayersLabels();
            setupOnlineNetworkListener();
            updateOnlineScoreBoard();
        });
    }

    private void setupOnlineNetworkListener() {
        NetworkConnection.getInstance().setListener(response -> {
            //opt string is safer than getString in case no type is returned
            String type = response.optString("type");
            //updates ui so its better to be seperated
            Platform.runLater(() -> {
                switch (type) {
                    case "move":
                        handleOpponentMove(response);
                        break;
                    case "game_over":
                        handleGameOver(response);
                        break;
                    case "turn_update":
                        handleTurnUpdate(response);
                        break;
                    case "rematch_start":
                        handleRematchStart(response);
                        break;
                    case "error":
                        handleError(response);
                        break;
                }
            });
        });
    }

    private void updateOnlineScoreBoard() {
        if (scoreP1 != null && scoreP2 != null) {
            // Display session scores that accumulate during play
            scoreP1.setText(String.valueOf(mySessionScore));
            scoreP2.setText(String.valueOf(opponentSessionScore));
        }
    }

    private void handleRematchStart(JSONObject response) {
        gameEnded = false;
        gameBoard = new Board();

        clearHighlights();
        //clear the board for a new game
        for (var node : board.getChildren()) {
            if (node instanceof StackPane) {
                ((StackPane) node).getChildren().clear();
            }
        }

        isMyTurn = response.getBoolean("yourTurn");
        updateOnlinePlayersLabels();

        // session scores persist across rematches for display
        updateOnlineScoreBoard();
    }

    private void updateScoreBoard() {
        if (isReplayMode) {
            return;
        }

        if (isOnlineMode) {
            updateOnlineScoreBoard();
        } else {
            // Offline mode uses GameSession
            scoreP1.setText(String.valueOf(GameSession.getScoreP1()));
            scoreP2.setText(String.valueOf(GameSession.getScoreP2()));
        }
    }

    private void handleOpponentMove(JSONObject response) {
        int row = response.getInt("row");
        int col = response.getInt("col");
        String symbol = response.getString("symbol");

        StackPane cell = getCell(row, col);
        if (cell != null && cell.getChildren().isEmpty()) {
            Cell cellSymbol = "X".equals(symbol) ? Cell.X : Cell.O;
            placeMove(cell, cellSymbol);
            gameBoard.getGrid()[row][col] = cellSymbol;

            // record move if recording is active
            if (GameSession.isRecording()) {
                GameSession.recordMove(row, col, symbol, opponentName);
            }
        }
    }

    private void handleTurnUpdate(JSONObject response) {
        isMyTurn = response.getBoolean("yourTurn");
        updateOnlinePlayersLabels();
    }

    private void handleGameOver(JSONObject response) {
        gameEnded = true;
        String result = response.getString("result");

        // Check BOTH possible forfeit flags from server
        boolean opponentForfeited = response.optBoolean("opponent_forfeited", false);
        boolean isForfeit = response.optBoolean("forfeit", false) || opponentForfeited;
        String forfeiter = response.optString("forfeiter", "");

        System.out.println("DEBUG: Game Over - Result: " + result + ", Opponent Forfeited: " + opponentForfeited + ", Is Forfeit: " + isForfeit + ", Forfeiter: " + forfeiter);

        String finalResult = "";
        int scoreToAdd = 0;

        // Calculate score for THIS game (for display purposes only)
        switch (result) {
            case "win":
                if (isForfeit || opponentForfeited) {
                    // Opponent quit - show forfeit message
                    turnLabel.setText(opponentName + " Forfeited - You Win!");
                    finalResult = myUsername + " Wins (Opponent Forfeited)";
                    System.out.println("INFO: Opponent forfeited, you win!");
                    // Don't highlight cells for forfeit win
                } else {
                    // Normal win
                    turnLabel.setText("You Win!");
                    finalResult = myUsername + " Wins";
                    highlightWinningCells("X".equals(mySymbol) ? Cell.X : Cell.O);
                }
                mySessionScore += 10; // You get +10 for winning
                opponentSessionScore -= 5; // Opponent gets -5 for losing (estimated)
                break;

            case "lose":
                if (isForfeit || (forfeiter != null && forfeiter.equals(myUsername))) {
                    // You forfeited
                    turnLabel.setText("You Forfeited - You Lost!");
                    finalResult = opponentName + " Wins (You Forfeited)";
                    System.out.println("INFO: You forfeited the game");
                } else {
                    // Normal loss
                    turnLabel.setText("You Lost!");
                    finalResult = opponentName + " Wins";
                    highlightWinningCells("X".equals(mySymbol) ? Cell.O : Cell.X);
                }
                mySessionScore -= 5; // You get -5 for losing
                opponentSessionScore += 10; // Opponent gets +10 for winning (estimated)
                break;

            case "draw":
                turnLabel.setText("It's a Draw!");
                finalResult = "Draw";
                mySessionScore += 2; // You get +2 for draw
                opponentSessionScore += 2; // Opponent gets +2 for draw (estimated)
                break;
        }

        System.out.println("DEBUG: Scores updated - My Score: " + mySessionScore + ", Opponent Score: " + opponentSessionScore);

        // Update UI with new session scores IMMEDIATELY
        updateOnlineScoreBoard();

        if (GameSession.isRecording() && GameSession.isRecordingInitiator(myUsername)) {
            String filePath = GameSession.saveGameRecord(finalResult);
            if (filePath != null) {
                System.out.println("Game saved to: " + filePath);
            }
        }

        // Show dialog with forfeit information
        boolean won = result.equals("win");
        boolean draw = result.equals("draw");
        showOnlineGameOverDialog(won, draw, isForfeit || opponentForfeited, forfeiter);
    }

    private void showOnlineGameOverDialog(boolean won, boolean draw, boolean isForfeit, String forfeiter) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        styleAlert(alert);
        alert.setTitle("Game Over");

        // Special case: opponent forfeited and you won - show simple OK dialog
        if (isForfeit && won) {
            alert.setHeaderText("Your Opponent Forfeited!");
            alert.setContentText(opponentName + " has forfeited. You have been awarded the victory!");

            ButtonType okBtn = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            alert.getButtonTypes().setAll(okBtn);

            alert.showAndWait().ifPresent(button -> {
                goBack(); // Simply go back to online players list
            });
            return; // Exit method - don't show play again options
        }

        // For all other cases (normal win/loss/draw, or you forfeited)
        if (draw) {
            alert.setHeaderText("It's a Draw!");
            alert.setContentText("Would you like to play again?");
        } else if (won) {
            alert.setHeaderText("You Win!");
            alert.setContentText("Would you like to play again?");
        } else if (isForfeit) {
            // You lost by forfeit
            alert.setHeaderText("You Lost by Forfeit!");
            alert.setContentText("You have forfeited the game. Would you like to play again?");
        } else {
            // Normal loss
            alert.setHeaderText("You Lost!");
            alert.setContentText("Would you like to play again?");
        }

        ButtonType playAgainBtn = new ButtonType("Play Again");
        ButtonType closeBtn = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(playAgainBtn, closeBtn);

        alert.showAndWait().ifPresent(button -> {
            if (button == playAgainBtn) {

                turnLabel.setText("Waiting for opponent to rematch...");

                JSONObject playAgain = new JSONObject();
                playAgain.put("type", "play_again");
                NetworkConnection.getInstance().sendMessage(playAgain);
            } else {
                // Game is already over, just send a clean disconnect
                JSONObject disconnect = new JSONObject();
                disconnect.put("type", "end_session");
                NetworkConnection.getInstance().sendMessage(disconnect);

                System.out.println("----------No Penalty (Game Already Ended)--------------");
                goBack();
            }
        });
    }

    private void handleError(JSONObject response) {
        String message = response.optString("message", "An error occurred");
        System.err.println("Game error: " + message);
    }

    private void updateOnlinePlayersLabels() {
        if (playerNameP1 != null && playerNameP2 != null) {
            playerNameP1.setText("You (" + mySymbol + ")");
            playerNameP2.setText(opponentName + " (" + ("X".equals(mySymbol) ? "O" : "X") + ")");
            turnLabel.setText(isMyTurn ? "Your Turn" : opponentName + "'s Turn");
        }
    }

    public void updatePlayersLabels() {
        if (isOnlineMode) {
            updateOnlinePlayersLabels();
            return;
        }

        if (isReplayMode) {
            return;
        }

        if (mode == GameMode.LOCAL_MODE) {
            if (player1 != null && player2 != null) {
                if (playerNameP1 != null && playerNameP2 != null) {
                    playerNameP1.setText(player1.getName());
                    playerNameP2.setText(player2.getName());
                    turnLabel.setText(Xturn ? player1.getName() + "'s Turn" : player2.getName() + "'s Turn");
                }
            }
        } else if (mode == GameMode.HUMAN_VS_COMPUTER_MODE) {
            if (playerNameP1 != null && playerNameP2 != null) {
                playerNameP1.setText("You");
                playerNameP2.setText("Computer");
                turnLabel.setText(Xturn ? "Your Turn" : "Computer's Turn");
            }
        }
    }

    // ==================== INITIALIZATION ====================
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("GameboardController initialize() called");

        xImage = new Image(getClass().getResourceAsStream("/assets/X.png"));
        oImage = new Image(getClass().getResourceAsStream("/assets/O.png"));

        if (replayControls != null) {
            replayControls.setVisible(false);
            replayControls.setManaged(false);
        }

        if (!isReplayMode && !isOnlineMode) {
            gameBoard = new Board();
            ai = new Minimax();
            mode = GameSession.getGameMode();
            difficulty = GameSession.getDifficulty();
            player1 = GameSession.getPlayer1();
            player2 = GameSession.getPlayer2();
            gameEnded = false;

            updatePlayersLabels();
            updateScoreBoard();
        } else if (!isReplayMode && isOnlineMode) {
            gameBoard = new Board();
            gameEnded = false;
            mySessionScore = 0; // Start fresh session
            opponentSessionScore = 0;
            updatePlayersLabels();
            updateOnlineScoreBoard();
        }
    }

    // ==================== REPLAY MODE ====================
    public void setReplayMode(JsonObject record) {
        this.isReplayMode = true;
        this.replayRecord = record;
        this.currentReplayIndex = 0;
        this.isReplaying = false;

        System.out.println("DEBUG: Starting setReplayMode...");

        if (board != null) {
            for (var node : board.getChildren()) {
                if (node instanceof StackPane) {
                    ((StackPane) node).getChildren().clear();
                }
            }
        }

        player1 = new Player_Offline(record.getString("player1Name"), Cell.X);
        player2 = new Player_Offline(record.getString("player2Name"), Cell.O);

        if (replayControls != null) {
            replayControls.setVisible(true);
            replayControls.setManaged(true);
            replayControls.toFront();

            if (rootPane != null) {
                rootPane.requestLayout();
            }
            System.out.println("DEBUG: replayControls set to VISIBLE");
        } else {
            System.err.println("CRITICAL ERROR: replayControls is NULL! Check your FXML fx:id");
        }

        if (scoreP1 != null) {
            scoreP1.setVisible(false);
            scoreP1.setManaged(false);
        }
        if (scoreP2 != null) {
            scoreP2.setVisible(false);
            scoreP2.setManaged(false);
        }

        if (playerNameP1 != null) {
            playerNameP1.setText(player1.getName());
        }
        if (playerNameP2 != null) {
            playerNameP2.setText(player2.getName());
        }
        if (turnLabel != null) {
            turnLabel.setText("Replay Mode: " + record.getInt("totalMoves") + " moves loaded");
        }

        if (board != null) {
            board.setDisable(true);
        }

        if (playButton != null) {
            playButton.setDisable(false);
        }
        if (pauseButton != null) {
            pauseButton.setDisable(true);
        }
        if (resetButton != null) {
            resetButton.setDisable(false);
        }

        System.out.println("Replay mode set successfully");
    }

    @FXML
    private void handlePlay() {
        System.out.println("Play button clicked");

        if (!isReplayMode || replayRecord == null) {
            System.err.println("Not in replay mode or no record!");
            return;
        }

        // Safety: Ensure highlights are gone if starting fresh
        if (currentReplayIndex == 0) {
            clearHighlights();
        }

        JsonArray moves = replayRecord.getJsonArray("moves");

        if (currentReplayIndex >= moves.size()) {
            System.out.println("Replay finished, resetting...");
            handleReset();
            return;
        }

        isReplaying = true;
        if (playButton != null) {
            playButton.setDisable(true);
        }
        if (pauseButton != null) {
            pauseButton.setDisable(false);
        }
        if (resetButton != null) {
            resetButton.setDisable(true);
        }

        System.out.println("Starting replay from move " + (currentReplayIndex + 1));
        playNextReplayMove();
    }

    private void playNextReplayMove() {
        if (!isReplaying || replayRecord == null) {
            System.out.println("Replay stopped or no record");
            finishReplay();
            return;
        }

        JsonArray moves = replayRecord.getJsonArray("moves");
        if (currentReplayIndex >= moves.size()) {
            System.out.println("All moves played");
            finishReplay();
            return;
        }

        JsonObject move = moves.getJsonObject(currentReplayIndex);

        System.out.println("Playing move " + (currentReplayIndex + 1) + "/" + moves.size());

        turnLabel.setText("Move " + (currentReplayIndex + 1) + "/" + moves.size() + ": "
                + move.getString("playerName") + " plays "
                + move.getString("symbol"));

        int row = move.getInt("row");
        int col = move.getInt("col");
        String symbol = move.getString("symbol");

        System.out.println("Placing " + symbol + " at [" + row + "," + col + "]");

        StackPane cell = getCell(row, col);
        if (cell != null) {
            Image img = symbol.equals("X") ? xImage : oImage;
            ImageView imageView = new ImageView(img);
            imageView.setFitWidth(80);
            imageView.setFitHeight(80);
            imageView.setPreserveRatio(true);
            cell.getChildren().add(imageView);
            System.out.println("Move placed successfully");
        } else {
            System.err.println("Cell not found for position [" + row + "," + col + "]");
        }

        currentReplayIndex++;

        if (isReplaying && currentReplayIndex < moves.size()) {
            currentTransition = new PauseTransition(Duration.seconds(REPLAY_DELAY));
            currentTransition.setOnFinished(e -> playNextReplayMove());
            currentTransition.play();
            System.out.println("Scheduled next move in " + REPLAY_DELAY + " seconds");
        } else {
            System.out.println("No more moves to play");
            finishReplay();
        }
    }

    private void finishReplay() {
        System.out.println("Finishing replay");
        isReplaying = false;
        currentTransition = null;

        if (playButton != null) {
            playButton.setDisable(false);
        }
        if (pauseButton != null) {
            pauseButton.setDisable(true);
        }
        if (resetButton != null) {
            resetButton.setDisable(false);
        }

        if (replayRecord != null) {
            turnLabel.setText("Game Over - " + replayRecord.getString("result"));
        }
    }

    @FXML
    private void handlePause() {
        System.out.println("Pause button clicked");

        if (!isReplayMode) {
            return;
        }

        isReplaying = false;
        if (currentTransition != null) {
            currentTransition.stop();
            currentTransition = null;
            System.out.println("Transition stopped");
        }

        if (playButton != null) {
            playButton.setDisable(false);
        }
        if (pauseButton != null) {
            pauseButton.setDisable(true);
        }
        if (resetButton != null) {
            resetButton.setDisable(false);
        }

        if (replayRecord != null) {
            int totalMoves = replayRecord.getInt("totalMoves");
            turnLabel.setText("Paused at move " + currentReplayIndex + "/" + totalMoves);
        }

        System.out.println("Replay paused at move " + currentReplayIndex);
    }

    @FXML
    private void handleReset() {
        System.out.println("Reset button clicked");

        if (!isReplayMode) {
            return;
        }

        isReplaying = false;
        if (currentTransition != null) {
            currentTransition.stop();
            currentTransition = null;
        }

        currentReplayIndex = 0;
        clearHighlights(); // Clear animations

        if (board != null) {
            for (var node : board.getChildren()) {
                if (node instanceof StackPane) {
                    ((StackPane) node).getChildren().clear();
                }
            }
            System.out.println("Board cleared");
        }

        if (playButton != null) {
            playButton.setDisable(false);
        }
        if (pauseButton != null) {
            pauseButton.setDisable(true);
        }
        if (resetButton != null) {
            resetButton.setDisable(false);
        }

        if (replayRecord != null) {
            turnLabel.setText("Ready to replay - " + replayRecord.getInt("totalMoves") + " moves");
        }

        System.out.println("Replay reset complete");
    }

    // ==================== NAVIGATION ====================
    @FXML
    private void goBack() {
        if (isReplaying && currentTransition != null) {
            currentTransition.stop();
            isReplaying = false;
        }

        if (GameSession.isRecording() && !gameEnded) {
            GameSession.cancelRecording();
        }

        if (isOnlineMode && !gameEnded) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            styleAlert(alert);
            alert.setTitle("Quit Game");
            alert.setHeaderText("Are you sure you want to quit?");
            alert.setContentText("Quitting this game means you forfeit and lose. Your opponent wins. Continue?");

            ButtonType quitBtn = new ButtonType("Quit (Forfeit)", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelBtn = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(quitBtn, cancelBtn);

            alert.showAndWait().ifPresent(button -> {
                if (button == quitBtn) {
                    JSONObject quit = new JSONObject();
                    quit.put("type", "quit_game");
                    NetworkConnection.getInstance().sendMessage(quit);
                    System.out.println("----------Player Forfeited--------------");
                    Navigation.goTo(Routes.ONLINE_PLAYERS); // Navigate back after sending quit message
                } else {
                    // Do nothing, stay on gameboard
                }
            });
            return; // Important: return here to prevent further navigation if online game is active
        }

        if (isReplayMode) {
            Navigation.goTo(Routes.GAME_RECORDS_OFFLINE);
        } else if (isOnlineMode) { // This else-if block will now only be reached if gameEnded is true for online mode
            Navigation.goTo(Routes.ONLINE_PLAYERS);
        } else {
            Navigation.goTo(Routes.MODE_SELECTION);
        }
    }

    // ==================== CELL CLICK HANDLING ====================
    @FXML
    private void onCellClicked(MouseEvent event) {
        if (gameEnded || isReplayMode) {
            return;
        }

        if (isOnlineMode) {
            handleOnlineCellClick(event);
        } else {
            handleOfflineCellClick(event);
        }
    }

    private void handleOnlineCellClick(MouseEvent event) {
        if (!isMyTurn) {
            return;
        }

        StackPane clickedCell = (StackPane) event.getSource();
        int row = GridPane.getRowIndex(clickedCell) == null ? 0 : GridPane.getRowIndex(clickedCell);
        int col = GridPane.getColumnIndex(clickedCell) == null ? 0 : GridPane.getColumnIndex(clickedCell);

        if (!clickedCell.getChildren().isEmpty()) {
            return;
        }

        Cell cellSymbol = "X".equals(mySymbol) ? Cell.X : Cell.O;
        placeMove(clickedCell, cellSymbol);
        gameBoard.getGrid()[row][col] = cellSymbol;

        // Record move if recording is active
        if (GameSession.isRecording()) {
            GameSession.recordMove(row, col, mySymbol, myUsername);
        }

        JSONObject moveMsg = new JSONObject();
        moveMsg.put("type", "move");
        moveMsg.put("row", row);
        moveMsg.put("col", col);
        NetworkConnection.getInstance().sendMessage(moveMsg);

        isMyTurn = false;
        updateOnlinePlayersLabels();
    }

    private void handleOfflineCellClick(MouseEvent event) {
        boolean actionTaken = false;

        StackPane clickedCell = (StackPane) event.getSource();
        int row = GridPane.getRowIndex(clickedCell) == null ? 0 : GridPane.getRowIndex(clickedCell);
        int col = GridPane.getColumnIndex(clickedCell) == null ? 0 : GridPane.getColumnIndex(clickedCell);

        if (mode == GameMode.HUMAN_VS_COMPUTER_MODE) {
            if (clickedCell.getChildren().isEmpty()) {
                if (Xturn) {
                    placeMove(clickedCell, Cell.X);
                    gameBoard.getGrid()[row][col] = Cell.X;

                    GameSession.recordMove(row, col, "X", "You");

                    Xturn = false;
                    turnLabel.setText("Computer's Turn");
                }

                Cell winnerCell = gameBoard.checkWinner();
                if (winnerCell != Cell.EMPTY) {
                    gameEnded = true;
                    if (winnerCell == Cell.X) {
                        handleGameEndOffline(true, false);
                        turnLabel.setText("You Win!");
                    } else {
                        handleGameEndOffline(false, false);
                        turnLabel.setText("You Lost!");
                    }
                    actionTaken = true;
                } else if (gameBoard.isFull()) {
                    handleGameEndOffline(false, true);
                    gameEnded = true;
                    turnLabel.setText("It's a Draw!");
                    actionTaken = true;
                }

                if (!actionTaken) {
                    Move bestMove = ai.getBestMove(gameBoard, difficulty);
                    if (bestMove != null) {
                        int aiRow = bestMove.getRow();
                        int aiCol = bestMove.getCol();
                        gameBoard.getGrid()[aiRow][aiCol] = Cell.O;

                        GameSession.recordMove(aiRow, aiCol, "O", "Computer");

                        StackPane aiCell = getCell(aiRow, aiCol);
                        if (aiCell != null) {
                            placeMove(aiCell, Cell.O);
                        }

                        winnerCell = gameBoard.checkWinner();
                        if (winnerCell != Cell.EMPTY) {
                            gameEnded = true;
                            if (winnerCell == Cell.O) {
                                handleGameEndOffline(false, false);
                                turnLabel.setText("Computer Wins!");
                            } else {
                                handleGameEndOffline(true, false);
                                turnLabel.setText("You Win!");
                            }
                        } else if (gameBoard.isFull()) {
                            handleGameEndOffline(false, true);
                            gameEnded = true;
                            turnLabel.setText("It's a Draw!");
                        } else {
                            Xturn = true;
                            turnLabel.setText("Your Turn");
                        }
                    }
                }
            }
        } else if (mode == GameMode.LOCAL_MODE) {
            if (clickedCell.getChildren().isEmpty()) {
                if (Xturn && gameBoard.checkWinner() == Cell.EMPTY) {
                    placeMove(clickedCell, Cell.X);
                    gameBoard.getGrid()[row][col] = Cell.X;

                    GameSession.recordMove(row, col, "X", player1.getName());

                    Xturn = false;
                } else {
                    if (!actionTaken && gameBoard.checkWinner() == Cell.EMPTY) {
                        placeMove(clickedCell, Cell.O);
                        gameBoard.getGrid()[row][col] = Cell.O;

                        GameSession.recordMove(row, col, "O", player2.getName());

                        Xturn = true;
                    }
                }

                Cell winnerCell = gameBoard.checkWinner();
                if (winnerCell != Cell.EMPTY) {
                    gameEnded = true;
                    String winnerName = (winnerCell == Cell.X) ? player1.getName() : player2.getName();

                    if (winnerCell == Cell.X) {
                        GameSession.addWinP1();
                    } else {
                        GameSession.addWinP2();
                    }

                    updateScoreBoard();
                    turnLabel.setText(winnerName + " Wins!");

                    if (GameSession.isRecording()) {
                        String result = winnerName + " Wins";
                        String filePath = GameSession.saveGameRecord(result);
                        if (filePath != null) {
                            System.out.println("Game saved to: " + filePath);
                        }
                    }

                    // HIGHLIGHT WINNER CELLS
                    highlightWinningCells(winnerCell);

                    showLocalModeWinPopup(winnerName);
                    actionTaken = true;
                } else if (gameBoard.isFull()) {
                    gameEnded = true;
                    GameSession.addDraw();
                    updateScoreBoard();
                    turnLabel.setText("It's a Draw!");

                    if (GameSession.isRecording()) {
                        String filePath = GameSession.saveGameRecord("Draw");
                        if (filePath != null) {
                            System.out.println("Game saved to: " + filePath);
                        }
                    }

                    showDrawPopup();
                    actionTaken = true;
                }

                if (!actionTaken) {
                    turnLabel.setText(Xturn ? player1.getName() + "'s Turn" : player2.getName() + "'s Turn");
                }
            }
        }
    }

    // ==================== GAME END HANDLING ====================
    private void handleGameEndOffline(boolean playerWon, boolean isDraw) {
        gameEnded = true;

        if (isDraw) {
            turnLabel.setText("It's a Draw!");
            GameSession.addDraw();
            updateScoreBoard();

            if (GameSession.isRecording()) {
                String filePath = GameSession.saveGameRecord("Draw");
                if (filePath != null) {
                    System.out.println("Game saved to: " + filePath);
                }
            }

            showDrawPopup();
        } else if (playerWon) {
            turnLabel.setText("You Win!");
            GameSession.addWinP1();
            updateScoreBoard();

            // Highlight Winning Cells (Player uses X locally vs Computer)
            highlightWinningCells(Cell.X);

            if (GameSession.isRecording()) {
                String filePath = GameSession.saveGameRecord("You Win");
                if (filePath != null) {
                    System.out.println("Game saved to: " + filePath);
                }
            }
            showWinLosePopup(true);
        } else {
            turnLabel.setText("Computer Wins!");
            GameSession.addWinP2();
            updateScoreBoard();

            // Highlight Winning Cells (Computer uses O)
            highlightWinningCells(Cell.O);

            if (GameSession.isRecording()) {
                String filePath = GameSession.saveGameRecord("Computer Wins");
                if (filePath != null) {
                    System.out.println("Game saved to: " + filePath);
                }
            }

            showWinLosePopup(false);
        }
    }

    // ==================== UTILITY METHODS ====================
    private void placeMove(StackPane cell, Cell symbol) {
        Image img = symbol == Cell.X ? xImage : oImage;
        ImageView imageView = new ImageView(img);
        imageView.setFitWidth(80);
        imageView.setFitHeight(80);
        imageView.setPreserveRatio(true);
        cell.getChildren().add(imageView);
    }

    private StackPane getCell(int row, int col) {
        for (var node : board.getChildren()) {
            Integer r = GridPane.getRowIndex(node);
            Integer c = GridPane.getColumnIndex(node);

            int rowIndex = (r == null) ? 0 : r;
            int colIndex = (c == null) ? 0 : c;

            if (rowIndex == row && colIndex == col) {
                return (StackPane) node;
            }
        }
        System.err.println("WARNING: Cell not found at [" + row + "," + col + "]");
        return null;
    }

    private void showWinLosePopup(boolean won) {
        System.out.println("showWinLosePopup called with won: " + won);
        Win_LoseController controller = Navigation.openModalWithController(Routes.WIN_LOSE);
        if (controller != null) {
            controller.setResult(won);
        } else {
            System.err.println("ERROR: Win_LoseController is null!");
        }
    }

    private void showDrawPopup() {
        System.out.println("showDrawPopup called");
        Win_LoseController controller = Navigation.openModalWithController(Routes.WIN_LOSE);
        if (controller != null) {
            controller.setResultDraw();
        } else {
            System.err.println("ERROR: Win_LoseController is null!");
        }
    }

    private void showLocalModeWinPopup(String winnerName) {
        System.out.println("showLocalModeWinPopup called with winner: " + winnerName);
        Win_LoseController controller = Navigation.openModalWithController(Routes.WIN_LOSE);
        if (controller != null) {
            controller.setResultLocalMode(winnerName);
        } else {
            System.err.println("ERROR: Win_LoseController is null!");
        }
    }

    private void styleAlert(Alert alert) {
        var dialogPane = alert.getDialogPane();

        dialogPane.setId("xo-alert");

        dialogPane.getStylesheets().add(
                getClass().getResource("/styles/alert.css").toExternalForm()
        );
    }

    private void highlightWinningCells(Cell winner) {
        Cell[][] grid = gameBoard.getGrid();

        for (int i = 0; i < 3; i++) {
            if (grid[i][0] == winner && grid[i][1] == winner && grid[i][2] == winner) {
                animateWin(i, 0);
                animateWin(i, 1);
                animateWin(i, 2);
                return;
            }
        }

        for (int i = 0; i < 3; i++) {
            if (grid[0][i] == winner && grid[1][i] == winner && grid[2][i] == winner) {
                animateWin(0, i);
                animateWin(1, i);
                animateWin(2, i);
                return;
            }
        }

        if (grid[0][0] == winner && grid[1][1] == winner && grid[2][2] == winner) {
            animateWin(0, 0);
            animateWin(1, 1);
            animateWin(2, 2);
            return;
        }
        if (grid[0][2] == winner && grid[1][1] == winner && grid[2][0] == winner) {
            animateWin(0, 2);
            animateWin(1, 1);
            animateWin(2, 0);
            return;
        }
    }

    private void animateWin(int row, int col) {
        StackPane cell = getCell(row, col);
        if (cell == null) {
            return;
        }

        if (!cell.getStyleClass().contains("cell-winning")) {
            cell.getStyleClass().add("cell-winning");
        }

        if (!cell.getChildren().isEmpty()) {
            Node symbol = cell.getChildren().get(0);

            ScaleTransition st = new ScaleTransition(Duration.millis(600), symbol);
            st.setFromX(1.0);
            st.setFromY(1.0);
            st.setToX(1.25);
            st.setToY(1.25);
            st.setCycleCount(Animation.INDEFINITE); // Loop forever
            st.setAutoReverse(true); // Go up and down
            st.play();
        }
    }

    private void clearHighlights() {
        for (var node : board.getChildren()) {
            if (node instanceof StackPane) {
                node.getStyleClass().remove("cell-winning");
            }
        }
    }
}
