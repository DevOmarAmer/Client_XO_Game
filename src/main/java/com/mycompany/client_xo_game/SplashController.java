package com.mycompany.client_xo_game;

import com.mycompany.client_xo_game.navigation.Navigation;
import com.mycompany.client_xo_game.navigation.Routes;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle; // Added missing import
import javafx.util.Duration;

public class SplashController implements Initializable {

    @FXML
    private StackPane rootPane;
    @FXML
    private HBox logoContainer;
    @FXML
    private Label labelX;
    @FXML
    private Label labelO;
    @FXML
    private Label loadingLabel;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label continueLabel;
    @FXML
    private Circle ring1; // Added Circle injection
    @FXML
    private Circle ring2; // Added Circle injection

    private Task<Void> loadTask;

    // AtomicBoolean ensures we don't trigger navigation twice
    private final AtomicBoolean isNavigating = new AtomicBoolean(false);
    // Flag to track if loading is actually done
    private final AtomicBoolean isLoaded = new AtomicBoolean(false);

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // START RING ANIMATION (Check for null to prevent NPE during scene builder preview or partial loading)
        if (ring1 != null) {
            playRingAnimation(ring1, 0);
        }
        if (ring2 != null) {
            playRingAnimation(ring2, 500); // 500ms delay for second ring
        }
        // 1. Setup Animations
        playEntranceAnimation();

        // Hide continue label initially
        if (continueLabel != null) {
            continueLabel.setOpacity(0);
        }

        // 2. Setup Input Handlers (Mouse & Keyboard)
        setupInputHandlers();

        // 3. Start Loading Task
        startLoadingTask();

        // 4. Request focus for keyboard input
        Platform.runLater(() -> {
            if (rootPane != null) {
                rootPane.requestFocus();
            }
        });
    }

    // ==========================================
    //              ANIMATIONS
    // ==========================================
    private void playEntranceAnimation() {
        if (rootPane == null) {
            return;
        }

        // Fade in entire screen
        FadeTransition fadeIn = new FadeTransition(Duration.millis(1200), rootPane);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        // Slide Logos
        TranslateTransition moveX = new TranslateTransition(Duration.millis(800), labelX);
        moveX.setFromX(-100);
        moveX.setToX(0);

        TranslateTransition moveO = new TranslateTransition(Duration.millis(800), labelO);
        moveO.setFromX(100);
        moveO.setToX(0);

        // Pulse Logo Container
        ScaleTransition pulse = new ScaleTransition(Duration.millis(1000), logoContainer);
        pulse.setByX(0.1);
        pulse.setByY(0.1);
        pulse.setCycleCount(Animation.INDEFINITE);
        pulse.setAutoReverse(true);

        ParallelTransition pt = new ParallelTransition(fadeIn, moveX, moveO, pulse);
        pt.play();
    }

    private void playBreathingText() {
        if (continueLabel == null) {
            return;
        }

        // Make label visible and start pulsing
        continueLabel.setOpacity(1);
        FadeTransition breathe = new FadeTransition(Duration.seconds(1.0), continueLabel);
        breathe.setFromValue(0.4);
        breathe.setToValue(1.0);
        breathe.setCycleCount(Animation.INDEFINITE);
        breathe.setAutoReverse(true);
        breathe.play();
    }

    private void playRingAnimation(Circle ring, double delayMillis) {
        if (ring == null) {
            return;
        }

        // Scale Up
        ScaleTransition scale = new ScaleTransition(Duration.seconds(2.5), ring);
        scale.setFromX(0.5);
        scale.setFromY(0.5);
        scale.setToX(1.5);
        scale.setToY(1.5);

        // Fade Out
        FadeTransition fade = new FadeTransition(Duration.seconds(2.5), ring);
        fade.setFromValue(0.6); // Start partially visible
        fade.setToValue(0.0);   // Fade to invisible

        ParallelTransition pt = new ParallelTransition(scale, fade);
        pt.setDelay(Duration.millis(delayMillis));
        pt.setCycleCount(Animation.INDEFINITE);
        pt.play();
    }

    // ==========================================
    //              INPUT HANDLING
    // ==========================================
    private void setupInputHandlers() {
        if (rootPane == null) {
            return;
        }

        // Mouse Click - Triggers ONLY if loaded
        rootPane.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (isLoaded.get()) {
                finishSplash();
            }
        });

        // Keyboard Press - Triggers ONLY if loaded
        rootPane.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (isLoaded.get()) {
                finishSplash();
            }
        });
    }

    // ==========================================
    //              LOADING LOGIC
    // ==========================================
    private void startLoadingTask() {
        loadTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                // Step 1: Assets
                updateMessage("Loading Game Assets...");
                updateProgress(0.1, 1.0);
                Thread.sleep(600);

                if (isCancelled()) {
                    return null;
                }

                // Step 2: Local Environment
                updateMessage("Setting up Offline Mode...");
                updateProgress(0.4, 1.0);
                Thread.sleep(700);

                if (isCancelled()) {
                    return null;
                }

                // Step 3: AI
                updateMessage("Configuring AI Engine...");
                updateProgress(0.7, 1.0);
                Thread.sleep(600);

                if (isCancelled()) {
                    return null;
                }

                // Step 4: Online Module Prep (Not connecting, just loading classes)
                updateMessage("Initializing Online Module...");
                updateProgress(0.9, 1.0);
                Thread.sleep(500);

                // Finalize
                updateMessage("Game Ready!");
                updateProgress(1.0, 1.0);

                return null;
            }
        };

        if (progressBar != null) {
            progressBar.progressProperty().bind(loadTask.progressProperty());
        }
        if (loadingLabel != null) {
            loadingLabel.textProperty().bind(loadTask.messageProperty());
        }

        // When Task Succeeds
        loadTask.setOnSucceeded(e -> {
            // 1. Mark as loaded so inputs work
            isLoaded.set(true);

            // 2. Unbind progress bar
            if (progressBar != null) {
                progressBar.progressProperty().unbind();
            }
            if (loadingLabel != null) {
                loadingLabel.textProperty().unbind();
            }

            // 3. Visual Cues: Progress bar full, Text updates
            if (progressBar != null) {
                progressBar.setProgress(1.0);
            }
            if (loadingLabel != null) {
                loadingLabel.setText("Ready To Battle");
            }

            // 4. Start the "Click to Continue" breathing animation
            playBreathingText();
        });

        new Thread(loadTask).start();
    }

    // ==========================================
    //              TRANSITION
    // ==========================================
    private void finishSplash() {
        // Prevent double navigation
        if (isNavigating.getAndSet(true)) {
            return;
        }

        // Play Exit Animation and Navigate
        FadeTransition fadeOut = new FadeTransition(Duration.millis(400), rootPane);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(event -> {
            Navigation.goTo(Routes.MODE_SELECTION);
        });
        fadeOut.play();
    }
}
