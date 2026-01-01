/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.client_xo_game.model;

import com.mycompany.client_xo_game.enums.GameStatus;
import com.mycompany.client_xo_game.game_engine.Board;

/**
 *
 * @author Alaa
 */
public class GameState {
       private Board board;
    private Player_Offline currPlayer;
    private Player_Offline playerX;
    private Player_Offline playerO;
    private Player_Offline winnerPlayer;
    private GameStatus status;
    
    
    public GameState(Player_Offline playerX , Player_Offline playerO){
        this.playerX = playerX;
        this.playerO = playerO;
        currPlayer = playerX; //x starts first
        status = GameStatus.RUNNING; //initiate status of game
        board = new Board(); //board is 3X3 cells
       
        
    }

    public Board getBoard() {
        return board;
    }



    public Player_Offline getCurrPlayer() {
        return currPlayer;
    }

    public void setCurrPlayer(Player_Offline currPlayer) {
        this.currPlayer = currPlayer;
    }

    public Player_Offline getPlayerX() {
        return playerX;
    }

    public void setPlayerX(Player_Offline playerX) {
        this.playerX = playerX;
    }

    public Player_Offline getPlayerO() {
        return playerO;
    }

    public void setPlayerO(Player_Offline playerO) {
        this.playerO = playerO;
    }

    public Player_Offline getWinnerPlayer() {
        return winnerPlayer;
    }

    public void setWinnerPlayer(Player_Offline winnerPlayer) {
        this.winnerPlayer = winnerPlayer;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }
    
}
