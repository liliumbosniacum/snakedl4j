package com.liliumbosniacum.snakedl4j.game.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

/**
 * Util class containing methods used to ease our handling of the game.
 *
 * @author lilium
 */
public final class GameUtils {

    // region Constructor
    private GameUtils() {}
    // endregion

    // region Implementation
    public static Image getFoodImage() {
        // return new ImageIcon("D:\\Development\\snakedl4j\\src\\main\\resources\\images\\food.png").getImage();
        return new ImageIcon("src/main/resources/images/food.png").getImage();
    }

    public static Image getHeadImage() {
        return new ImageIcon("src/main/resources/images/head.png").getImage();
    }

    public static Image getTailImage() {
        return new ImageIcon("src/main/resources/images/tail.png").getImage();
    }
    // endregion
}
