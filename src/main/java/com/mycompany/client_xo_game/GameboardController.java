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
    import java.io.IOException;
    import java.net.URL;
    import java.util.ResourceBundle;
import javafx.application.Platform;
    import javafx.fxml.FXML;
    import javafx.fxml.FXMLLoader;
    import javafx.fxml.Initializable;
    import javafx.scene.Parent;
    import javafx.scene.Scene;
    import javafx.scene.control.Label;
    import javafx.scene.image.Image;
    import javafx.scene.image.ImageView;
    import javafx.scene.input.MouseEvent;
    import javafx.scene.layout.BorderPane;
    import javafx.scene.layout.GridPane;
    import javafx.scene.layout.HBox;
    import javafx.scene.layout.StackPane;
    import javafx.stage.Modality;
    import javafx.stage.Stage;
    import javafx.stage.StageStyle;
import org.json.JSONObject;

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

        public void updatePlayersLabels() {
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
            scoreP1.setText(String.valueOf(GameSession.getScoreP1()));
            scoreP2.setText(String.valueOf(GameSession.getScoreP2()));
        }

        @Override
        public void initialize(URL url, ResourceBundle rb) {
            gameBoard = new Board();
            ai = new Minimax();
            mode = GameSession.getGameMode();
            difficulty = GameSession.getDifficulty();
            player1 = GameSession.getPlayer1();
            player2 = GameSession.getPlayer2();
            xImage = new Image(getClass().getResourceAsStream("/assets/X.png"));
            oImage = new Image(getClass().getResourceAsStream("/assets/O.png"));
            gameEnded = false;

            updatePlayersLabels();
            updateScoreBoard();
            

        }

        @FXML
        private void goBack() {
                Navigation.goTo(Routes.MODE_SELECTION);

        }

        @FXML

    private void onCellClicked(MouseEvent event) {
        if (gameEnded) {
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
                    Xturn = false;
                } else {
                    if (!actionTaken && gameBoard.checkWinner() == Cell.EMPTY) {
                        placeMove(clickedCell, Cell.O);
                        gameBoard.getGrid()[row][col] = Cell.O;
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
                    showLocalModeWinPopup(winnerName);
                    actionTaken = true;
                } else if (gameBoard.isFull()) {
                    gameEnded = true;
                    GameSession.addDraw();
                    updateScoreBoard();
                    turnLabel.setText("It's a Draw!");
                    showDrawPopup();
                    actionTaken = true;
                }

                if (!actionTaken) {
                    turnLabel.setText(Xturn ? player1.getName() + "'s Turn" : player2.getName() + "'s Turn");
                }
            }
        }
    }
        private void handleGameEnd(boolean playerWon, boolean isDraw) {
            gameEnded = true;

            if (isDraw) {
                turnLabel.setText("It's a Draw!");
                GameSession.addDraw();
                updateScoreBoard();
                showDrawPopup();
            } else if (playerWon) {
                turnLabel.setText("You Win!");
                GameSession.addWinP1();
                updateScoreBoard();
                showWinLosePopup(true);
            } else {
                turnLabel.setText("Computer Wins!");
                GameSession.addWinP2();
                updateScoreBoard();
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
                int r = GridPane.getRowIndex(node) == null ? 0 : GridPane.getRowIndex(node);
                int c = GridPane.getColumnIndex(node) == null ? 0 : GridPane.getColumnIndex(node);
                if (r == row && c == col) {
                    return (StackPane) node;
                }
            }
            return null;
        }

        private void showWinLosePopup(boolean won) {
            System.out.println("showWinLosePopup called with won: " + won); // Debug
            Win_LoseController controller = Navigation.openModalWithController(Routes.WIN_LOSE);
            if (controller != null) {
                controller.setResult(won);
            } else {
                System.err.println("ERROR: Win_LoseController is null!");
            }
        }

        private void showDrawPopup() {
            System.out.println("showDrawPopup called"); // Debug
            Win_LoseController controller = Navigation.openModalWithController(Routes.WIN_LOSE);
            if (controller != null) {
                controller.setResultDraw();
            } else {
                System.err.println("ERROR: Win_LoseController is null!");
            }
        }

        private void showLocalModeWinPopup(String winnerName) {
            System.out.println("showLocalModeWinPopup called with winner: " + winnerName); // Debug
            Win_LoseController controller = Navigation.openModalWithController(Routes.WIN_LOSE);
            if (controller != null) {
                controller.setResultLocalMode(winnerName);
            } else {
                System.err.println("ERROR: Win_LoseController is null!");
            }
        }
    }