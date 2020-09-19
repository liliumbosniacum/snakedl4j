package com.liliumbosniacum.snakedl4j.game.helper;

/**
 * Class used to wrap coordinates of a game object (e.g. player or food)
 *
 * @author lilium
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
}
