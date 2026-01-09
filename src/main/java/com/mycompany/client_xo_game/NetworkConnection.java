package com.mycompany.client_xo_game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import javafx.application.Platform;
import org.json.JSONObject;

public class NetworkConnection {

    private static Map<String, NetworkConnection> instances = new HashMap<>();
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private int port = 8888;         // Must match Server.PORT
    private Consumer<JSONObject> listener;
    private String currentUsername; // Store logged-in username

    private NetworkConnection(String ip) {
        try {
            socket = new Socket(ip, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            startListening();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static NetworkConnection getInstance(String ip) {
        if (!instances.containsKey(ip)) {
            instances.put(ip, new NetworkConnection(ip));
        }
        return instances.get(ip);
    }

    public static NetworkConnection getInstance() {
        return getInstance("127.0.0.1");
    }

    public void setCurrentUsername(String username) {
        this.currentUsername = username;
    }

    public String getCurrentUsername() {
        return currentUsername;
    }

    public void setListener(Consumer<JSONObject> listener) {
        this.listener = listener;
    }

    public void sendMessage(JSONObject json) {
        if (out != null) {
            out.println(json.toString());
        }
    }

    private void startListening() {
        new Thread(() -> {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    // Parse incoming JSON
                    JSONObject json = new JSONObject(message);
                    processMessage(json);
                }
            } catch (IOException e) {
                System.out.println("Connection to server lost.");
            }
        }).start();
    }

    private void processMessage(JSONObject json) {
        if (listener != null) {
            Platform.runLater(() -> listener.accept(json));
        }
    }

}
