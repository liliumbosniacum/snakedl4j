package com.liliumbosniacum.snakedl4j.game.helper;

import java.util.Objects;

/**
 * Class used to wrap coordinates of a game object (e.g. player or food)
 *
 * @author mirza
 */
public class Position {
    private int x;
    private int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    /**
     * Checks if position is located outside the bounds.
     *
     * @return Returns true if position is outside the bounds.
     */
    public boolean isOutsideTheGameBounds() {
        return x > GameUtils.GAME_DIMENSIONS || y > GameUtils.GAME_DIMENSIONS || x < 0 || y < 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Position position = (Position) o;
        return x == position.x && y == position.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "Position{x=" + x + ", y=" + y + '}';
    }
}
