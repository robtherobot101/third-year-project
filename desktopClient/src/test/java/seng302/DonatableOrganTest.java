package seng302;

import org.junit.Before;
import org.junit.Test;
import seng302.User.Attribute.Organ;
import seng302.User.DonatableOrgan;

import java.time.Duration;
import java.time.LocalDateTime;

import static java.time.temporal.ChronoUnit.SECONDS;
import static junit.framework.TestCase.assertEquals;

public class DonatableOrganTest {
    private DonatableOrgan test1, test2;

    @Before
    public void setup() {
        test1 = new DonatableOrgan(LocalDateTime.now(), Organ.BONE, 1, false);
        test2 = new DonatableOrgan(LocalDateTime.now(), Organ.INTESTINE, 4, 1, false);
        test2.setReceiverDeathRegion("Canterbury");
    }

    @Test
    public void testTimeLeft() {
        Duration expDuration = test1.getExpiryDuration(test1.getOrganType());
        test1.setTimeLeft(expDuration);
        assertEquals(expDuration, test1.getTimeLeft());
    }

    @Test
    public void testTick() {
        Duration expDuration = test2.getExpiryDuration(test2.getOrganType());
        test2.setTimeLeft(expDuration);
        test2.tickTimeLeft();
        assertEquals(expDuration.minus(1, SECONDS), test2.getTimeLeft());
    }
}
