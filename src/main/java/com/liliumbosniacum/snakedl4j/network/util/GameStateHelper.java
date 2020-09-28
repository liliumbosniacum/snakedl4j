package com.liliumbosniacum.snakedl4j.network.util;

import com.liliumbosniacum.snakedl4j.game.helper.Direction;
import com.liliumbosniacum.snakedl4j.game.helper.GameUtils;
import com.liliumbosniacum.snakedl4j.game.helper.Position;
import com.liliumbosniacum.snakedl4j.network.Action;
import com.liliumbosniacum.snakedl4j.network.GameState;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Helper class used to ease out creation of game states.
 *
 * @author mirza
 */
public final class GameStateHelper {
    // region Member
    /**
     * Dictates how far can the snake see. Number of network inputs depends on it.
     */
    private static final int VIEW_DISTANCE = 3;
    private static final int FOOD_EATEN_REWARD = 100;
    // endregion

    // region Constructor
    private GameStateHelper() {}
    // endregion

    // region Implementation
    /**
     * Public create game state.
     *
     * @param snakePosition Current snake position.
     * @param currentDirection Current direction.
     * @param foodPosition Food position.
     * @return Returns created game state object.
     */
    public static GameState createGameState(final Position[] snakePosition,
                                            final Direction currentDirection,
                                            final Position foodPosition) {
        final Position headPosition = snakePosition[0];
        final Position[] snakeBodyPosition = Arrays.copyOfRange(snakePosition, 1, snakePosition.length);

        final Boolean[] states = mergeStates(
                currentDirection == Direction.DOWN
                        ? getNegativeStates() : getStatsForDirectionUp(snakeBodyPosition, headPosition),
                currentDirection == Direction.LEFT
                        ? getNegativeStates() : getStatsForDirectionRight(snakeBodyPosition, headPosition),
                currentDirection == Direction.UP
                        ? getNegativeStates() : getStatsForDirectionDown(snakeBodyPosition, headPosition),
                currentDirection == Direction.RIGHT
                        ? getNegativeStates() : getStatsForDirectionLeft(snakeBodyPosition, headPosition),
                getFoodStates(headPosition, foodPosition)
        );

        return new GameState(states);
    }

    public static double getScoreForAction(final Action action,
                                           final Position[] snakePosition,
                                           final Position foodPosition) {
        final Position headPosition = snakePosition[0];
        final Position[] snakeBodyPosition = Arrays.copyOfRange(snakePosition, 1, snakePosition.length);

        final Boolean[] foodStates = getFoodStates(headPosition, foodPosition);

        double score = 0;
        switch (action) {
            case MOVE_UP:
                score += getScoreForStates(getStatsForDirectionUp(snakeBodyPosition, headPosition));
                score += getScoreForFoodState(foodStates, 0);
                score += getScoreForFoodState(foodStates, 4);
                score += getScoreForFoodState(foodStates, 5);
                score += foodPosition.equals(new Position(headPosition.getX(), headPosition.getY() - GameUtils.PLAYER_SIZE)) ? FOOD_EATEN_REWARD : 0;
                break;
            case MOVE_RIGHT:
                score += getScoreForStates(getStatsForDirectionRight(snakeBodyPosition, headPosition));
                score += getScoreForFoodState(foodStates, 1);
                score += getScoreForFoodState(foodStates, 4);
                score += getScoreForFoodState(foodStates, 6);
                score += foodPosition.equals(new Position(headPosition.getX() + GameUtils.PLAYER_SIZE, headPosition.getY())) ? FOOD_EATEN_REWARD : 0;
                break;
            case MOVE_DOWN:
                score += getScoreForStates(getStatsForDirectionDown(snakeBodyPosition, headPosition));
                score += getScoreForFoodState(foodStates, 2);
                score += getScoreForFoodState(foodStates, 6);
                score += getScoreForFoodState(foodStates, 7);
                score += foodPosition.equals(new Position(headPosition.getX(), headPosition.getY() + GameUtils.PLAYER_SIZE)) ? FOOD_EATEN_REWARD : 0;
                break;
            case MOVE_LEFT:
                score += getScoreForStates(getStatsForDirectionLeft(snakeBodyPosition, headPosition));
                score += getScoreForFoodState(foodStates, 3);
                score += getScoreForFoodState(foodStates, 5);
                score += getScoreForFoodState(foodStates, 7);
                score += foodPosition.equals(new Position(headPosition.getX() - GameUtils.PLAYER_SIZE, headPosition.getY())) ? FOOD_EATEN_REWARD : 0;
                break;
            default:
                break;
        }

        return score;
    }

