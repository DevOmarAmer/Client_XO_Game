package com.mycompany.client_xo_game;

import com.mycompany.client_xo_game.navigation.Navigation;
import com.mycompany.client_xo_game.navigation.Routes;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;

import java.util.LinkedList;
import java.util.Random;

public class SnakeController {

    @FXML
    private Canvas canvas;
    @FXML
    private StackPane root;

    // UI Elements
    @FXML
    private Label scoreLabel;      // Final score in Game Over
    @FXML
    private Label lblLiveScore;    // Live score in HUD
    @FXML
    private VBox gameOverPane;

    private GraphicsContext gc;

    // Grid Settings
    private static final int CELL_SIZE = 25;
    private int gridWidth;
    private int gridHeight;

    // Enhanced Cyberpunk Colors
    private static final Color COLOR_BG = Color.web("#0a0a1a");           // Dark background
    private static final Color COLOR_GRID = Color.web("#1a1a3a");         // Grid lines
    private static final Color COLOR_GRID_GLOW = Color.web("#00f3ff", 0.1); // Grid glow
    private static final Color COLOR_SNAKE = Color.web("#00f3ff");        // Cyan
    private static final Color COLOR_SNAKE_HEAD = Color.web("#00ffff");   // Brighter cyan for head
    private static final Color COLOR_FOOD = Color.web("#ff00ff");         // Pink

    private LinkedList<Point> snake;
    private Direction direction = Direction.RIGHT;
    private Direction nextDirection = Direction.RIGHT;
    private Point food;
    private int score = 0;

    private AnimationTimer gameLoop;
    private long lastUpdate = 0;
    private static final long MOVE_INTERVAL = 80_000_000;
    private boolean isGameRunning = false;

    @FXML
    public void initialize() {
        gc = canvas.getGraphicsContext2D();
        canvas.setFocusTraversable(true);

        canvas.widthProperty().bind(root.widthProperty());
        canvas.heightProperty().bind(root.heightProperty());

        canvas.widthProperty().addListener(e -> handleResize());
        canvas.heightProperty().addListener(e -> handleResize());

        setupControls();
        Platform.runLater(this::startNewGame);
    }

    private void handleResize() {
        calculateGridDimensions();
        if (!isGameRunning && gridWidth > 0 && gridHeight > 0 && !gameOverPane.isVisible()) {
            startNewGame();
        }
    }

    private void calculateGridDimensions() {
        if (canvas.getWidth() > 0 && canvas.getHeight() > 0) {
            gridWidth = (int) canvas.getWidth() / CELL_SIZE;
            gridHeight = (int) canvas.getHeight() / CELL_SIZE;
        }
    }

    private void startNewGame() {
        calculateGridDimensions();
        if (gridWidth <= 0 || gridHeight <= 0) {
            return;
        }

        snake = new LinkedList<>();
        int startX = gridWidth / 2;
        int startY = gridHeight / 2;

        snake.add(new Point(startX, startY));
        snake.add(new Point(startX - 1, startY));
        snake.add(new Point(startX - 2, startY));

        direction = Direction.RIGHT;
        nextDirection = Direction.RIGHT;
        score = 0;
        updateScoreUI();

        spawnFood();

        // Reset UI
        gameOverPane.setVisible(false);
        canvas.setEffect(null); // Remove blur
        isGameRunning = true;

        startGameLoop();
    }

