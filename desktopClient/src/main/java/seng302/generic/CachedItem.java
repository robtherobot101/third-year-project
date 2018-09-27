package seng302.generic;

import java.time.Duration;
import java.time.LocalDateTime;

public class CachedItem {
    private String value;
    private LocalDateTime createdAt;

    /**
     * class constructor for a new cache item
     * @param value String the value of the cache to store
     * @param createdAt LocalDateTime the time of creation
     */
    CachedItem(String value, LocalDateTime createdAt){
        this.value = value;
        this.createdAt = createdAt;
    }

    /**
     * Returns the duration between the creation time of the item and the current time.
     *
     * @return The duration
     */
    public Duration age(){
        return Duration.between(createdAt, LocalDateTime.now());
    }

    /**
     * Returns the value
     *
     * @return The value
     */
    public String getValue(){
        return value;
    }
}
