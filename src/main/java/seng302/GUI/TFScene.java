package seng302.GUI;

import seng302.Generic.Main;

/**
 * An enum to store constants that refer to each GUI window.
 */
public enum TFScene {
    login(400, 280),
    createAccount(400, 450),
    clinician(Main.mainWindowPrefWidth, Main.mainWindowPrefHeight),
    userWindow(Main.mainWindowPrefWidth, Main.mainWindowPrefHeight),
    transplantList(Main.mainWindowPrefWidth, Main.mainWindowPrefHeight);

    private int width, height;

    TFScene(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
