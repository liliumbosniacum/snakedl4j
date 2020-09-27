package com.liliumbosniacum.snakedl4j.network;

public enum GameMode {
    /**
     * Indicates that training should be started.
     */
    TRAIN,
    /**
     * Indicates that evaluation of existing network should be started.
     */
    EVALUATE;

    public static GameMode create(final String mode) {
        try {
            return GameMode.valueOf(mode);
        } catch (final Exception e) {
            return GameMode.EVALUATE;
        }
    }
}
