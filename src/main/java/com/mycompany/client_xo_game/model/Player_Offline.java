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
public class Player_Offline {
        private String name;
    private Cell symbol;
    
    public Player_Offline(){
        name = "No Name";
        symbol = Cell.EMPTY;
    }
    public Player_Offline(String name , Cell symbol){
        this.name = name;
        this.symbol = symbol;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void setSymbol(Cell symbol) {
        this.symbol = symbol;
    }
    
    public Cell getSymbol() {
        return symbol;
    }
}
