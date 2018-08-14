package seng302.GUI.Controllers.User;

import java.util.LinkedList;
import seng302.GUI.StatusIndicator;
import seng302.GUI.TitleBar;
import seng302.User.User;

public abstract class UserTabController {

    protected StatusIndicator statusIndicator;
    protected TitleBar titleBar;
    protected UserController userController;
    protected LinkedList<User> undoStack = new LinkedList<>(), redoStack = new LinkedList<>();
    protected User currentUser;

    /**
     * Set the status indicator object from the User window the page is being displayed in
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
     * Sets up a reference to the parent User window controller for this controller.
     *
     * @param parent The User window controller that is the parent of this controller
     */
    public void setParent(UserController parent) {
        userController = parent;
    }

    /**
     * Undoes the previous action performed on the page
     */
    protected abstract void undo();

    /**
     * Redoes the previous action performed on the page
     */
    protected abstract void redo();

    /**
     * Add an action to the undo stack
     * @param user The User with the old fields
     */
    protected void addToUndoStack(User user){
        undoStack.add(new User(user));
        redoStack.clear();
    }

    /**
     * Returns whether the undo stack is empty
     * @return true only if the undo stack contains items
     */
    protected boolean undoEmpty(){
        return undoStack.isEmpty();
    }

    /**
     * Returns whether the redo stack is empty
     * @return true only if the redo stack contains items
     */
    protected boolean redoEmpty(){
        return redoStack.isEmpty();
    }

}
