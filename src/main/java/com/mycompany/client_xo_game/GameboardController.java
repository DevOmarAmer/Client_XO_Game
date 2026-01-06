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
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;

public class GameboardController implements Initializable {

    @FXML
    private BorderPane rootPane;
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
    
    // Replay mode fields
    private boolean isReplayMode = false;
    private JsonObject replayRecord;
    private int currentReplayIndex = 0;
    private boolean isReplaying = false;
    private PauseTransition currentTransition = null;
    private static final double REPLAY_DELAY = 1.0; // seconds
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public void updatePlayersLabels() {
        if (isReplayMode) {
            // Skip normal player label updates in replay mode
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

    private void updateScoreBoard() {
        if (isReplayMode) {
            return; // Don't update scores in replay mode
        }
        scoreP1.setText(String.valueOf(GameSession.getScoreP1()));
        scoreP2.setText(String.valueOf(GameSession.getScoreP2()));
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("GameboardController initialize() called");
        
        xImage = new Image(getClass().getResourceAsStream("/assets/X.png"));
        oImage = new Image(getClass().getResourceAsStream("/assets/O.png"));
        
        // Hide replay controls initially
        if (replayControls != null) {
            replayControls.setVisible(false);
            replayControls.setManaged(false);
        }
        
        // Only initialize game components if NOT in replay mode
        // Replay mode will be set after initialization via setReplayMode()
        if (!isReplayMode) {
            gameBoard = new Board();
            ai = new Minimax();
            mode = GameSession.getGameMode();
            difficulty = GameSession.getDifficulty();
            player1 = GameSession.getPlayer1();
            player2 = GameSession.getPlayer2();
            gameEnded = false;
            
            updatePlayersLabels();
            updateScoreBoard();
        }
        
        System.out.println("GameboardController initialize() complete");
    }
    

 public void setReplayMode(JsonObject record) {
    this.isReplayMode = true;
    this.replayRecord = record;
    this.currentReplayIndex = 0;
    this.isReplaying = false;

    System.out.println("DEBUG: Starting setReplayMode...");

    // 1. Clear board logic
    if (board != null) {
        for (var node : board.getChildren()) {
            if (node instanceof StackPane) ((StackPane) node).getChildren().clear();
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


    if (scoreP1 != null) { scoreP1.setVisible(false); scoreP1.setManaged(false); }
    if (scoreP2 != null) { scoreP2.setVisible(false); scoreP2.setManaged(false); }

   
    if (playerNameP1 != null) playerNameP1.setText(player1.getName());
    if (playerNameP2 != null) playerNameP2.setText(player2.getName());
    if (turnLabel != null) turnLabel.setText("Replay Mode: " + record.getInt("totalMoves") + " moves loaded");

    if (board != null) board.setDisable(true);

    if (playButton != null) playButton.setDisable(false);
    if (pauseButton != null) pauseButton.setDisable(true);
    if (resetButton != null) resetButton.setDisable(false);

    System.out.println("Replay mode set successfully");
}



    @FXML
    private void goBack() {
  
        if (isReplaying && currentTransition != null) {
            currentTransition.stop();
            isReplaying = false;
        }
        
     
        if (GameSession.isRecording() && !gameEnded) {
            GameSession.cancelRecording();
        }
        
        if (isReplayMode) {
            Navigation.goTo(Routes.GAME_RECORDS_OFFLINE);
        } else {
            Navigation.goTo(Routes.MODE_SELECTION);
        }
    }

    @FXML
    private void onCellClicked(MouseEvent event) {
        if (gameEnded || isReplayMode) {
            return;
        }

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
                        handleGameEnd(true, false);
                        turnLabel.setText("You Win!");
                    } else {
                        handleGameEnd(false, false); 
                        turnLabel.setText("You Lost!");
                    }
                    actionTaken = true;
                } else if (gameBoard.isFull()) {
                    handleGameEnd(false, true); 
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
                                handleGameEnd(false, false);
                                turnLabel.setText("Computer Wins!");
                            } else {
                                handleGameEnd(true, false);
                                turnLabel.setText("You Win!");
                            }
                        } else if (gameBoard.isFull()) {
                            handleGameEnd(false, true);
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
    

    @FXML
    private void handlePlay() {
        System.out.println("Play button clicked");
        
        if (!isReplayMode || replayRecord == null) {
            System.err.println("Not in replay mode or no record!");
            return;
        }
        
        JsonArray moves = replayRecord.getJsonArray("moves");
        
      
        if (currentReplayIndex >= moves.size()) {
            System.out.println("Replay finished, resetting...");
            handleReset();
            return;
        }
        
  
        isReplaying = true;
        if (playButton != null) playButton.setDisable(true);
        if (pauseButton != null) pauseButton.setDisable(false);
        if (resetButton != null) resetButton.setDisable(true);
        
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
     
        turnLabel.setText("Move " + (currentReplayIndex + 1) + "/" + moves.size() + ": " + 
                         move.getString("playerName") + " plays " + 
                         move.getString("symbol"));
    
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
        
        if (playButton != null) playButton.setDisable(false);
        if (pauseButton != null) pauseButton.setDisable(true);
        if (resetButton != null) resetButton.setDisable(false);
        
        if (replayRecord != null) {
            turnLabel.setText("Game Over - " + replayRecord.getString("result"));
        }
    }
    
    @FXML
    private void handlePause() {
        System.out.println("Pause button clicked");
        
        if (!isReplayMode) return;
        
  
        isReplaying = false;
        if (currentTransition != null) {
            currentTransition.stop();
            currentTransition = null;
            System.out.println("Transition stopped");
        }
        
   
        if (playButton != null) playButton.setDisable(false);
        if (pauseButton != null) pauseButton.setDisable(true);
        if (resetButton != null) resetButton.setDisable(false);
        
     
        if (replayRecord != null) {
            int totalMoves = replayRecord.getInt("totalMoves");
            turnLabel.setText("Paused at move " + currentReplayIndex + "/" + totalMoves);
        }
        
        System.out.println("Replay paused at move " + currentReplayIndex);
    }
    
    @FXML
    private void handleReset() {
        System.out.println("Reset button clicked");
        
        if (!isReplayMode) return;
  
        isReplaying = false;
        if (currentTransition != null) {
            currentTransition.stop();
            currentTransition = null;
        }
        
     
        currentReplayIndex = 0;
        
  
        if (board != null) {
            for (var node : board.getChildren()) {
                if (node instanceof StackPane) {
                    ((StackPane) node).getChildren().clear();
                }
            }
            System.out.println("Board cleared");
        }
        
     
        if (playButton != null) playButton.setDisable(false);
        if (pauseButton != null) pauseButton.setDisable(true);
        if (resetButton != null) resetButton.setDisable(false);
        
   
        if (replayRecord != null) {
            turnLabel.setText("Ready to replay - " + replayRecord.getInt("totalMoves") + " moves");
        }
        
        System.out.println("Replay reset complete");
    }
    
    private void handleGameEnd(boolean playerWon, boolean isDraw) {
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
            
        
            if (GameSession.isRecording()) {
                String filePath = GameSession.saveGameRecord("Computer Wins");
                if (filePath != null) {
                    System.out.println("Game saved to: " + filePath);
                }
            }
            
            showWinLosePopup(false);
        }
    }
    
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
}