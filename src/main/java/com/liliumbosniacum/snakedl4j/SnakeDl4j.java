package com.liliumbosniacum.snakedl4j;

import com.liliumbosniacum.snakedl4j.game.Game;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.CompletableFuture;

public class SnakeDl4j extends JFrame {

    private SnakeDl4j() {
        Game game = new Game();
        add(game);
        setResizable(false);
        pack();

        setTitle(SnakeDl4j.class.getSimpleName());
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        CompletableFuture.runAsync(() -> {
            game.initializeGame();
            for (int i = 0; i<50; i++) {
                game.move();
            }
        });
    }

    public static void main(String[] args) {

        EventQueue.invokeLater(() -> {
            JFrame ex = new SnakeDl4j();
            ex.setVisible(true);
        });
    }
}
