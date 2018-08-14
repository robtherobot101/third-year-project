package seng302.GUI;

import seng302.Generic.WindowManager;

/**
 * An enum to store constants that refer to each GUI window.
 */
public enum TFScene {
    login("login", 600, 320),
    createAccount("User/createUser", 400, 450),
    clinician("Clinician/Clinician", WindowManager.MAIN_WINDOW_PREF_WIDTH, WindowManager.MAIN_WINDOW_PREF_HEIGHT), //Don't reorder these
    admin("Admin/Admin", WindowManager.MAIN_WINDOW_PREF_WIDTH, WindowManager.MAIN_WINDOW_PREF_HEIGHT),
    userWindow("User/User", WindowManager.MAIN_WINDOW_PREF_WIDTH, WindowManager.MAIN_WINDOW_PREF_HEIGHT);

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
