package com.liliumbosniacum.snakedl4j.network.util;

import com.liliumbosniacum.snakedl4j.network.Action;
import com.liliumbosniacum.snakedl4j.network.GameState;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.BackpropType;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.nd4j.shade.guava.primitives.Booleans;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Helper class used to ease out handling of networks.
 *
 * @author mirza
 */
public final class NetworkUtil {
    // region Members
    /**
     * Name of the network that is used when saving and loading it.
     */
    public static final String NETWORK_NAME = "trained_network.zip";

    private static final Logger LOG = LoggerFactory.getLogger(NetworkUtil.class);
    private static final Map<String, Double> Q_TABLE = initQTable();
    private static final int HIDDEN_LAYER_COUNT = 150;
    // endregion

    // region Constructor
    private NetworkUtil() {}
    // endregion

    // region Implementation
    /**
     * Get the network for training.
     *
     * @return Returns {@link MultiLayerNetwork} used for training.
     */
    public static MultiLayerNetwork getNetwork() {
        return new MultiLayerNetwork(getConfiguration());
    }

    /**
     * Used to get action using epsilon greedy algorithm.
     *
     * @param state Current state of the game.
     * @param network Network.
     * @param epsilon Epsilon value.
     * @return Returns calculated action.
     */
    public static Action epsilonGreedyAction(final GameState state,
                                             final MultiLayerNetwork network,
                                             final double epsilon) {
        // https://www.geeksforgeeks.org/epsilon-greedy-algorithm-in-reinforcement-learning/
        final double random = getRandomDouble();
        if (random < epsilon) {
            return Action.getRandomAction();
        }

        return getActionFromTheNetwork(state, network);
    }

    /**
     * Gets the action from the network based on the current state.
     *
     * @param state Current state.
     * @param network Network.
     * @return Returns action outputed by the network
     */
    public static Action getActionFromTheNetwork(final GameState state, final MultiLayerNetwork network) {
        final INDArray output = network.output(toINDArray(state), false);

        /*
        Values provided by the network. Based on them we chose the current best action.
         */
        final float[] outputValues = output.data().asFloat();

        // Find index of the highest value
        final int maxValueIndex = getMaxValueIndex(outputValues);

        final Action actionByIndex = Action.getActionByIndex(maxValueIndex);
        LOG.debug("For values '{}' index of highest value is '{}' and action is '{}'",
                outputValues,
                maxValueIndex,
                actionByIndex
        );

        return actionByIndex;
    }

    /**
     * Update network and q-table with new values.
     *
     * @param state Current game state.
     * @param action Taken action.
     * @param score Achieved score.
     * @param nextState Next game state.
     * @param network Network.
     */
    public static void update(final GameState state,
                              final Action action,
                              final double score,
                              final GameState nextState,
                              final MultiLayerNetwork network) {
        // Get max q score for next state
        final double maxQScore = getMaxQScore(nextState);

        // Calculate target score
        final double targetScore = score + (0.9 * maxQScore);

        // Update the table with new score
        Q_TABLE.put(getStateWithActionString(state.getGameStateString(), action), targetScore);

        // Update network
        final INDArray stateObservation = toINDArray(state);
        final INDArray output = network.output(stateObservation);
        final INDArray updatedOutput = output.putScalar(action.getActionIndex(), targetScore);

        network.fit(stateObservation, updatedOutput);
    }

