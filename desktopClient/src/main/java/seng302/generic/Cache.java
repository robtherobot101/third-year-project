package seng302.generic;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class Cache {
    private String filepath;
    private Map<String, CachedItem> cacheMap;


    public Cache(String filepath) {
        this.filepath = filepath;
        this.cacheMap = new HashMap<>();
    }

    /**
     * Returns the file path where the cache will be saved.
     *
     * @return The file path
     */
    public String getFilePath(){
        return filepath;
    }

    /**
     * Adds an item to the cache. All items are referenced by a key
     *
     * @param key The key of the item to add
     * @param value The value of the item to add
     */
    public void put(String key, String value){
        CachedItem item = new CachedItem(value, LocalDateTime.now());
        cacheMap.put(key, item);
    }

    /**
     * Saves the cache in the JSON format at the filepath
     */
    public void save(){
        IO.saveCache(this);
    }

    /**
     * Returns the value corresponding to the given key
     *
     * @param key The given key
     * @return The value corresponding to the given key
     */
    public String get(String key) {
        return cacheMap.get(key).getValue();
    }


    /**
     * Removes all cache entries which have durations greater than or equal to the given duration
     *
     * @param duration The given duration
     */
    public void purgeEntriesOlderThan(Duration duration){
        for(Map.Entry<String, CachedItem> entry : cacheMap.entrySet()){
            if(entry.getValue().age().toNanos() >= duration.toNanos()){ // Not sure the best way to do this. Comparing by days would just be less generic and accurate
                cacheMap.remove(entry.getKey());
            }
        }
    }

    /**
     * Returns whether or not a given key is in the map
     *
     * @param key The given key
     * @return true if the key is in the map otherwise false
     */
    public boolean contains(String key){
        return cacheMap.containsKey(key);
    }

    /**
     * Removes all items from the cache
     */
    public void clear(){
        cacheMap.clear();
    }


}
