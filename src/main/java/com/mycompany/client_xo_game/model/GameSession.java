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
}

