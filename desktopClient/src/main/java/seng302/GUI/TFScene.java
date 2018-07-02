package seng302.GUI;

import seng302.Generic.WindowManager;

/**
 * An enum to store constants that refer to each GUI window.
 */
public enum TFScene {
    login("login", 600, 320),
    createAccount("createAccount", 400, 450),
    clinician("clinician", WindowManager.mainWindowPrefWidth, WindowManager.mainWindowPrefHeight), //Don't reorder these
    admin("admin", WindowManager.mainWindowPrefWidth, WindowManager.mainWindowPrefHeight),
    userWindow("userWindow", WindowManager.mainWindowPrefWidth, WindowManager.mainWindowPrefHeight);

    private int width, height;
    private String name;

    TFScene(String name, int width, int height) {
        this.name = name;
        this.width = width;
        this.height = height;
    }

    public String getPath() {
        return "/fxml/" + name + ".fxml";
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
