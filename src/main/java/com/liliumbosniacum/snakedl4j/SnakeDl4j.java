package com.liliumbosniacum.snakedl4j;

import com.liliumbosniacum.snakedl4j.game.Game;
import com.liliumbosniacum.snakedl4j.network.GameMode;
import com.liliumbosniacum.snakedl4j.network.util.NetworkEvaluationHelper;
import com.liliumbosniacum.snakedl4j.network.util.NetworkTrainingHelper;

import javax.swing.JFrame;
import javax.swing.WindowConstants;
import java.awt.EventQueue;

public class SnakeDl4j extends JFrame {

    private SnakeDl4j(final GameMode mode) {
        final Game game = new Game();
        add(game);
        setResizable(false);
        pack();

        setTitle(SnakeDl4j.class.getSimpleName() + " - " + mode);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        if (mode == GameMode.TRAIN) {
            NetworkTrainingHelper.startTraining(game);
            return;
        }

        if(mode == GameMode.EVALUATE) {
            NetworkEvaluationHelper.startEvaluating(game);
        }
    }

    public static void main(String[] args) {
        // Game mode to execute. Start the application with available game modes provided in the program arguments
        final GameMode mode = GameMode.create(args[0]);

        EventQueue.invokeLater(() -> {
            JFrame ex = new SnakeDl4j(mode);
            ex.setVisible(true);
        });
    }
}
