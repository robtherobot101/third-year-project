package seng302.GUI.Controllers.User;

import seng302.GUI.Controllers.User.UserController;
import seng302.GUI.StatusIndicator;
import seng302.GUI.TitleBar;
import seng302.User.User;

import java.util.LinkedList;

public abstract class UserTabController {

    protected StatusIndicator statusIndicator;
    protected TitleBar titleBar;
    protected UserController userController;
    protected LinkedList<User> undoStack = new LinkedList<>(), redoStack = new LinkedList<>();
    protected User currentUser;

    /**
     * Sets the current user being modified
     * @param currentUser The user object being modified
     */
    public void setCurrentUser(User currentUser){
        this.currentUser = currentUser;
    }


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
    public void setParent(UserController parent) {
        userController = parent;
    }

    /**
     * Undoes the previous action performed on the page
     */
    public abstract void undo();

    /**
     * Redoes the previous action performed on the page
     */
    public abstract void redo();

    /**
     * Add an action to the undo stack
     * @param user The user with the old fields
     */
    public void addToUndoStack(User user){
        undoStack.add(new User(user));
        redoStack.clear();
    }

    /**
     * Returns whether the undo stack is empty
     * @return true only if the undo stack contains items
     */
    public boolean undoEmpty(){
        return undoStack.isEmpty();
    }

    /**
     * Returns whether the redo stack is empty
     * @return true only if the redo stack contains items
     */
    public boolean redoEmpty(){
        return redoStack.isEmpty();
    }

}
