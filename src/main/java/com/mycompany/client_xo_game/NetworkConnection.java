package com.mycompany.client_xo_game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.Consumer;
import javafx.application.Platform;
import org.json.JSONObject;

public class NetworkConnection {
    private static NetworkConnection instance;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String ip = "127.0.0.1"; // Change to Server IP if on different machine
    private int port = 6666;         // Must match Server.PORT
    private Consumer<JSONObject> listener;

    private NetworkConnection() {
        try {
            socket = new Socket(ip, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            startListening();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static NetworkConnection getInstance() {
        if (instance == null) {
            instance = new NetworkConnection();
        }
        return instance;
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