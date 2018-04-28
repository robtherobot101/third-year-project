package seng302;

import org.junit.Before;
import org.junit.Test;
import seng302.GUI.Controllers.UserWindowController;
import seng302.User.User;
import seng302.Generic.Main;
import seng302.User.Attribute.Organ;

import java.util.ArrayList;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNotSame;
import static junit.framework.TestCase.assertTrue;

public class UndoRedoTest {
    private UserWindowController userWindowController;

    @Before
    public void setup() {
        Main.users = new ArrayList<>();
        Main.users.add(new User("Andrew,Neil,Davidson", "01/02/1998", "01/11/4000", "male", 12.1, 50.45, "o+", "Canterbury", "1235 abc Street"));
        userWindowController = new UserWindowController();
    }

    /*
    @Test
    public void testRemoveFromStack() {
        User toSet = Main.users.get(0);
        userWindowController.addUserToUndoStack(toSet);
        userWindowController.undo();
        //userWindowController.userUndo(toSet);
        assertTrue(userWindowController.getUserUndoStack().isEmpty());
    }

    @Test
    public void testLoadToRedo() {
        User toSet = Main.users.get(0);
        userWindowController.addUserToUndoStack(toSet);
        userWindowController.undo();
        //userWindowController.userUndo(toSet);
        assertFalse(userWindowController.getUserRedoStack().isEmpty());
    }*/

    @Test
    public void testUndo() {
        User originalUser = Main.users.get(0);
        userWindowController.addUserToUndoStack(originalUser);
        originalUser.setOrgan(Organ.BONE);
        User changedUser = userWindowController.getUserUndoStack().get(0);
        assertNotSame(originalUser.getOrgans(), changedUser.getOrgans());
    }

    /*
    @Test
    public void testRedo() {
        User originalUser = Main.users.get(0);
        userWindowController.addUserToUndoStack(originalUser);
        originalUser.setOrgan(Organ.CORNEA);
        userWindowController.undo();
        userWindowController.redo();
        //userWindowController.userUndo(originalUser);
        //originalUser = userWindowController.userRedo(originalUser);
        User newUser = userWindowController.getUserUndoStack().get(0);
        assertTrue(newUser.getOrgans().contains(Organ.CORNEA));
    }

    @After
    public void tearDown(){
        userWindowController.getUserUndoStack().clear();
    }*/
}
