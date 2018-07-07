package seng302;

import org.junit.Before;
import org.junit.Test;
import seng302.Generic.SqlSanitation;

import static org.junit.Assert.assertEquals;

public class SqlSanitationTest {

    SqlSanitation sqlSanitation;

    @Before
    public void setUp(){
        sqlSanitation = new SqlSanitation();
    }


    @Test
    public void queryContainsDeleteUppercaseTest(){
        String result = sqlSanitation.sanitizeSqlString("DELETE * FROM USER WHERE id = 1");
        assertEquals("You do not have permission to delete from the database.",result);
    }

    @Test
    public void queryContainsDeleteLowercaseTest(){
        String result = sqlSanitation.sanitizeSqlString("delete * from user where id = 1");
        assertEquals("You do not have permission to delete from the database.",result);
    }

    @Test
    public void queryContainsDeleteMixedCaseTest(){
        String result = sqlSanitation.sanitizeSqlString("Delete * From User Where id = 1");
        assertEquals("You do not have permission to delete from the database.",result);
    }

    @Test
    public void queryContainsDropTest(){
        String result = sqlSanitation.sanitizeSqlString("Drop User Where id = 1");
        assertEquals("You do not have permission to drop in the database.",result);
    }

    @Test
    public void queryContainsInsertTest(){
        String result = sqlSanitation.sanitizeSqlString("INSERT INTO Users VALUES (value1, value2, value3); ");
        assertEquals("You do not have permission to insert into the database.",result);
    }

    @Test
    public void queryContainsUpdateTest(){
        String result = sqlSanitation.sanitizeSqlString("UPDATE table_name SET first_name = value1, last_name = value2 WHERE id = 1;");
        assertEquals("You do not have permission to update in the database.",result);
    }

    @Test
    public void queryContainsCreateTest(){
        String result = sqlSanitation.sanitizeSqlString("CREATE TABLE table_name (column1 text, column2 text, column3 text); ");
        assertEquals("You do not have permission to create in the database.",result);
    }

    @Test
    public void queryContainsAlterTest(){
        String result = sqlSanitation.sanitizeSqlString("ALTER TABLE User ADD column_name text; ");
        assertEquals("You do not have permission to alter the database.",result);
    }

    @Test
    public void queryContainsPasswordTest(){
        String result = sqlSanitation.sanitizeSqlString("Select password From User");
        assertEquals("You do not have permission to view the passwords of users in the database.",result);
    }

    @Test
    public void goodQueryTest(){
        String result = sqlSanitation.sanitizeSqlString("select first_name, last_name from User where id = 5");
        assertEquals("",result);
    }

}
