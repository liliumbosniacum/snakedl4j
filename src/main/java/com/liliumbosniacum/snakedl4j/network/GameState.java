package com.liliumbosniacum.snakedl4j.network;

import java.util.Arrays;

/**
 * Class representing current game state.
 *
 * @author mirza
 */
public class GameState {
    private Boolean[] states;

    public GameState(final Boolean[] states) {
        this.states = states;
    }

    public Boolean[] getStates() {
        return states;
    }

    @Override
    public String toString() {
        return "GameState{states=" + Arrays.toString(states) + '}';
    }

    /**
     * Builds game state string based on current values.
     * @return Returns e.g. from [false, true, false] -> 010.
     */
    public String getGameStateString() {
        final StringBuilder builder = new StringBuilder();
        for (final Boolean state : states) {
            builder.append(state ? '1' : '0');
        }
        return builder.toString();
    }
}
