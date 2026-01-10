package com.mycompany.client_xo_game.util;

import com.mycompany.client_xo_game.App;
import javafx.scene.control.Alert;
import javafx.stage.StageStyle;

public class ClientUtils {

    public static void styleAlert(Alert alert) {
        // 1. Remove the "X" Window Bar
        alert.initStyle(StageStyle.UNDECORATED);

        // 2. Set the owner to App.getStage() so it stays on top of the app
        if (App.getStage() != null) {
            alert.initOwner(App.getStage());
        }

        // 3. Apply CSS
        var dialogPane = alert.getDialogPane();
        dialogPane.setId("xo-alert");

        // Use the global styles.css we created earlier
        var cssUrl = ClientUtils.class.getResource("/styles/styles.css");
        if (cssUrl != null) {
            dialogPane.getStylesheets().add(cssUrl.toExternalForm());
        }
    }

    public static void styleDialog(javafx.scene.control.Dialog<?> dialog) {
        // 1. Remove the "X" Window Bar
        dialog.initStyle(StageStyle.UNDECORATED);

        // 2. Set the owner to App.getStage() so it stays on top
        if (App.getStage() != null) {
            dialog.initOwner(App.getStage());
        }

        // 3. Apply CSS
        var dialogPane = dialog.getDialogPane();
        dialogPane.setId("xo-alert"); // Reuses the ID from your CSS

        // Use the global styles.css we created
        var cssUrl = ClientUtils.class.getResource("/styles/styles.css");
        if (cssUrl != null) {
            dialogPane.getStylesheets().add(cssUrl.toExternalForm());
        }
    }

}
