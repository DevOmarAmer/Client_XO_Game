/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.client_xo_game.model;

import com.mycompany.client_xo_game.enums.AIDifficulty;
import com.mycompany.client_xo_game.enums.GameMode;

/**
 *
 * @author Alaa
 */
public class GameSession {
 
    private static GameMode currentMode;
    private static AIDifficulty difficulty;
    private static Player_Offline player1;
    private static Player_Offline player2;
    
    public static void setGameMode(GameMode mode) {
        currentMode = mode;
    }
    
    public static GameMode getGameMode() {
        return currentMode;
    }
    
    public static void setDifficulty(AIDifficulty level) {
        difficulty = level;
    }
    
    public static AIDifficulty getDifficulty() {
        return difficulty;
    }
        public static void setPlayer1(Player_Offline player) {
        player1 = player;
    }
    
    public static Player_Offline getPlayer1() {
        return player1;
    }
    
    public static void setPlayer2(Player_Offline player) {
        player2 = player;
    }
    
    public static Player_Offline getPlayer2() {
        return player2;
    }
    
      public static void setPlayers(Player_Offline p1, Player_Offline p2) {
        player1 = p1;
        player2 = p2;
    }
      
         public static void clearSession() {
        currentMode = null;
        difficulty = null;
        player1 = null;
        player2 = null;
    }
}

