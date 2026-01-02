package com.mycompany.client_xo_game;

import com.mycompany.client_xo_game.enums.AIDifficulty;
import com.mycompany.client_xo_game.enums.Cell;
import com.mycompany.client_xo_game.enums.GameMode;
import com.mycompany.client_xo_game.game_engine.Board;
import com.mycompany.client_xo_game.game_engine.Minimax;
import com.mycompany.client_xo_game.model.GameSession;
import com.mycompany.client_xo_game.model.Move;
import com.mycompany.client_xo_game.navigation.Navigation;
import com.mycompany.client_xo_game.navigation.Routes;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class GameboardController implements Initializable {

    @FXML
    private BorderPane rootPane;
    @FXML
    private Label turnLabel;
    @FXML
    private HBox playersBar;
    @FXML
    private Label scoreP1;
    @FXML
    private Label scoreP2;
    @FXML
    private StackPane boardContainer;
    @FXML
    private GridPane board;

    private String level;
    private String player1;
    private String player2;
    private Board gameBoard;
    private Minimax ai;
    private GameMode mode;
    private AIDifficulty difficulty;
    private boolean Xturn = true; //X starts the game 
    private Image xImage;
    private Image oImage;


    public void setPlayerNames(String p1, String p2) {
        this.player1 = p1;
        this.player2 = p2;
        updatePlayerLabels();
    }

//    private void updatePlayerLabels() {
//        if (scoreP1 != null && scoreP2 != null) {
//            scoreP1.setText(player1 + " Score: 0");
//            scoreP2.setText(player2 + " Score: 0");
//            turnLabel.setText(player1 + " Turn");
//        }
//    }
    private void updatePlayerLabels() {
        scoreP1.setText(player1 + " Score: 0");
        scoreP2.setText(player2 + " Score: 0");
        turnLabel.setText(Xturn ? player1 + " Turn" : player2 + " Turn");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        rootPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                onSceneShown();
            }
        });
        gameBoard = new Board();
        ai = new Minimax();
        mode = GameSession.getGameMode();
        difficulty = GameSession.getDifficulty();
        xImage = new Image(getClass().getResourceAsStream("/assets/X.png"));
        oImage = new Image(getClass().getResourceAsStream("/assets/O.png"));
    }



    private void onSceneShown() {
        if (level != null) {
            turnLabel.setText(level + " MODE");
        } else if (player1 != null && player2 != null) {
            scoreP1.setText(player1 + " Score: 0");
            scoreP2.setText(player2 + " Score: 0");
            turnLabel.setText(player1 + "'s Turn");
        }
    }

    @FXML
    private void goBack() {
        if(mode == GameMode.HUMAN_VS_COMPUTER_MODE){
            Navigation.goTo(Routes.LEVEL_SELECTION);
        }
        else{
        Navigation.goTo(Routes.MODE_SELECTION);
        }
        
    }

    @FXML
    private void onCellClicked(MouseEvent event) {
        boolean actionTaken = false;

        StackPane clickedCell = (StackPane) event.getSource();
        int row = GridPane.getRowIndex(clickedCell) == null ? 0 : GridPane.getRowIndex(clickedCell);//getRowIndex might return null
        int col = GridPane.getColumnIndex(clickedCell) == null ? 0 : GridPane.getColumnIndex(clickedCell);
        if (mode == GameMode.HUMAN_VS_COMPUTER_MODE) {
            if (clickedCell.getChildren().isEmpty()) {
                if (Xturn) {
                    placeMove(clickedCell, Cell.X);
                    gameBoard.getGrid()[row][col] = Cell.X;
                    Xturn = false;
                }

                Cell winnerCell = gameBoard.checkWinner();//check winning condition
                if (winnerCell != Cell.EMPTY) {
                    System.out.println("Winner: " + winnerCell);
                    actionTaken = true;
                } else if (gameBoard.isFull()) {
                    System.out.println("Draw!");
                    actionTaken = true;
                }

                if (!actionTaken) {//ai move
                    Move bestMove = ai.getBestMove(gameBoard, difficulty);
                    if (bestMove != null) {
                        int aiRow = bestMove.getRow();
                        int aiCol = bestMove.getCol();
                        gameBoard.getGrid()[aiRow][aiCol] = Cell.O;
                        StackPane aiCell = getCell(aiRow, aiCol);
                        if (aiCell != null) {
                            placeMove(aiCell, Cell.O);
                        }

                        // check if AI move ends game
                        winnerCell = gameBoard.checkWinner();
                        if (winnerCell != Cell.EMPTY) {
                            System.out.println("Winner: " + winnerCell);
                        } else if (gameBoard.isFull()) {
                            System.out.println("Draw!");
                        } else {
                            Xturn = true;  // back to human
                        }
                    }
                }
            }
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
}
