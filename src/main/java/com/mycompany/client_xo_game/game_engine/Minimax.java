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

/**
 *
 * @author Alaa
 */
public class Minimax {
   
    public Move getBestMove(Board board, AIDifficulty difficulty) {
        int maxDepth;
       
        switch (difficulty) {
            case EASY:   
                maxDepth = 1; 
                break;
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

    private int minimax(Board board, int depth, boolean isMaximizing) {
     
        Cell result = board.checkWinner();
        
        if (result == Cell.O) return 10 - depth;
        if (result == Cell.X) return -10 + depth; 
        
        
        if (board.isFull()) return 0; 

     
        if (depth == 0) return 0;

        if (isMaximizing) { 
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
            return maxEval;

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
            return minEval;
        }
    }
    

 
}
