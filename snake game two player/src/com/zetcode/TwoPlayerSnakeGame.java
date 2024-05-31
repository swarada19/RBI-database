package com.zetcode;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class TwoPlayerSnakeGame extends JPanel implements ActionListener, KeyListener {
    private class Tile {
        int x;
        int y;

        Tile(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    int boardWidth;
    int boardHeight;
    int tileSize = 25;

    // Snake 1
    Tile snake1Head;
    ArrayList<Tile> snake1Body;
    int velocity1X;
    int velocity1Y;
    boolean gameOver1 = false;

    // Snake 2
    Tile snake2Head;
    ArrayList<Tile> snake2Body;
    int velocity2X;
    int velocity2Y;
    boolean gameOver2 = false;

    // Food
    Tile food;
    Random random;

    // Game logic
    Timer gameLoop;

    TwoPlayerSnakeGame(int boardWidth, int boardHeight) {
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        setPreferredSize(new Dimension(this.boardWidth, this.boardHeight));
        setBackground(Color.black);
        addKeyListener(this);
        setFocusable(true);

        // Initialize snake 1
        snake1Head = new Tile(5, 5);
        snake1Body = new ArrayList<>();

        // Initialize snake 2
        snake2Head = new Tile(15, 15);
        snake2Body = new ArrayList<>();

        // Initialize food
        food = new Tile(10, 10);
        random = new Random();
        placeFood();

        // Initialize velocities
        velocity1X = 1;
        velocity1Y = 0;
        velocity2X = -1;
        velocity2Y = 0;

        // Game timer
        gameLoop = new Timer(100, this);
        gameLoop.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        // Grid Lines
        for (int i = 0; i < boardWidth / tileSize; i++) {
            g.drawLine(i * tileSize, 0, i * tileSize, boardHeight);
            g.drawLine(0, i * tileSize, boardWidth, i * tileSize);
        }

        // Food
        g.setColor(Color.red);
        g.fill3DRect(food.x * tileSize, food.y * tileSize, tileSize, tileSize, true);

        // Snake 1
        g.setColor(Color.green);
        g.fill3DRect(snake1Head.x * tileSize, snake1Head.y * tileSize, tileSize, tileSize, true);
        for (Tile snakePart : snake1Body) {
            g.fill3DRect(snakePart.x * tileSize, snakePart.y * tileSize, tileSize, tileSize, true);
        }

        // Snake 2
        g.setColor(Color.blue);
        g.fill3DRect(snake2Head.x * tileSize, snake2Head.y * tileSize, tileSize, tileSize, true);
        for (Tile snakePart : snake2Body) {
            g.fill3DRect(snakePart.x * tileSize, snakePart.y * tileSize, tileSize, tileSize, true);
        }

        // Scores
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        if (gameOver1) {
            g.setColor(Color.red);
            g.drawString("Player 1 Game Over: " + snake1Body.size(), tileSize - 16, tileSize);
        } else {
            g.setColor(Color.green);
            g.drawString("Player 1 Score: " + snake1Body.size(), tileSize - 16, tileSize);
        }

        if (gameOver2) {
            g.setColor(Color.red);
            g.drawString("Player 2 Game Over: " + snake2Body.size(), boardWidth - 160, tileSize);
        } else {
            g.setColor(Color.blue);
            g.drawString("Player 2 Score: " + snake2Body.size(), boardWidth - 160, tileSize);
        }
    }

    public void placeFood() {
        food.x = random.nextInt(boardWidth / tileSize);
        food.y = random.nextInt(boardHeight / tileSize);
    }

    public void move() {
        if (!gameOver1) {
            moveSnake(snake1Head, snake1Body, velocity1X, velocity1Y);
        }
        if (!gameOver2) {
            moveSnake(snake2Head, snake2Body, velocity2X, velocity2Y);
        }

        // Check for collisions between snakes
        checkCollisions();
    }

    public void moveSnake(Tile head, ArrayList<Tile> body, int velocityX, int velocityY) {
        if (collision(head, food)) {
            body.add(new Tile(food.x, food.y));
            placeFood();
        }

        for (int i = body.size() - 1; i >= 0; i--) {
            Tile snakePart = body.get(i);
            if (i == 0) {
                snakePart.x = head.x;
                snakePart.y = head.y;
            } else {
                Tile prevSnakePart = body.get(i - 1);
                snakePart.x = prevSnakePart.x;
                snakePart.y = prevSnakePart.y;
            }
        }

        head.x += velocityX;
        head.y += velocityY;
    }

    public void checkCollisions() {
        // Check for self-collisions and wall collisions
        checkGameOver(snake1Head, snake1Body);
        checkGameOver(snake2Head, snake2Body);

        // Check for collisions between snakes
        for (Tile part : snake1Body) {
            if (collision(snake2Head, part)) {
                gameOver2 = true;
            }
        }
        for (Tile part : snake2Body) {
            if (collision(snake1Head, part)) {
                gameOver1 = true;
            }
        }
    }

    public void checkGameOver(Tile head, ArrayList<Tile> body) {
        for (Tile part : body) {
            if (collision(head, part)) {
                if (head == snake1Head) {
                    gameOver1 = true;
                } else {
                    gameOver2 = true;
                }
            }
        }

        if (head.x * tileSize < 0 || head.x * tileSize >= boardWidth || head.y * tileSize < 0 || head.y * tileSize >= boardHeight) {
            if (head == snake1Head) {
                gameOver1 = true;
            } else {
                gameOver2 = true;
            }
        }
    }

    public boolean collision(Tile tile1, Tile tile2) {
        return tile1.x == tile2.x && tile1.y == tile2.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver1 && gameOver2) {
            gameLoop.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // Player 1 controls (WASD)
        if (e.getKeyCode() == KeyEvent.VK_W && velocity1Y != 1) {
            velocity1X = 0;
            velocity1Y = -1;
        } else if (e.getKeyCode() == KeyEvent.VK_S && velocity1Y != -1) {
            velocity1X = 0;
            velocity1Y = 1;
        } else if (e.getKeyCode() == KeyEvent.VK_A && velocity1X != 1) {
            velocity1X = -1;
            velocity1Y = 0;
        } else if (e.getKeyCode() == KeyEvent.VK_D && velocity1X != -1) {
            velocity1X = 1;
            velocity1Y = 0;
        }

        // Player 2 controls (Arrow keys)
        if (e.getKeyCode() == KeyEvent.VK_UP && velocity2Y != 1) {
            velocity2X = 0;
            velocity2Y = -1;
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN && velocity2Y != -1) {
            velocity2X = 0;
            velocity2Y = 1;
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT && velocity2X != 1) {
            velocity2X = -1;
            velocity2Y = 0;
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT && velocity2X != -1) {
            velocity2X = 1;
            velocity2Y = 0;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Two Player Snake Game");
        TwoPlayerSnakeGame game = new TwoPlayerSnakeGame(600, 600);
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
