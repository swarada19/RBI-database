package com.zetcode;

import javax.swing.*;

public class App {
    public static void main(String[] args) throws Exception {
        int boardWidth = 600;
        int boardHeight = boardWidth;

        JFrame frame = new JFrame("Two Player Snake Game");
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        TwoPlayerSnakeGame snakeGame = new TwoPlayerSnakeGame(boardWidth, boardHeight);
        frame.add(snakeGame);
        frame.pack();
        frame.setVisible(true);
        snakeGame.requestFocus();
    }
}
