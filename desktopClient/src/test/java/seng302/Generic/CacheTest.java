package seng302.Generic;

import org.junit.Test;

import java.time.Duration;

import static java.lang.Thread.sleep;
import static org.junit.Assert.*;

public class CacheTest {

    @Test
    public void purgeEntriesOlderThan_oldEntiresCached_oldEntriesPurged() throws InterruptedException{
        Cache cache = new Cache("");
        cache.put("key","value");
        sleep(20);
        cache.purgeEntriesOlderThan(Duration.ofMillis(20));
        assertFalse(cache.contains("key"));
    }

    @Test
    public void purgeEntriesOlderThan_zeroOldEntriesCached_entriesUnchanged() throws InterruptedException{
        Cache cache = new Cache("");
        cache.put("key","value1");
        cache.purgeEntriesOlderThan(Duration.ofMillis(20));
        assertTrue(cache.contains("key"));
    }

    @Test
    public void testSavingDataToCacheThenImportingAgain() {
        Cache cache = IO.importCache("./doc/examples/testCache.json");
        cache.put("test", "test Data");
        cache.save();
        Cache testCache = IO.importCache("./doc/examples/testCache.json");
        assertEquals("test Data", testCache.get("test"));
        testCache.clear();
        testCache.save();
    }

    @Test
    public void testAddingDataToCache() {
        Cache cache = new Cache("");
        cache.put("test", "test Data");
        assertEquals("test Data", cache.get("test"));

    }
}