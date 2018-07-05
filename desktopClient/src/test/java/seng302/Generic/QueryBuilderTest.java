package seng302.Generic;

import org.junit.Test;

import static org.junit.Assert.*;

public class QueryBuilderTest {

    @Test
    public void test_toString() {
        QueryBuilder qb = new QueryBuilder();
        qb.addParameter("keyA","valueA");
        qb.addParameter("keyB","valueB");
        assertTrue(qb.toString().equals("keyA=valueA&keyB=valueB") || qb.toString().equals("keyB=valueB&keyA=valueA"));
    }
}