    /**
     * Get number of possible states. There are 4 directions in which snake can see. Number of inputs is equal to
     * those 4 directions times how far it can see plus 8 food states.
     *
     * @return Returns number of possible states.
     */
    public static int getNumberOfPossibleStates() {
        // View distance must always be at least 1
        return (4 * (VIEW_DISTANCE != 0 ? VIEW_DISTANCE : 1)) + 8;
    }
    // endregion

    // region Helper
    private static Boolean[] mergeStates(Boolean[] ...stateArrays) {
        return Stream.of(stateArrays)
                .flatMap(Stream::of)
                .toArray(Boolean[]::new);
    }

    private static Boolean[] getStatsForDirectionUp(final Position[] snakeBodyPosition,
                                                    final Position headPosition) {
        final Boolean[] states = new Boolean[VIEW_DISTANCE];

        for (int i = 1; i <= VIEW_DISTANCE; i++) {
            final Position tmpPosition = new Position(
                    headPosition.getX(),
                    headPosition.getY() - (GameUtils.PLAYER_SIZE * i)
            );

            states[i - 1] = isPositionPositive(tmpPosition, snakeBodyPosition, headPosition);
        }

        return states;
    }

    private static Boolean[] getStatsForDirectionRight(final Position[] snakeBodyPosition,
                                                       final Position headPosition) {
        final Boolean[] states = new Boolean[VIEW_DISTANCE];

        for (int i = 1; i <= VIEW_DISTANCE; i++) {
            final Position tmpPosition = new Position(
                    headPosition.getX() + (GameUtils.PLAYER_SIZE * i),
                    headPosition.getY()
            );

            states[i - 1] = isPositionPositive(tmpPosition, snakeBodyPosition, headPosition);
        }

        return states;
    }

    private static Boolean[] getStatsForDirectionDown(final Position[] snakeBodyPosition,
                                                      final Position headPosition) {
        final Boolean[] states = new Boolean[VIEW_DISTANCE];

        for (int i = 1; i <= VIEW_DISTANCE; i++) {
            final Position tmpPosition = new Position(
                    headPosition.getX(),
                    headPosition.getY() + (GameUtils.PLAYER_SIZE * i)
            );

            states[i - 1] = isPositionPositive(tmpPosition, snakeBodyPosition, headPosition);
        }

        return states;
    }

    private static Boolean[] getStatsForDirectionLeft(final Position[] snakeBodyPosition,
                                                      final Position headPosition) {
        final Boolean[] states = new Boolean[VIEW_DISTANCE];

        for (int i = 1; i <= VIEW_DISTANCE; i++) {
            final Position tmpPosition = new Position(
                    headPosition.getX() - (GameUtils.PLAYER_SIZE * i),
                    headPosition.getY()
            );

            states[i - 1] = isPositionPositive(tmpPosition, snakeBodyPosition, headPosition);
        }

        return states;
    }

    private static Boolean[] getNegativeStates() {
        final Boolean[] states = new Boolean[VIEW_DISTANCE];
        for (int i = 1; i <= VIEW_DISTANCE; i++) {
            states[i - 1] = false;
        }

        return states;
    }

    private static Boolean[] getFoodStates(final Position headPosition, final Position foodPosition) {
        boolean isFoodUp = foodPosition.getY() < headPosition.getY();
        boolean isFoodRight = foodPosition.getX() > headPosition.getX();
        boolean isFoodDown = foodPosition.getY() > headPosition.getY();
        boolean isFoodLeft = foodPosition.getX() < headPosition.getX();

        return new Boolean[] {
                isFoodUp, // Is food up
                isFoodRight, // Is food right
                isFoodDown, // Is food down
                isFoodLeft, // Is food left
                isFoodUp && isFoodRight, // Is food up right
                isFoodUp && isFoodLeft, // Is food up left
                isFoodDown && isFoodRight, // Is food down right
                isFoodDown && isFoodLeft // Is food down left
        };
    }

    private static double getScoreForStates(final Boolean[] states) {
        if (Arrays.stream(states).allMatch(x -> false)) {
            return -100; // Will die
        }

        return -1;
    }

    private static double getScoreForFoodState(final Boolean[] foodState, final int index) {
        return foodState[index] ? 0.5 : 0;
    }

    /**
     * Position counts as positive if it is inside the bounds and if it is not already contained within snake body
     * positions.
     *
     * @param position Position to check.
     * @param snakeBody Snake body.
     * @param snakeHead Snake head.
     * @return Returns true if position is positive.
     */
    private static Boolean isPositionPositive(final Position position,
                                              final Position[] snakeBody,
                                              final Position snakeHead) {
        return !position.isOutsideTheGameBounds()
                && Arrays.stream(snakeBody).noneMatch(snakeHead::equals);
    }
    // endregion
}
