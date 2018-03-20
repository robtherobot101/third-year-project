package seng302;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import seng302.Core.Donor;
import seng302.Core.Main;
import seng302.Core.Organ;

import java.util.ArrayList;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

public class UndoRedoTest {

    @Before
    public void setup() {
        Main.donors = new ArrayList<>();
        Main.donors.add(new Donor("Andrew,Neil,Davidson", "01/02/1998", "01/11/4000", "male", 12.1, 50.45, "o+", "Canterbury", "1235 abc Street"));
    }

    @Test
    public void testRemoveFromStack() {
        Donor toSet = Main.donors.get(0);
        Main.addDonorToUndoStack(toSet);
        Main.donorUndo(toSet);
        assertTrue(Main.getDonorUndoStack().isEmpty());
    }

    @Test
    public void testLoadToRedo() {
        Donor toSet = Main.donors.get(0);
        Main.addDonorToUndoStack(toSet);
        Main.donorUndo(toSet);
        assertFalse(Main.getDonorRedoStack().isEmpty());
    }

    @Test
    public void testUndo(){
        Donor originalDonor = Main.donors.get(0);
        Main.addDonorToUndoStack(originalDonor);
        originalDonor.setOrgan(Organ.BONE);
        Donor changedDonor = Main.getDonorUndoStack().get(0);
        assertFalse(originalDonor.getOrgans() == changedDonor.getOrgans());
    }

    @Test
    public void testRedo(){
        Donor originalDonor = Main.donors.get(0);
        Main.addDonorToUndoStack(originalDonor);
        originalDonor.setOrgan(Organ.CORNEA);
        Main.donorUndo(originalDonor);
        originalDonor = Main.donorRedo(originalDonor);
        assertTrue(originalDonor.getOrgans().contains(Organ.CORNEA));
    }

    @After
    public void tearDown(){
        Main.getDonorUndoStack().clear();
    }
}