    /**
     * Puts the thread to sleep for certain amount of time.
     *
     * @param millis Time to sleep.
     */
    public static void wait(final int millis) {
        try {
            Thread.sleep(millis);
        } catch (final InterruptedException e) {
            LOG.error(e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
    }
    // endregion

    // region Helper
    private static MultiLayerConfiguration getConfiguration() {
        return new NeuralNetConfiguration.Builder()
                .seed(12345)    //Random number generator seed for improved repeatability
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .weightInit(WeightInit.XAVIER)
                .updater(new Adam(0.001))
                .l2(0.001) // l2 regularization on all layers
                .list()
                .layer(0, new DenseLayer.Builder()
                        .nIn(GameStateHelper.getNumberOfPossibleStates()) // Number of inputs
                        .nOut(HIDDEN_LAYER_COUNT)
                        .weightInit(WeightInit.XAVIER)
                        .activation(Activation.RELU)
                        .build())
                .layer(1, new DenseLayer.Builder()
                        .nIn(HIDDEN_LAYER_COUNT)
                        .nOut(HIDDEN_LAYER_COUNT)
                        .weightInit(WeightInit.XAVIER)
                        .activation(Activation.RELU)
                        .build())
                .layer(2, new OutputLayer.Builder(LossFunctions.LossFunction.MSE)
                        .nIn(HIDDEN_LAYER_COUNT)
                        .nOut(4) // Since we have 4 possible actions
                        .weightInit(WeightInit.XAVIER)
                        .activation(Activation.IDENTITY)
                        .weightInit(WeightInit.XAVIER)
                        .build())
                .backpropType(BackpropType.Standard)
                .build();
    }

    private static INDArray toINDArray(final GameState gameState) {
        return Nd4j.create(new boolean[][]{Booleans.toArray(Arrays.asList(gameState.getStates()))});
    }

    private static double getRandomDouble() {
        return (Math.random() * ((double) 1 + 1 - (double) 0)) + (double) 0;
    }

    private static int getMaxValueIndex(final float[] values) {
        int maxAt = 0;

        for (int i = 0; i < values.length; i++) {
            maxAt = values[i] > values[maxAt] ? i : maxAt;
        }

        return maxAt;
    }

    private static double getMaxQScore(final GameState state) {
        final String gameStateString = state.getGameStateString();

        final String stateWithActUP = getStateWithActionString(gameStateString, Action.MOVE_UP);
        final String stateWithActRIGHT = getStateWithActionString(gameStateString, Action.MOVE_RIGHT);
        final String stateWithActDOWN = getStateWithActionString(gameStateString, Action.MOVE_DOWN);
        final String stateWithActLEFT = getStateWithActionString(gameStateString, Action.MOVE_LEFT);

        double score =  Q_TABLE.get(stateWithActUP);

        final Double scoreRight = Q_TABLE.get(stateWithActRIGHT);
        if (scoreRight > score) {
            score = scoreRight;
        }

        final Double scoreDown = Q_TABLE.get(stateWithActDOWN);
        if (scoreDown > score) {
            score = scoreDown;
        }

        final Double scoreLeft = Q_TABLE.get(stateWithActLEFT);
        if (scoreLeft > score) {
            score = scoreLeft;
        }

        return score;
    }

    // Create table where all possible combinations of input states and actions taken will be stored (their score)
    private static Map<String, Double> initQTable() {
        final HashMap<String, Double> qTable = new HashMap<>();
        final List<String> inputs = getInputs(GameStateHelper.getNumberOfPossibleStates());

        for (final String stateInput : inputs) {
            qTable.put(getStateWithActionString(stateInput, Action.MOVE_UP), 0.0);
            qTable.put(getStateWithActionString(stateInput, Action.MOVE_RIGHT), 0.0);
            qTable.put(getStateWithActionString(stateInput, Action.MOVE_DOWN), 0.0);
            qTable.put(getStateWithActionString(stateInput, Action.MOVE_LEFT), 0.0);
        }

        return qTable;
    }

    private static List<String> getInputs(final int inputCount) {
        final List<String> inputs = new ArrayList<>();

        for (int i = 0; i < Math.pow(2, inputCount); i++) {
            String bin = Integer.toBinaryString(i);
            while (bin.length() < inputCount) {
                bin = "0" + bin;
            }

            inputs.add(String.copyValueOf(bin.toCharArray()));
        }

        return inputs;
    }

    private static String getStateWithActionString(final String stateString, final Action action) {
        return stateString + '-' + action;
    }
    // endregion
}
