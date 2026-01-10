package com.mycompany.client_xo_game.model;

import com.mycompany.client_xo_game.enums.AIDifficulty;
import com.mycompany.client_xo_game.enums.GameMode;
import com.mycompany.client_xo_game.util.GameRecorder;

public class GameSession {
    private static GameMode currentMode;
    private static AIDifficulty difficulty;
    private static Player_Offline player1;
    private static Player_Offline player2;
    private static int scoreP1 = 0;
    private static int scoreP2 = 0;
    private static GameRecorder gameRecorder = new GameRecorder();
    
    // Track who initiated recording in online games
    private static String recordingInitiator = null;
    private static boolean isOnlineRecording = false;
    
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
        scoreP1 += 2;
        scoreP2 += 2;
    }
    public static void addLossP1() {
    scoreP1 -= 5;
}

public static void addLossP2() {
    scoreP2 -= 5;
}
    public static void clearSession() {
        currentMode = null;
        difficulty = null;
        player1 = null;
        player2 = null;
        scoreP1 = 0;
        scoreP2 = 0;
        cancelRecording();
        recordingInitiator = null;
        isOnlineRecording = false;
    }
  
    public static void startRecording() {
        if (player1 == null || player2 == null) {
            System.err.println("Cannot start recording: Players not set");
            return;
        }
        
        String p1Name = player1.getName();
        String p2Name = (player2 != null) ? player2.getName() : "Computer";
        
        gameRecorder.startRecording(p1Name, p2Name, false);
        recordingInitiator = null;
        isOnlineRecording = false;
        System.out.println("Recording started: " + p1Name + " vs " + p2Name);
    }
    
    
    public static void startOnlineRecording(String initiatorUsername, String opponentUsername) {
        recordingInitiator = initiatorUsername;
        isOnlineRecording = true;
        
        // Player who requested recording is always player1 (comes before "VS" in filename)
        gameRecorder.startRecording(initiatorUsername, opponentUsername, true);
        
        System.out.println("Online recording started: " + initiatorUsername + 
                          " (initiator) vs " + opponentUsername);
    }
    
   
    public static boolean isRecordingInitiator(String username) {
        return recordingInitiator != null && recordingInitiator.equals(username);
    }
    
  
    public static String getRecordingInitiator() {
        return recordingInitiator;
    }
    
  
    public static boolean isOnlineRecording() {
        return isOnlineRecording;
    }
   
    public static void recordMove(int row, int col, String symbol, String playerName) {
        gameRecorder.recordMove(row, col, symbol, playerName);
    }
    
    public static String saveGameRecord(String result) {
        String filePath = gameRecorder.saveGame(result);
        
        // Reset recording state after saving
        if (filePath != null) {
            recordingInitiator = null;
            isOnlineRecording = false;
        }
        
        return filePath;
    }
    
    public static boolean isRecording() {
        return gameRecorder.isRecording();
    }
    
    public static void cancelRecording() {
        if (gameRecorder.isRecording()) {
            gameRecorder.cancelRecording();
            System.out.println("Recording cancelled");
        }
        recordingInitiator = null;
        isOnlineRecording = false;
    }
    
}