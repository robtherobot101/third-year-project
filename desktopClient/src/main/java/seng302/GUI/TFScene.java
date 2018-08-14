package seng302.GUI;

import seng302.generic.WindowManager;

/**
 * An enum to store constants that refer to each GUI window.
 */
public enum TFScene {
    login("login", 600, 320),
    createAccount("user/createUser", 400, 450),
    clinician("clinician/clinician", WindowManager.MAIN_WINDOW_PREF_WIDTH, WindowManager.MAIN_WINDOW_PREF_HEIGHT), //Don't reorder these
    admin("admin/admin", WindowManager.MAIN_WINDOW_PREF_WIDTH, WindowManager.MAIN_WINDOW_PREF_HEIGHT),
    userWindow("user/user", WindowManager.MAIN_WINDOW_PREF_WIDTH, WindowManager.MAIN_WINDOW_PREF_HEIGHT);

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
