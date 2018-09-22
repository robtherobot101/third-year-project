package seng302.Logic.Database;

import org.junit.Before;
import org.junit.Test;
import seng302.Model.Attribute.Organ;
import seng302.Model.DonatableOrgan;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OrgansDatabaseTest {

    private OrgansDatabase organsDatabase = new OrgansDatabase();


    @Before
    public void reset() throws SQLException, IOException {
        Administration administration = new Administration();
        administration.reset();
        administration.resample();
    }

    @Test
    public void insertOrganTest() throws SQLException{
        DonatableOrgan donatableOrgan = new DonatableOrgan(LocalDateTime.now().plusHours(4), Organ.EAR, 3,false, false);
        organsDatabase.insertOrgan(donatableOrgan);

        List<DonatableOrgan> organs = organsDatabase.getAllDonatableOrgans();
        assertEquals(organs.get(0).getDonorId(), donatableOrgan.getDonorId());
        assertEquals(organs.get(0).getOrganType(), donatableOrgan.getOrganType());
        assertEquals(organs.get(0).getTimeOfDeath(), donatableOrgan.getTimeOfDeath().minusNanos(donatableOrgan.getTimeOfDeath().getNano()));
    }

    @Test
    public void updateOrganTest() throws SQLException{
        DonatableOrgan donatableOrgan = new DonatableOrgan(LocalDateTime.now().plusHours(4), Organ.BONE, 3,1, false, false);
        organsDatabase.insertOrgan(donatableOrgan);

        donatableOrgan.setTimeOfDeath(LocalDateTime.now().plusHours(5));
        organsDatabase.updateOrgan(donatableOrgan);
        List<DonatableOrgan> organs = organsDatabase.getAllDonatableOrgans();

        assertEquals(organs.get(0).getDonorId(), donatableOrgan.getDonorId());
        assertEquals(organs.get(0).getOrganType(), donatableOrgan.getOrganType());
        assertEquals(organs.get(0).getTimeOfDeath(), donatableOrgan.getTimeOfDeath().minusNanos(donatableOrgan.getTimeOfDeath().getNano()));
    }

    @Test
    public void removeOrganTest() throws SQLException{
        DonatableOrgan donatableOrgan = new DonatableOrgan(LocalDateTime.now().plusHours(4), Organ.SKIN, 3, 1,false, false);
        organsDatabase.insertOrgan(donatableOrgan);

        List<DonatableOrgan> organs = organsDatabase.getAllDonatableOrgans();
        assertEquals(organs.get(0).getDonorId(), donatableOrgan.getDonorId());
        assertEquals(organs.get(0).getOrganType(), donatableOrgan.getOrganType());
        assertEquals(organs.get(0).getTimeOfDeath(), donatableOrgan.getTimeOfDeath().minusNanos(donatableOrgan.getTimeOfDeath().getNano()));

        organsDatabase.removeOrgan(organs.get(0));
        organs = organsDatabase.getAllDonatableOrgans();

        assertTrue(organs.isEmpty());
    }
}