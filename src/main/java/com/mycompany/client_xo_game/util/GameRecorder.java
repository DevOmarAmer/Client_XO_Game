/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.client_xo_game.util;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonWriter;
import jakarta.json.JsonWriterFactory;
import jakarta.json.stream.JsonGenerator;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Alaa
 */
public class GameRecorder {
     private static final String RECORDS_DIR = "game_records";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    
    private List<MoveData> moves;
    private String player1Name;
    private String player2Name;
    private LocalDateTime gameStartTime;
    private boolean isRecording;
    
    public GameRecorder() {
        this.moves = new ArrayList<>();
        this.isRecording = false;
    }
    
    public void startRecording(String player1Name, String player2Name) {
        this.player1Name = player1Name;
        this.player2Name = player2Name;
        this.gameStartTime = LocalDateTime.now();
        this.moves = new ArrayList<>();
        this.isRecording = true;
        
        File dir = new File(RECORDS_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
    
    public void recordMove(int row, int col, String symbol, String playerName) {
        if (!isRecording) {
            return;
        }
        
        MoveData move = new MoveData(
            moves.size() + 1,
            row,
            col,
            symbol,
            playerName,
            LocalDateTime.now()
        );
        moves.add(move);
    }

    private static class MoveData {
        int moveNumber;
        int row;
        int col;
        String symbol;
        String playerName;
        LocalDateTime timestamp;
        
        MoveData(int moveNumber, int row, int col, String symbol, 
                String playerName, LocalDateTime timestamp) {
            this.moveNumber = moveNumber;
            this.row = row;
            this.col = col;
            this.symbol = symbol;
            this.playerName = playerName;
            this.timestamp = timestamp;
        }
    }
}
