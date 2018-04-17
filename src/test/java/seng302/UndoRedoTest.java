package seng302;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import seng302.Controllers.UserWindowController;
import seng302.Core.Donor;
import seng302.Core.Main;
import seng302.Core.Organ;

import java.util.ArrayList;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNotSame;
import static junit.framework.TestCase.assertTrue;

public class UndoRedoTest {
    private UserWindowController userWindowController;

    @Before
    public void setup() {
        Main.donors = new ArrayList<>();
        Main.donors.add(new Donor("Andrew,Neil,Davidson", "01/02/1998", "01/11/4000", "male", 12.1, 50.45, "o+", "Canterbury", "1235 abc Street"));
        userWindowController = new UserWindowController();
    }

    /*
    @Test
    public void testRemoveFromStack() {
        Donor toSet = Main.donors.get(0);
        userWindowController.addDonorToUndoStack(toSet);
        userWindowController.undo();
        //userWindowController.donorUndo(toSet);
        assertTrue(userWindowController.getDonorUndoStack().isEmpty());
    }

    @Test
    public void testLoadToRedo() {
        Donor toSet = Main.donors.get(0);
        userWindowController.addDonorToUndoStack(toSet);
        userWindowController.undo();
        //userWindowController.donorUndo(toSet);
        assertFalse(userWindowController.getDonorRedoStack().isEmpty());
    }*/

    @Test
    public void testUndo() {
        Donor originalDonor = Main.donors.get(0);
        userWindowController.addDonorToUndoStack(originalDonor);
        originalDonor.setOrgan(Organ.BONE);
        Donor changedDonor = userWindowController.getDonorUndoStack().get(0);
        assertNotSame(originalDonor.getOrgans(), changedDonor.getOrgans());
    }

    /*
    @Test
    public void testRedo() {
        Donor originalDonor = Main.donors.get(0);
        userWindowController.addDonorToUndoStack(originalDonor);
        originalDonor.setOrgan(Organ.CORNEA);
        userWindowController.undo();
        userWindowController.redo();
        //userWindowController.donorUndo(originalDonor);
        //originalDonor = userWindowController.donorRedo(originalDonor);
        Donor newDonor = userWindowController.getDonorUndoStack().get(0);
        assertTrue(newDonor.getOrgans().contains(Organ.CORNEA));
    }

    @After
    public void tearDown(){
        userWindowController.getDonorUndoStack().clear();
    }*/
}
