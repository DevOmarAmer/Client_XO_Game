/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.client_xo_game.game_engine;

import com.mycompany.client_xo_game.enums.Cell;
import com.mycompany.client_xo_game.enums.GameStatus;
import com.mycompany.client_xo_game.model.GameState;
import com.mycompany.client_xo_game.model.Move;

/**
 *
 * @author Alaa
 */
public class GameEngine {
      GameState state;
  
    
    public GameEngine(GameState state){
        this.state = state;
    }
    public GameState getState(){
    return state;
    }
    
    public boolean applyMove(Move move){
        boolean moveApplied = false;
        if((state.getStatus() == GameStatus.RUNNING)){ //if game is running set symbol of move to the one of current player
            move.setSymbol(state.getCurrPlayer().getSymbol());
            if(state.getBoard().placeMove(move)){// now check if there's place  to put that move on board or not if yes insert
                moveApplied = true;
                Cell winnerSymbol = state.getBoard().checkWinner();//now check if someone won
                if(winnerSymbol != Cell.EMPTY){//someone won
                    state.setStatus(GameStatus.WIN);
                    state.setWinnerPlayer(state.getCurrPlayer());
                }
                else{ //no winner so check for a draw 
                    if(state.getBoard().isFull()){
                        state.setStatus(GameStatus.DRAW);
                    }
                    else{ // game is still running
                        switchTurn();
                    }
                }
            }
                
            }
           return moveApplied; 
        }
        
    
    
    public void switchTurn() {
        if (state.getCurrPlayer() == state.getPlayerX()) {
            state.setCurrPlayer(state.getPlayerO());
        } else {
            state.setCurrPlayer(state.getPlayerX());
        }
    }

    
}
