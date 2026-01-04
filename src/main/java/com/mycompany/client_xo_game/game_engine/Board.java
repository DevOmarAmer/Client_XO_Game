/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.client_xo_game.game_engine;

import com.mycompany.client_xo_game.enums.Cell;
import com.mycompany.client_xo_game.model.Move;

/**
 *
 * @author Alaa
 */
public class Board {
        private Cell[][] grid;
    public Board(){
        grid = new Cell[3][3];
        for(int row =0 ; row<3 ; row++){
            for(int col =0 ; col<3 ; col++){
                grid[row][col] = Cell.EMPTY; //initiate grid to empty at first
            }
        }
    }
    
    public boolean placeMove(Move move){
        boolean isValidMove = false;
        if((grid[move.getRow()][move.getCol()] == Cell.EMPTY)){ //cell is empty
            isValidMove = true;
            grid[move.getRow()][move.getCol()] = move.getSymbol(); }
        
        else {// cell isnot empty
            isValidMove = false;
        }
        return isValidMove;
    
    }
      public boolean isFull() {
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (grid[r][c] == Cell.EMPTY) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public Cell checkWinner(){
        Cell winnerSymbol = Cell.EMPTY;
        for(int row =0 ; row<3 ; row++){//compare by row
            if(grid[row][0] != Cell.EMPTY){ //ensuring i'm not comparing empty values
                if((grid[row][0] == grid[row][1]) && (grid[row][1] == grid[row][2])){
                    winnerSymbol = grid[row][0]; //return any cell to get winner symbol
                }
            }
        }
        for(int col = 0 ; col <3 ; col++){//compare by columns
            if(grid[0][col] != Cell.EMPTY){
                if((grid[0][col] == grid[1][col]) && (grid[1][col] == grid[2][col])){
                     winnerSymbol = grid[0][col];
                }
            }
        }
        
       if(grid[0][0] !=Cell.EMPTY){//main diagonal
           if((grid[0][0] == grid[1][1]) && (grid[1][1] == grid[2][2])){
                winnerSymbol = grid[0][0];
           }
        
       }
       if(grid[0][2] !=Cell.EMPTY){//other diagonal
           if((grid[0][2] == grid[1][1]) && (grid[1][1] == grid[2][0])){
                winnerSymbol = grid[0][2];
           }
        
       }
       
        return winnerSymbol;      
    }
    
    public Cell[][] getGrid(){
        return grid;
    }
    
}
