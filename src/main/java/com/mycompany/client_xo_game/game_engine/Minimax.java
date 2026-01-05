/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.client_xo_game.game_engine;

import com.mycompany.client_xo_game.enums.AIDifficulty;
import static com.mycompany.client_xo_game.enums.AIDifficulty.EASY;
import static com.mycompany.client_xo_game.enums.AIDifficulty.HARD;
import static com.mycompany.client_xo_game.enums.AIDifficulty.MEDIUM;
import com.mycompany.client_xo_game.enums.Cell;
import com.mycompany.client_xo_game.model.Move;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Alaa
 */
public class Minimax {
public Move getBestMove(Board board, AIDifficulty difficulty) {
    
    if (difficulty == AIDifficulty.EASY) {
        return getRandomMove(board);
    }
    
    int maxDepth;
    switch (difficulty) {
        case MEDIUM: 
            maxDepth = 2; 
            break; 
        case HARD:   
            maxDepth = 9; 
            break; 
        default:     
            maxDepth = 9;
    }
    
    int bestScore = Integer.MIN_VALUE;
    Move bestMove = null;
    
    for (int row = 0; row < 3; row++) {
        for (int col = 0; col < 3; col++) {
            if (board.getGrid()[row][col] == Cell.EMPTY) {
                board.getGrid()[row][col] = Cell.O;
                int score = minimax(board, maxDepth, false);
                board.getGrid()[row][col] = Cell.EMPTY;
                
                if (score > bestScore) {
                    bestScore = score;
                    bestMove = new Move(row, col);
                }
            }
        }
    }
    return bestMove;
}

private Move getRandomMove(Board board) {
    ArrayList<Move> availableMoves = new ArrayList<>();
     int randomIndex =0;
    //keeping track of avaliable spaces
    for (int row = 0; row < 3; row++) {
        for (int col = 0; col < 3; col++) {
            if (board.getGrid()[row][col] == Cell.EMPTY) {
                availableMoves.add(new Move(row, col));
            }
        }
    }
   
    if (!availableMoves.isEmpty()) {
      
    
     randomIndex = (int) (Math.random() * availableMoves.size());
    
    }
    return availableMoves.get(randomIndex);
}

  private int minimax(Board board, int depth, boolean isMaximizing) {
    int result = 0;
    Cell winner = board.checkWinner();
    
    if (winner == Cell.O) {
        result = 10 - depth;
    } else if (winner == Cell.X) {
        result = -10 + depth;
    } else if (board.isFull() || depth == 0) {
        result = 0;
    } else if (isMaximizing) { 
        int maxEval = Integer.MIN_VALUE;
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (board.getGrid()[r][c] == Cell.EMPTY) {
                    board.getGrid()[r][c] = Cell.O; 
                    int eval = minimax(board, depth - 1, false);
                    board.getGrid()[r][c] = Cell.EMPTY; 
                    
                    maxEval = Math.max(maxEval, eval);
                }
            }
        }
        result = maxEval;
    } else { 
        int minEval = Integer.MAX_VALUE;
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (board.getGrid()[r][c] == Cell.EMPTY) {
                    board.getGrid()[r][c] = Cell.X; 
                    int eval = minimax(board, depth - 1, true);
                    board.getGrid()[r][c] = Cell.EMPTY; 
                    
                    minEval = Math.min(minEval, eval);
                }
            }
        }
        result = minEval;
    }
    
    return result;
}
    

 
}
