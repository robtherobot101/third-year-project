package seng302;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import seng302.Core.Donor;
import seng302.Core.Main;

import static org.junit.Assert.assertEquals;

/**
 * Unit test for simple Main.
 */
public class MainTest {
    @Before
    public void setup() {
        Main.donors = new ArrayList<>();
        Main.recalculateNextId();
        Main.donors.add(new Donor("Andrew,Neil,Davidson", "01/02/1998", "01/11/4000", "male", 12.1, 50.45, "o+", "Canterbury", "1235 abc Street"));
        Main.donors.add(new Donor("Test Donor,Testperson", "01/04/1530", "31/01/1565", "other", 1.234, 1.11111, "a-", "Auckland", "street sample text"));
        Main.donors.add(new Donor("Singlename", LocalDate.parse("12/06/1945", Donor.dateFormat)));
        Main.donors.add(new Donor("Donor 2,Person", "01/12/1990", "09/03/2090", "female", 2, 60, "b-", "Sample Region", "Sample Address"));
        Main.donors.add(new Donor("a,long,long,name", "01/11/3000", "01/11/4000", "other", 0.1, 12.4, "b-", "Example region", "Example Address 12345"));
    }

    @Test
    public void testGetById() {
        assertEquals(Main.donors.get(2), Main.getDonorById(2));
        Main.donors.remove(2);
        assertEquals(null, Main.getDonorById(2));
    }

    @Test
    public void testGetByName() {
        assertEquals(2, Main.getDonorByName(new String[]{"Donor","Person"}).size());
        assertEquals(1, Main.getDonorByName(new String[]{"nglenam"}).size());
        assertEquals("Singlename", Main.getDonorByName(new String[]{"nglenam"}).get(0).getName());
        assertEquals(1, Main.getDonorByName(new String[]{"a","on","lo","me"}).size());
        assertEquals(0, Main.getDonorByName(new String[]{"name","long"}).size());
    }

    @Test
    public void testImportSave() {
        Main.donors.add(new Donor("extra", LocalDate.parse("01/01/1000", Donor.dateFormat)));
        Main.saveDonors("testsave");
        Main.donors.remove(5);
        assertEquals(5, Main.donors.size());
        Main.importDonors("testsave");
        assertEquals("extra", Main.donors.get(5).getName());
    }

    @After
    public void tearDown() {
        Main.donors = new ArrayList<>();
    }
}
