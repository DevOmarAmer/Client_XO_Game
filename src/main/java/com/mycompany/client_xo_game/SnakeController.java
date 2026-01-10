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

    // Cyberpunk Colors
    private static final Color COLOR_SNAKE = Color.web("#00f3ff"); // Cyan
    private static final Color COLOR_FOOD = Color.web("#ff00ff");  // Pink

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
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Draw Snake
        gc.setFill(COLOR_SNAKE);
        gc.setEffect(new DropShadow(20, COLOR_SNAKE));
        for (Point p : snake) {
            gc.fillRect(p.x * CELL_SIZE + 1, p.y * CELL_SIZE + 1, CELL_SIZE - 2, CELL_SIZE - 2);
        }
        gc.setEffect(null);

        // Draw Food
        if (food != null) {
            gc.setFill(COLOR_FOOD);
            gc.setEffect(new DropShadow(20, COLOR_FOOD));
            gc.fillOval(food.x * CELL_SIZE + 4, food.y * CELL_SIZE + 4, CELL_SIZE - 8, CELL_SIZE - 8);
            gc.setEffect(null);
        }
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
        if (gameLoop != null) {
            gameLoop.stop();
        }
        Navigation.goTo(Routes.MODE_SELECTION);
    }

    private void setupControls() {
        canvas.setFocusTraversable(true);
        canvas.requestFocus();
        canvas.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case UP:
                    if (direction != Direction.DOWN) {
                        nextDirection = Direction.UP;
                    }
                    break;
                case DOWN:
                    if (direction != Direction.UP) {
                        nextDirection = Direction.DOWN;
                    }
                    break;
                case LEFT:
                    if (direction != Direction.RIGHT) {
                        nextDirection = Direction.LEFT;
                    }
                    break;
                case RIGHT:
                    if (direction != Direction.LEFT) {
                        nextDirection = Direction.RIGHT;
                    }
                    break;
                case ESCAPE:
                    goToMenu();
                    break; // Allow ESC to exit
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
