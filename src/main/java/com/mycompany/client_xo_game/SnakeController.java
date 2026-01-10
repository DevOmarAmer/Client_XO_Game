package com.mycompany.client_xo_game;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.util.LinkedList;
import java.util.Random;
import javafx.scene.layout.VBox;

public class SnakeController {

    @FXML private Canvas canvas;
    @FXML private StackPane root;
    @FXML private Label scoreLabel;
    @FXML private VBox gameOverPane; // MATCH the FXML VBox


    private GraphicsContext gc;

    private static final int CELL_SIZE = 20;
    private static final int GRID_WIDTH = 30;
    private static final int GRID_HEIGHT = 20;

    private LinkedList<Point> snake;
    private Direction direction = Direction.RIGHT;
    private Direction nextDirection = Direction.RIGHT;

    private Point food;
    private int score = 0;

    private AnimationTimer gameLoop;
    private long lastUpdate = 0;
    private static final long MOVE_INTERVAL = 80_000_000;

    @FXML
    public void initialize() {
        gc = canvas.getGraphicsContext2D();
        canvas.setFocusTraversable(true);
        setupControls();
        startNewGame();
    }

    private void startNewGame() {
        snake = new LinkedList<>();
        snake.add(new Point(10, 10));
        snake.add(new Point(9, 10));
        snake.add(new Point(8, 10));

        direction = Direction.RIGHT;
        nextDirection = Direction.RIGHT;
        score = 0;

        spawnFood();
        gameOverPane.setVisible(false);
        startGameLoop();
    }

    private void startGameLoop() {
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
        direction = nextDirection;

        Point head = snake.getFirst();
        Point newHead = head.move(direction);

        // wrap around (NO BOUNDARIES)
        newHead.x = (newHead.x + GRID_WIDTH) % GRID_WIDTH;
        newHead.y = (newHead.y + GRID_HEIGHT) % GRID_HEIGHT;

        if (snake.contains(newHead)) {
            gameOver();
            return;
        }

        snake.addFirst(newHead);

        if (newHead.equals(food)) {
            score++;
            spawnFood();
        } else {
            snake.removeLast();
        }
    }

    private void draw() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // snake
        gc.setFill(Color.LIMEGREEN);
        for (Point p : snake) {
            gc.fillRoundRect(
                    p.x * CELL_SIZE,
                    p.y * CELL_SIZE,
                    CELL_SIZE,
                    CELL_SIZE,
                    10,
                    10
            );
        }

        // apple (CIRCLE)
        gc.setFill(Color.RED);
        gc.fillOval(
                food.x * CELL_SIZE + 3,
                food.y * CELL_SIZE + 3,
                CELL_SIZE - 6,
                CELL_SIZE - 6
        );
    }

    private void spawnFood() {
        Random rand = new Random();
        Point p;
        do {
            p = new Point(rand.nextInt(GRID_WIDTH), rand.nextInt(GRID_HEIGHT));
        } while (snake.contains(p));
        food = p;
    }

    private void gameOver() {
        gameLoop.stop();
        scoreLabel.setText("Score: " + score);
        gameOverPane.setVisible(true);
    }

    @FXML
    private void playAgain() {
        startNewGame();
    }

    private void setupControls() {
        canvas.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case UP:
                    if (direction != Direction.DOWN) nextDirection = Direction.UP;
                    break;
                case DOWN:
                    if (direction != Direction.UP) nextDirection = Direction.DOWN;
                    break;
                case LEFT:
                    if (direction != Direction.RIGHT) nextDirection = Direction.LEFT;
                    break;
                case RIGHT:
                    if (direction != Direction.LEFT) nextDirection = Direction.RIGHT;
                    break;
            }
        });
    }

    // =====================
    // SUPPORT CLASSES
    // =====================

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
                case UP:    return new Point(x, y - 1);
                case DOWN:  return new Point(x, y + 1);
                case LEFT:  return new Point(x - 1, y);
                default:    return new Point(x + 1, y);
            }
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Point)) return false;
            Point p = (Point) o;
            return p.x == x && p.y == y;
        }

        @Override
        public int hashCode() {
            return x * 31 + y;
        }
    }
}
