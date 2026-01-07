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

public class GameRecorder {
    private static final String RECORDS_DIR = "game_records";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    
    private List<MoveData> moves;
    private String player1Name;
    private String player2Name;
    private LocalDateTime gameStartTime;
    private boolean isRecording;
    private boolean isOnlineGame;
    
    public GameRecorder() {
        this.moves = new ArrayList<>();
        this.isRecording = false;
        this.isOnlineGame = false;
    }
    
    public void startRecording(String player1Name, String player2Name) {
        startRecording(player1Name, player2Name, false);
    }
    
    public void startRecording(String player1Name, String player2Name, boolean isOnline) {
        this.player1Name = player1Name;
        this.player2Name = player2Name;
        this.gameStartTime = LocalDateTime.now();
        this.moves = new ArrayList<>();
        this.isRecording = true;
        this.isOnlineGame = isOnline;
        
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
    
    public String saveGame(String result) {
        if (!isRecording || moves.isEmpty()) {
            return null;
        }
        
        try {
            String fileName = generateFileName();
            File file = new File(RECORDS_DIR, fileName);
       
            JsonArrayBuilder movesArrayBuilder = Json.createArrayBuilder();
            for (MoveData move : moves) {
                JsonObject moveObj = Json.createObjectBuilder()
                    .add("moveNumber", move.moveNumber)
                    .add("row", move.row)
                    .add("col", move.col)
                    .add("symbol", move.symbol)
                    .add("playerName", move.playerName)
                    .add("timestamp", move.timestamp.format(ISO_FORMATTER))
                    .build();
                movesArrayBuilder.add(moveObj);
            }
            
            // Add [ONLINE] prefix to player2Name if it's an online game
            String displayPlayer2Name = isOnlineGame ? "[ONLINE] " + player2Name : player2Name;
        
            JsonObject gameRecord = Json.createObjectBuilder()
                .add("player1Name", player1Name)
                .add("player2Name", displayPlayer2Name)
                .add("gameStartTime", gameStartTime.format(ISO_FORMATTER))
                .add("gameEndTime", LocalDateTime.now().format(ISO_FORMATTER))
                .add("result", result)
                .add("totalMoves", moves.size())
                .add("isOnlineGame", isOnlineGame)
                .add("moves", movesArrayBuilder)
                .build();
              
            Map<String, Object> config = new HashMap<>();
            config.put(JsonGenerator.PRETTY_PRINTING, true);
            JsonWriterFactory writerFactory = Json.createWriterFactory(config);
            
            try (JsonWriter writer = writerFactory.createWriter(new FileWriter(file))) {
                writer.writeObject(gameRecord);
            }
            
            isRecording = false;
            System.out.println("Game recorded successfully: " + fileName);
            return file.getAbsolutePath();
            
        } catch (IOException e) {
            System.err.println("Error saving game record: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    public static JsonObject loadGameRecord(File file) {
        try (FileReader fileReader = new FileReader(file);
             JsonReader jsonReader = Json.createReader(fileReader)) {
            return jsonReader.readObject();
        } catch (Exception e) {
            System.err.println("Error loading game record: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    public static File[] getAllGameRecords() {
        File recordsDir = new File(RECORDS_DIR);
        if (!recordsDir.exists() || !recordsDir.isDirectory()) {
            return new File[0];
        }
        
        File[] files = recordsDir.listFiles((dir, name) -> name.endsWith(".json"));
        return files != null ? files : new File[0];
    }
    
    private String generateFileName() {
        String date = gameStartTime.format(DATE_FORMATTER);
        String safeName1 = sanitizeFileName(player1Name);
        String safeName2 = sanitizeFileName(player2Name);
        String onlinePrefix = isOnlineGame ? "ONLINE_" : "";
        return onlinePrefix + safeName1 + "_VS_" + safeName2 + "_" + date + ".json";
    }
    
    private String sanitizeFileName(String name) {
        return name.replaceAll("[^a-zA-Z0-9-_]", "_");
    }
    
    public boolean isRecording() {
        return isRecording;
    }
    
    public void cancelRecording() {
        this.isRecording = false;
        this.moves.clear();
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