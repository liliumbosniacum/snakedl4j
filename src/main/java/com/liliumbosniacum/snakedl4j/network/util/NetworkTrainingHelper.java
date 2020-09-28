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
 * Helper class used to ease out network training.
 *
 * @author mirza
 */
public final class NetworkTrainingHelper {
    // region Member
    private static final Logger LOG = LoggerFactory.getLogger(NetworkTrainingHelper.class);
    private static final int NUMBER_OF_GAMES = 5_000;
    private static final int STUCK_SCORE = -500; // Score which indicates that the player is stuck (running in a loop)
    // endregion

    // region Constructor
    private NetworkTrainingHelper() {}
    // endregion

    // region Implementation
    public static void startTraining(final Game game) {
        final long startTime = System.currentTimeMillis();
        LOG.info("Starting new training session with '{}' games", NUMBER_OF_GAMES);

        final Thread train = new Thread(() -> {
            final MultiLayerNetwork network = NetworkUtil.getNetwork();
            network.init();
            double epsilon = 0.9;

            int largestSnakeLength = 0;
            for (int i = 1; i <= NUMBER_OF_GAMES; i++) {
                LOG.debug("Starting game session number '{}'", i);
                // Prepare the game world
                game.initializeGame();

                // Get current game state
                GameState state = game.getGameState();

                int gameSessionScore = 0;
                while (game.isOngoing()) {
                    if (gameSessionScore < STUCK_SCORE) {
                        LOG.error("Player is stuck, ending the game");
                        game.endGame();
                    }

                    // Select action based on current state
                    final Action action = NetworkUtil.epsilonGreedyAction(state, network, epsilon);

                    // Decrease epsilon value
                    epsilon -=0.001;

                    // Get score for selected action
                    final double score = GameStateHelper.getScoreForAction(
                            action,
                            game.getSnakePosition(),
                            game.getFoodPosition()
                    );

                    // Change direction based on selected action
                    game.changeDirection(action);

                    // Move the player
                    game.move();

                    // Get next (current) state
                    final GameState nextState = game.getGameState();

                    // Update network
                    NetworkUtil.update(state, action, score, nextState, network);

                    // Apply next state
                    state = nextState;

                    // Increment score
                    gameSessionScore += score;
                }

                final int snakeLength = game.getSnakeLength();
                LOG.debug("Total score for session '{}' is :'{}' with snake length of: '{}'",
                        i,
                        gameSessionScore,
                        snakeLength
                );

                if (snakeLength > largestSnakeLength) {
                    largestSnakeLength = snakeLength;
                    LOG.info("Current longest snake equals : '{}' at game session : '{}'", largestSnakeLength, i);
                }
            }

            LOG.info("All game sessions are over in '{}'ms, largest snake length was '{}'",
                    System.currentTimeMillis() - startTime,
                    largestSnakeLength
            );
            saveNetwork(network);
        });

        train.start();
    }
    // endregion

    // region Helper
    private static void saveNetwork(final MultiLayerNetwork network) {
        LOG.debug("Saving trained network");
        try {
            network.save(new File(NetworkUtil.NETWORK_NAME));
        } catch (IOException e) {
            LOG.error("Failed to save network: '{}'", e.getMessage(), e);
        }
    }
    // endregion
}
