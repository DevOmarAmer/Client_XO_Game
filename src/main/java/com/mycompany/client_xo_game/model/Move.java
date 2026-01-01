/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.client_xo_game.model;

import com.mycompany.client_xo_game.enums.Cell;

/**
 *
 * @author Alaa
 */
public class Move {
        private int row;
    private int col;
    private Cell symbol;
    
    public Move(int row, int col){
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public Cell getSymbol() {
        return symbol;
    }

    public void setSymbol(Cell symbol) {
        this.symbol = symbol;
    }
  
}
