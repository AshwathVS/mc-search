package org.mcsearch.cache;

import java.util.Iterator;
import java.util.LinkedHashMap;

public class LRUCache<K, V> implements ICache<K, V> {

    private LinkedHashMap<K, V> cache;
    private int capacity;

    public LRUCache(int capacity) throws IllegalArgumentException {
        if(capacity <= 0) throw new IllegalArgumentException("Capacity must be > 0");
        this.capacity = capacity;
        this.cache = new LinkedHashMap<>(capacity);
    }

    @Override
    public void put(K key, V value) {
        if(cache.size() == capacity) {
            Iterator<K> it = this.cache.keySet().iterator();
            it.next();
            it.remove();
        }
        cache.put(key, value);
    }

    @Override
    public boolean containsKey(K key) {
        return this.cache.containsKey(key);
    }

    @Override
    public V get(K key) {
        if(containsKey(key)) return this.cache.get(key);
        else return null;
    }

    @Override
    public void removeKey(K key) {
        if(this.cache.containsKey(key)) this.cache.remove(key);
    }
}
