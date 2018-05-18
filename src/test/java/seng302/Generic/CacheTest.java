package seng302.Generic;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.*;

class CacheTest {

    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void tearDown() {
    }


    @Test
    public void purgeEntriesOlderThan_oldEntiresCached_oldEntriesPurged() throws InterruptedException{
        Cache cache = new Cache("");
        cache.put("key","value");
        sleep(20);
        cache.purgeEntriesOlderThan(Duration.ofMillis(20));
        assertFalse(cache.hasKey("key"));
    }

    @Test
    public void purgeEntriesOlderThan_zeroOldEntriesCached_entriesUnchanged() throws InterruptedException{
        Cache cache = new Cache("");
        cache.put("key","value1");
        cache.purgeEntriesOlderThan(Duration.ofMillis(20));
        assertTrue(cache.hasKey("key"));
    }
}