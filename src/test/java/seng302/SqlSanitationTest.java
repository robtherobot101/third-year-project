package seng302;

import org.junit.Test;
import seng302.TUI.SqlSanitation;

import static org.junit.Assert.assertEquals;

public class SqlSanitationTest {

    @Test
    public void queryContainsDeleteUppercaseTest(){
        String result = SqlSanitation.sanitizeSqlString("DELETE * FROM USER WHERE id = 1");
        assertEquals("You do not have permission to delete from the database.",result);
    }

    @Test
    public void queryContainsDeleteLowercaseTest(){
        String result = SqlSanitation.sanitizeSqlString("delete * from user where id = 1");
        assertEquals("You do not have permission to delete from the database.",result);
    }

    @Test
    public void queryContainsDeleteMixedCaseTest(){
        String result = SqlSanitation.sanitizeSqlString("Delete * From User Where id = 1");
        assertEquals("You do not have permission to delete from the database.",result);
    }

    @Test
    public void queryContainsDropTest(){
        String result = SqlSanitation.sanitizeSqlString("Drop User Where id = 1");
        assertEquals("You do not have permission to drop in the database.",result);
    }

    @Test
    public void queryContainsInsertTest(){
        String result = SqlSanitation.sanitizeSqlString("INSERT INTO Users VALUES (value1, value2, value3); ");
        assertEquals("You do not have permission to insert into the database.",result);
    }

    @Test
    public void queryContainsUpdateTest(){
        String result = SqlSanitation.sanitizeSqlString("UPDATE table_name SET first_name = value1, last_name = value2 WHERE id = 1;");
        assertEquals("You do not have permission to update in the database.",result);
    }

    @Test
    public void queryContainsCreateTest(){
        String result = SqlSanitation.sanitizeSqlString("CREATE TABLE table_name (column1 text, column2 text, column3 text); ");
        assertEquals("You do not have permission to create in the database.",result);
    }

    @Test
    public void queryContainsAlterTest(){
        String result = SqlSanitation.sanitizeSqlString("ALTER TABLE User ADD column_name text; ");
        assertEquals("You do not have permission to alter the database.",result);
    }

    @Test
    public void queryContainsPasswordTest(){
        String result = SqlSanitation.sanitizeSqlString("Select password From User");
        assertEquals("You do not have permission to view the passwords of users in the database.",result);
    }

    @Test
    public void goodQueryTest(){
        String result = SqlSanitation.sanitizeSqlString("select first_name, last_name from User where id = 5");
        assertEquals("",result);
    }

}
