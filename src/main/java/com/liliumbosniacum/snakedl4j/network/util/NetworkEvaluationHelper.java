package com.liliumbosniacum.snakedl4j.network.util;

import com.liliumbosniacum.snakedl4j.game.Game;
import com.liliumbosniacum.snakedl4j.network.Action;
import com.liliumbosniacum.snakedl4j.network.GameState;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Helper class used to ease out network evaluation.
 *
 * @author mirza
 */
public class NetworkEvaluationHelper {
    // region Member
    private static final Logger LOG = LoggerFactory.getLogger(NetworkEvaluationHelper.class);
    private static final int NUMBER_OF_GAMES = 100;
    // endregion

    // region Constructor
    private NetworkEvaluationHelper() {}
    // endregion

    // region Implementation
    public static void startEvaluating(final Game game) {
        LOG.info("Starting evaluation of trained network");

        final Thread evaluate = new Thread(() -> {
            final MultiLayerNetwork network = loadNetwork();

            int highscore = 0;
            for (int i = 1; i <= NUMBER_OF_GAMES; i++) {
                game.initializeGame();

                int score = 0;
                GameState gameState = game.getGameState();
                while (game.isOngoing()) {
                    // Get action from the network
                    final Action action = NetworkUtil.getActionFromTheNetwork(gameState, network);

                    // Change direction based on outputted action
                    game.changeDirection(action);

                    // Move the player
                    game.move();

                    // Get next (current) state
                    gameState = game.getGameState();

                    // Get current score
                    score = game.getSnakeLength();

                    // Wait so that the user can see what exactly the snake is doing (remove it if you want full speed)
                    NetworkUtil.wait(20);
                }

                LOG.info("Session '{}' ended with score of '{}'", i, score);

                if (score > highscore) {
                    highscore = score;
                }
            }

            LOG.info("Highscore achieved by network is '{}'", highscore);
        });

        evaluate.start();
    }
    // endregion

    // region Helper
    private static MultiLayerNetwork loadNetwork() {
        try {
            return MultiLayerNetwork.load(new File(NetworkUtil.NETWORK_NAME), true);
        } catch (IOException e) {
            LOG.error("Failed to load network: '{}'", e.getMessage(), e);
        }

        return NetworkUtil.getNetwork();
    }
    // endregion
}
