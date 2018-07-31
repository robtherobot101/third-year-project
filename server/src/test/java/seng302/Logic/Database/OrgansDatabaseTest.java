package seng302.Logic.Database;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import seng302.Model.Attribute.Organ;
import seng302.Model.DonatableOrgan;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OrgansDatabaseTest {

    private OrgansDatabase organsDatabase = new OrgansDatabase();


    @Before
    public void reset() throws SQLException {
        Administration administration = new Administration();
        administration.reset();
        administration.resample();
    }

    @Test
    public void insertOrganTest() throws SQLException{
        DonatableOrgan donatableOrgan = new DonatableOrgan(LocalDateTime.now().plusHours(4), Organ.EAR, 1);
        organsDatabase.insertOrgan(donatableOrgan);

        List<DonatableOrgan> organs = organsDatabase.getAllDonatableOrgans();
        assertEquals(organs.get(0).getDonorId(), donatableOrgan.getDonorId());
        assertEquals(organs.get(0).getOrganType(), donatableOrgan.getOrganType());
        assertEquals(organs.get(0).getTimeOfExpiry(), donatableOrgan.getTimeOfExpiry());
    }

    @Test
    public void updateOrganTest() throws SQLException{
        DonatableOrgan donatableOrgan = new DonatableOrgan(LocalDateTime.now().plusHours(4), Organ.BONE, 1);
        organsDatabase.insertOrgan(donatableOrgan);

        donatableOrgan.setTimeOfExpiry(LocalDateTime.now().plusHours(5));
        organsDatabase.updateOrgan(donatableOrgan);
        List<DonatableOrgan> organs = organsDatabase.getAllDonatableOrgans();

        assertEquals(organs.get(0).getDonorId(), donatableOrgan.getDonorId());
        assertEquals(organs.get(0).getOrganType(), donatableOrgan.getOrganType());
        assertEquals(organs.get(0).getTimeOfExpiry(), donatableOrgan.getTimeOfExpiry());
    }

    @Test
    public void removeOrganTest() throws SQLException{
        DonatableOrgan donatableOrgan = new DonatableOrgan(LocalDateTime.now().plusHours(4), Organ.SKIN, 1);
        organsDatabase.insertOrgan(donatableOrgan);

        List<DonatableOrgan> organs = organsDatabase.getAllDonatableOrgans();
        assertEquals(organs.get(0).getDonorId(), donatableOrgan.getDonorId());
        assertEquals(organs.get(0).getOrganType(), donatableOrgan.getOrganType());
        assertEquals(organs.get(0).getTimeOfExpiry(), donatableOrgan.getTimeOfExpiry());

        organsDatabase.removeOrgan(donatableOrgan);
        organs = organsDatabase.getAllDonatableOrgans();

        assertTrue(organs.isEmpty());
    }


}
