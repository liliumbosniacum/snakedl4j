package com.liliumbosniacum.snakedl4j.network;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Class representing actions which player can take.
 *
 * @author mirza
 */
public enum Action {
    /**
     * Player will move up.
     */
    MOVE_UP,
    /**
     * Player will move right.
     */
    MOVE_RIGHT,
    /**
     * Player will move down.
     */
    MOVE_DOWN,
    /**
     * Player will move left.
     */
    MOVE_LEFT;

    private static final List<Action> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
    private static final int SIZE = VALUES.size();
    private static final Random RANDOM = new Random();

    /**
     * Get random action from all available values.
     *
     * @return Returns one of Action values.
     */
    public static Action getRandomAction() {
        return VALUES.get(RANDOM.nextInt(SIZE));
    }

    /**
     * Gets an action based on provided index.
     *
     * @param index Index based on which action is selected.
     * @return Returns one of Action values.
     */
    public static Action getActionByIndex(final int index) {
        return VALUES.get(index);
    }

    /**
     * Get index of current action.
     *
     * @return Returns index of current action.
     */
    public int getActionIndex() {
        return VALUES.indexOf(this);
    }
}
