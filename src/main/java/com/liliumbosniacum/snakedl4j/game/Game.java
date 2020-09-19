package com.liliumbosniacum.snakedl4j.game;

import com.liliumbosniacum.snakedl4j.game.helper.Direction;
import com.liliumbosniacum.snakedl4j.game.helper.GameUtils;
import com.liliumbosniacum.snakedl4j.game.helper.Position;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Implementation of a simple snake game with some extra methods needed for the network.
 * Original implementation can be found here https://github.com/janbodnar/Java-Snake-Game
 *
 * @author lilium
 */
public class Game extends JPanel implements ActionListener {
    // region Member
    private static final Logger LOG = LoggerFactory.getLogger(Game.class);

    private static final int GAME_DIMENSIONS = 300; // Dimensions of game world 300x300
    private static final int PLAYER_SIZE = 10;

    private Image food = GameUtils.getFoodImage();
    private Image tail = GameUtils.getTailImage();
    private Image head = GameUtils.getHeadImage();

    // Used to keep track of all snake parts (positions of the tail and head)
    private Position[] snakePosition = new Position[900];

    private boolean inGame = true;
    private Direction currentDirection = Direction.RIGHT;
    private Position foodPosition;
    private int snakeLength;
    // endregion

    // region Setup
    public Game() {
        setBackground(Color.WHITE);
        setFocusable(true);
        setPreferredSize(new Dimension(GAME_DIMENSIONS, GAME_DIMENSIONS));

        initializeGame();
    }
    // endregion

    // region Implementation
    @Override
    public void actionPerformed(ActionEvent e) {
        if (inGame) {
            // checkFood
            // checkCollision
            LOG.info("Action was performed");
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
                snakePosition[0] = new Position(headPosition.getX(), headPosition.getY() - PLAYER_SIZE);
                break;
            case RIGHT:
                snakePosition[0] = new Position(headPosition.getX() + PLAYER_SIZE, headPosition.getY());
                break;
            case DOWN:
                snakePosition[0] = new Position(headPosition.getX(), headPosition.getY() + PLAYER_SIZE);
                break;
            case LEFT:
                snakePosition[0] = new Position(headPosition.getX() - PLAYER_SIZE, headPosition.getY());
                break;
            default:
                LOG.error("Unknown position");
        }

        // As we do not use any key pressed events to move our player we need "manually" notify about performed action
        actionPerformed(null);
    }
    // endregion

    // region Helper
    private void draw(final Graphics graphics) {
        if (!inGame) {
            return; // No need to do anything if the game is not running
        }

        // Draw food
        graphics.drawImage(food, foodPosition.getX(), foodPosition.getY(), this);

        // Draw snake
        for (int i = 0; i < snakeLength; i++) {
            // Position of one of the snake parts (head or tail)
            final Position pos = snakePosition[i];

            // First item is always head
            graphics.drawImage(i == 0 ? head : tail, pos.getX(), pos.getY(), this);

            // Synchronize graphics state
            Toolkit.getDefaultToolkit().sync();
        }
    }

    public void initializeGame() {
        snakeLength = 3;

        // Set snake on it's default position
        for (int i = 0; i < snakeLength; i++) {
            snakePosition[i] = new Position(50 - i * PLAYER_SIZE, 50);
        }

        // Set food position
        setFoodPosition();
    }

    private void setFoodPosition() {
        foodPosition = new Position(
                (int) (Math.random() * 29) * PLAYER_SIZE,
                (int) (Math.random() * 29)  * PLAYER_SIZE
        );
    }
    // endregion
}
