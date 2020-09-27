package com.liliumbosniacum.snakedl4j.game;

import com.liliumbosniacum.snakedl4j.game.helper.Direction;
import com.liliumbosniacum.snakedl4j.game.helper.GameUtils;
import com.liliumbosniacum.snakedl4j.game.helper.Position;
import com.liliumbosniacum.snakedl4j.network.Action;
import com.liliumbosniacum.snakedl4j.network.GameState;
import com.liliumbosniacum.snakedl4j.network.util.GameStateHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Implementation of a simple snake game with some extra methods needed for the network.
 * Original implementation can be found here https://github.com/janbodnar/Java-Snake-Game
 *
 * @author mirza
 */
public class Game extends JPanel implements ActionListener {
    // region Member
    private static final Logger LOG = LoggerFactory.getLogger(Game.class);

    private static final Image FOOD_IMAGE = GameUtils.getFoodImage();
    private static final Image TAIL_IMAGE = GameUtils.getTailImage();
    private static final Image HEAD_IMAGE = GameUtils.getHeadImage();

    // Used to keep track of all snake parts (positions of the tail and head)
    private transient Position[] snakePosition = new Position[900];

    private boolean inGame = true;
    private Direction currentDirection = Direction.RIGHT;
    private transient Position foodPosition;
    private int snakeLength;
    // endregion

    // region Setup
    public Game() {
        setBackground(Color.WHITE);
        setFocusable(true);
        setPreferredSize(new Dimension(GameUtils.GAME_DIMENSIONS, GameUtils.GAME_DIMENSIONS));

        initializeGame();
    }
    // endregion

    // region Implementation
    @Override
    public void actionPerformed(ActionEvent e) {
        if (inGame) {
            if (isFoodEaten()) {
                // Increase player length
                snakeLength++;

                // Set food on a new position
                setFoodPosition();
            } else {
                 final Position headPosition = snakePosition[0];
                 inGame = !headPosition.isOutsideTheGameBounds();

                 if (inGame) { // We only need to check for body part collision if we are still in the game
                     checkIfPlayerHeadIsCollidingWithOtherBodyParts(headPosition);
                 }
            }
        }

        if (!inGame) {
            LOG.debug("Game is over :(");
        }

        repaint();
    }

    @Override
    protected void paintComponent(final Graphics graphics) {
        super.paintComponent(graphics);

        draw(graphics);
    }

    /**
     * Move the player and check for collisions
     */
    public void move() {
        for (int i = snakeLength; i > 0; i--) {
            snakePosition[i] = snakePosition[i - 1];
        }

        final Position headPosition = snakePosition[0];

        switch (currentDirection) {
            case UP:
                snakePosition[0] = new Position(headPosition.getX(), headPosition.getY() - GameUtils.PLAYER_SIZE);
                break;
            case RIGHT:
                snakePosition[0] = new Position(headPosition.getX() + GameUtils.PLAYER_SIZE, headPosition.getY());
                break;
            case DOWN:
                snakePosition[0] = new Position(headPosition.getX(), headPosition.getY() + GameUtils.PLAYER_SIZE);
                break;
            case LEFT:
                snakePosition[0] = new Position(headPosition.getX() - GameUtils.PLAYER_SIZE, headPosition.getY());
                break;
            default:
                LOG.error("Unknown position");
        }

        // As we do not use any key pressed events to move our player we need "manually" notify about performed action
        actionPerformed(null);
    }

    /**
     * Change direction based on forwarded action.
     *
     * @param action Action based on which direction is changed.
     */
    public void changeDirection(final Action action) {
        switch (action) {
            case MOVE_UP:
                currentDirection = Direction.UP;
                break;
            case MOVE_RIGHT:
                currentDirection = Direction.RIGHT;
                break;
            case MOVE_DOWN:
                currentDirection = Direction.DOWN;
                break;
            case MOVE_LEFT:
                currentDirection = Direction.LEFT;
                break;
        }
    }

    /**
     * Initializes game world and places the food and player on starting position
     */
    public void initializeGame() {
        snakeLength = 3;
        snakePosition = new Position[900];

        // Set snake on it's default position
        for (int i = 0; i < snakeLength; i++) {
            snakePosition[i] = new Position(50 - i * GameUtils.PLAYER_SIZE, 50);
        }

        // Set food position
        setFoodPosition();

        // Mark that player is in game
        inGame = true;
    }

    /**
     * Used to check if the game is still ongoing.
     *
     * @return Returns true if player is still alive and in the game.
     */
    public boolean isOngoing() {
        return inGame;
    }

    /**
     * Get current game state.
     *
     * @return Returns an object representing current game state.
     */
    public GameState getGameState() {
        return GameStateHelper.createGameState(snakePosition, currentDirection, foodPosition);
    }

    /**
     * Get snake position.
     *
     * @return Returns current snake position.
     */
    public Position[] getSnakePosition() {
        return snakePosition;
    }

    /**
     * Get food position.
     *
     * @return Returns current food position.
     */
    public Position getFoodPosition() {
        return foodPosition;
    }

    /**
     * Get current snake length;
     *
     * @return Returns current snake length.
     */
    public int getSnakeLength() {
        return snakeLength;
    }

    // endregion

    // region Helper
    private void draw(final Graphics graphics) {
        if (!inGame) {
            return; // No need to do anything if the game is not running
        }

        // Draw food
        graphics.drawImage(FOOD_IMAGE, foodPosition.getX(), foodPosition.getY(), this);

        // Draw snake
        for (int i = 0; i < snakeLength; i++) {
            // Position of one of the snake parts (head or tail)
            final Position pos = snakePosition[i];

            // First item is always head
            graphics.drawImage(i == 0 ? HEAD_IMAGE : TAIL_IMAGE, pos.getX(), pos.getY(), this);

            // Synchronize graphics state
            Toolkit.getDefaultToolkit().sync();
        }
    }

    private void setFoodPosition() {
        foodPosition = new Position(
                (int) (Math.random() * 29) * GameUtils.PLAYER_SIZE,
                (int) (Math.random() * 29)  * GameUtils.PLAYER_SIZE
        );
    }

    private boolean isFoodEaten() {
        // Get current position of snakes head
        final Position headPosition = snakePosition[0];

        // Return true if snakes head is on the food position (snake if having a snack)
        return foodPosition.equals(headPosition);
    }

    private void checkIfPlayerHeadIsCollidingWithOtherBodyParts(Position headPosition) {
        for (int i = 1; i < snakePosition.length; i++) {
            /*
            If head position is equal to any other snake body part position that means that snake has just
            tried to eat itself and that the game is over.
             */
            if (headPosition.equals(snakePosition[i])) {
                inGame = false;
                break;
            }
        }
    }
    // endregion
}