    private void startGameLoop() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (now - lastUpdate >= MOVE_INTERVAL) {
                    update();
                    draw();
                    lastUpdate = now;
                }
            }
        };
        gameLoop.start();
    }

    private void update() {
        if (!isGameRunning) {
            return;
        }
        calculateGridDimensions();

        direction = nextDirection;
        Point head = snake.getFirst();
        Point newHead = head.move(direction);

        // Wrap Around
        if (newHead.x >= gridWidth) {
            newHead.x = 0;
        } else if (newHead.x < 0) {
            newHead.x = gridWidth - 1;
        }
        if (newHead.y >= gridHeight) {
            newHead.y = 0;
        } else if (newHead.y < 0) {
            newHead.y = gridHeight - 1;
        }

        if (snake.contains(newHead)) {
            gameOver();
            return;
        }

        snake.addFirst(newHead);

        if (newHead.equals(food)) {
            score++;
            updateScoreUI();
            spawnFood();
        } else {
            snake.removeLast();
        }
    }

    private void updateScoreUI() {
        lblLiveScore.setText("SCORE: " + score);
    }

    private void draw() {
        // Clear and draw background
        gc.setFill(COLOR_BG);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Draw cyberpunk grid
        drawGrid();

        // Draw Snake with gradient effect
        drawSnake();

        // Draw Food with pulsing effect
        drawFood();
    }

    private void drawGrid() {
        gc.setLineWidth(1);
        
        // Draw vertical lines
        for (int x = 0; x <= gridWidth; x++) {
            double xPos = x * CELL_SIZE;
            
            // Alternating line intensity for depth
            if (x % 2 == 0) {
                gc.setStroke(COLOR_GRID);
            } else {
                gc.setStroke(COLOR_GRID_GLOW);
            }
            
            gc.strokeLine(xPos, 0, xPos, canvas.getHeight());
        }
        
        // Draw horizontal lines
        for (int y = 0; y <= gridHeight; y++) {
            double yPos = y * CELL_SIZE;
            
            if (y % 2 == 0) {
                gc.setStroke(COLOR_GRID);
            } else {
                gc.setStroke(COLOR_GRID_GLOW);
            }
            
            gc.strokeLine(0, yPos, canvas.getWidth(), yPos);
        }

        // Draw highlighted grid intersections
        gc.setFill(COLOR_GRID_GLOW);
        for (int x = 0; x <= gridWidth; x += 4) {
            for (int y = 0; y <= gridHeight; y += 4) {
                gc.fillOval(x * CELL_SIZE - 1.5, y * CELL_SIZE - 1.5, 3, 3);
            }
        }
    }

    private void drawSnake() {
        // Draw body segments
        gc.setEffect(new DropShadow(15, COLOR_SNAKE));
        
        for (int i = 0; i < snake.size(); i++) {
            Point p = snake.get(i);
            
            // Gradient effect - head is brighter
            if (i == 0) {
                gc.setFill(COLOR_SNAKE_HEAD);
                // Draw head with rounded corners
                gc.fillRoundRect(
                    p.x * CELL_SIZE + 2, 
                    p.y * CELL_SIZE + 2, 
                    CELL_SIZE - 4, 
                    CELL_SIZE - 4,
                    8, 8
                );
            } else {
                // Body segments fade slightly
                double opacity = 1.0 - (i * 0.02);
                opacity = Math.max(opacity, 0.6);
                gc.setFill(Color.web("#00f3ff", opacity));
                gc.fillRoundRect(
                    p.x * CELL_SIZE + 2, 
                    p.y * CELL_SIZE + 2, 
                    CELL_SIZE - 4, 
                    CELL_SIZE - 4,
                    6, 6
                );
            }
        }
        
        gc.setEffect(null);
    }

    private void drawFood() {
        if (food == null) return;

        // Pulsing effect
        long time = System.currentTimeMillis();
        double pulse = Math.sin(time / 200.0) * 0.2 + 0.8;
        
        gc.setFill(COLOR_FOOD);
        gc.setEffect(new DropShadow(25, COLOR_FOOD));
        
        double size = (CELL_SIZE - 8) * pulse;
        double offset = (CELL_SIZE - size) / 2;
        
        gc.fillOval(
            food.x * CELL_SIZE + offset, 
            food.y * CELL_SIZE + offset, 
            size, 
            size
        );
        
        // Inner glow
        gc.setFill(Color.web("#ffffff", 0.5));
        gc.fillOval(
            food.x * CELL_SIZE + offset + size * 0.3, 
            food.y * CELL_SIZE + offset + size * 0.3, 
            size * 0.4, 
            size * 0.4
        );
        
        gc.setEffect(null);
    }

    private void spawnFood() {
        if (gridWidth <= 0 || gridHeight <= 0) {
            return;
        }
        Random rand = new Random();
        Point p;
        do {
            p = new Point(rand.nextInt(gridWidth), rand.nextInt(gridHeight));
        } while (snake.contains(p));
        food = p;
    }

    private void gameOver() {
        gameLoop.stop();
        isGameRunning = false;

        scoreLabel.setText("FINAL SCORE: " + score);
        gameOverPane.setVisible(true);

        // Blur the background for better UI focus
        canvas.setEffect(new GaussianBlur(10));
    }

    @FXML
    private void playAgain() {
        startNewGame();
        canvas.requestFocus();
    }

    @FXML
    private void goToMenu() {
        showExitConfirmation();
    }

    private void showExitConfirmation() {
        // Pause the game
        boolean wasRunning = isGameRunning;
        if (isGameRunning && gameLoop != null) {
            gameLoop.stop();
            isGameRunning = false;
        }

        // Create confirmation dialog
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit Game");
        alert.setHeaderText("Are you sure you want to exit?");
        alert.setContentText("Your current progress will be lost.");

        // Style the dialog
        javafx.stage.Stage stage = (javafx.stage.Stage) alert.getDialogPane().getScene().getWindow();
        stage.initStyle(javafx.stage.StageStyle.UNDECORATED);
        
        // Apply cyberpunk styling
        alert.getDialogPane().setStyle(
            "-fx-background-color: rgba(10, 10, 26, 0.98);" +
            "-fx-border-color: #00f3ff;" +
            "-fx-border-width: 3px;" +
            "-fx-border-radius: 15px;" +
            "-fx-background-radius: 15px;" +
            "-fx-effect: dropshadow(three-pass-box, #00f3ff, 30, 0.7, 0, 0);"
        );
        
        alert.getDialogPane().lookup(".header-panel").setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #00f3ff;" +
            "-fx-font-family: 'Courier New', monospace;" +
            "-fx-font-size: 18px;" +
            "-fx-font-weight: bold;"
        );
        
        alert.getDialogPane().lookup(".content").setStyle(
            "-fx-text-fill: #ffffff;" +
            "-fx-font-family: 'Courier New', monospace;" +
            "-fx-font-size: 14px;"
        );

        // Custom buttons
        javafx.scene.control.ButtonType exitBtn = new javafx.scene.control.ButtonType("EXIT", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
        javafx.scene.control.ButtonType resumeBtn = new javafx.scene.control.ButtonType("RESUME", javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE);
        
        alert.getButtonTypes().setAll(exitBtn, resumeBtn);

        // Style buttons
        alert.getDialogPane().lookupButton(exitBtn).setStyle(
            "-fx-background-color: rgba(255, 0, 255, 0.15);" +
            "-fx-text-fill: #ff00ff;" +
            "-fx-border-color: #ff00ff;" +
            "-fx-border-width: 2px;" +
            "-fx-border-radius: 8px;" +
            "-fx-background-radius: 8px;" +
            "-fx-font-family: 'Courier New', monospace;" +
            "-fx-font-weight: bold;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 10 20;"
        );
        
        alert.getDialogPane().lookupButton(resumeBtn).setStyle(
            "-fx-background-color: rgba(0, 243, 255, 0.15);" +
            "-fx-text-fill: #00f3ff;" +
            "-fx-border-color: #00f3ff;" +
            "-fx-border-width: 2px;" +
            "-fx-border-radius: 8px;" +
            "-fx-background-radius: 8px;" +
            "-fx-font-family: 'Courier New', monospace;" +
            "-fx-font-weight: bold;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 10 20;"
        );

        // Show dialog and handle response
        alert.showAndWait().ifPresent(response -> {
            if (response == exitBtn) {
                // Exit to menu
                if (gameLoop != null) {
                    gameLoop.stop();
                }
                Navigation.goTo(Routes.MODE_SELECTION);
            } else {
                // Resume game
                if (wasRunning && !gameOverPane.isVisible()) {
                    isGameRunning = true;
                    startGameLoop();
                }
                canvas.requestFocus();
            }
        });
    }

    private void setupControls() {
        canvas.setFocusTraversable(true);
        canvas.requestFocus();
        canvas.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case UP:
                case W:
                    if (direction != Direction.DOWN) {
                        nextDirection = Direction.UP;
                    }
                    break;
                case DOWN:
                case S:
                    if (direction != Direction.UP) {
                        nextDirection = Direction.DOWN;
                    }
                    break;
                case LEFT:
                case A:
                    if (direction != Direction.RIGHT) {
                        nextDirection = Direction.LEFT;
                    }
                    break;
                case RIGHT:
                case D:
                    if (direction != Direction.LEFT) {
                        nextDirection = Direction.RIGHT;
                    }
                    break;
                case ESCAPE:
                    showExitConfirmation();
                    break;
            }
        });
        root.setOnMouseClicked(e -> canvas.requestFocus());
    }

    private enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    private static class Point {

        int x, y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        Point move(Direction d) {
            switch (d) {
                case UP:
                    return new Point(x, y - 1);
                case DOWN:
                    return new Point(x, y + 1);
                case LEFT:
                    return new Point(x - 1, y);
                default:
                    return new Point(x + 1, y);
            }
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Point)) {
                return false;
            }
            Point p = (Point) o;
            return p.x == x && p.y == y;
        }

        @Override
        public int hashCode() {
            return x * 31 + y;
        }
    }
}