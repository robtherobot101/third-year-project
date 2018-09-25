package seng302.Logic.Database;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import seng302.Server;

import java.io.IOException;
import java.sql.SQLException;

public abstract class GenericTest {
    @BeforeClass
    @AfterClass
    public static void reset() throws SQLException, IOException {
        Administration administration = new Administration();
        administration.reset();
        administration.resample();
    }
}
