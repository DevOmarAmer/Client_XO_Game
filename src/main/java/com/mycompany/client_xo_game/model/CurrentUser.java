package com.mycompany.client_xo_game.model;

public class CurrentUser {
    private static String username;

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        CurrentUser.username = username;
    }
}
