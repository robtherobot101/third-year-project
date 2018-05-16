package seng302.GUI.Controllers;

import seng302.GUI.StatusIndicator;
import seng302.GUI.TitleBar;

public abstract class PageController {

    protected StatusIndicator statusIndicator;
    protected TitleBar titleBar;
    protected UserWindowController userWindowController;

    /**
     * Set the status indicator object from the user window the page is being displayed in
     *
     * @param statusIndicator the statusIndicator object
     */
    public void setStatusIndicator(StatusIndicator statusIndicator) {
        this.statusIndicator = statusIndicator;
    }

    /**
     * Assign the title bar of the window
     *
     * @param titleBar The title bar of the pane in which this pane is located
     */
    public void setTitleBar(TitleBar titleBar) {
        this.titleBar = titleBar;
    }

    /**
     * Sets up a reference to the parent user window controller for this controller.
     *
     * @param parent The user window controller that is the parent of this controller
     */
    public void setParent(UserWindowController parent) {
        userWindowController = parent;
    }

}
