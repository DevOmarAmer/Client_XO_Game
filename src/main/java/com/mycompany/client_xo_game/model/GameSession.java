/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.client_xo_game.model;

import com.mycompany.client_xo_game.enums.AIDifficulty;
import com.mycompany.client_xo_game.enums.GameMode;
import com.mycompany.client_xo_game.util.GameRecorder;

/**
 *
 * @author Alaa
 */
public class GameSession {

    private static GameMode currentMode;
    private static AIDifficulty difficulty;
    private static Player_Offline player1;
    private static Player_Offline player2;
    private static int scoreP1 = 0;
    private static int scoreP2 = 0;
     private static GameRecorder gameRecorder = new GameRecorder();

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

    public static int getScoreP1() {
        return scoreP1;
    }

    public static void setScoreP1(int scoreP1) {
        GameSession.scoreP1 = scoreP1;
    }

    public static int getScoreP2() {
        return scoreP2;
    }

    public static void setScoreP2(int scoreP2) {
        GameSession.scoreP2 = scoreP2;
    }

    public static void addWinP1() {
        scoreP1 += 10;
    }

    public static void addWinP2() {
        scoreP2 += 10;
    }

    public static void addDraw() {
        scoreP1 += 5;
        scoreP2 += 5;
    }

    public static void clearSession() {
        currentMode = null;
        difficulty = null;
        player1 = null;
        player2 = null;
        scoreP1 = 0;
        scoreP2 = 0;
        cancelRecording();
    }
    

    
  
    public static boolean isRecording() {
        return gameRecorder.isRecording();
    }
    
 
    public static void cancelRecording() {
        if (gameRecorder.isRecording()) {
            gameRecorder.cancelRecording();
            System.out.println("Recording cancelled");
        }
    }
}
