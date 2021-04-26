package org.mcsearch.cache;

import java.util.HashMap;
import java.util.LinkedHashSet;

public class LFUCache<K, V> implements ICache<K, V> {
    HashMap<K, V> cache;
    HashMap<Integer, LinkedHashSet<K>> countVsKeyListMap;
    HashMap<K, Integer> countMap;

    int cap;
    int min = -1;

    public LFUCache(int capacity) {
        cap = capacity;
        cache = new HashMap<>();
        countMap = new HashMap<>();
        countVsKeyListMap = new HashMap<>();
        countVsKeyListMap.put(1, new LinkedHashSet<>());
    }

    @Override
    public V get(K key) {
        if (!cache.containsKey(key))
            return null;

        int count = countMap.get(key);
        countMap.put(key, count + 1);
        countVsKeyListMap.get(count).remove(key);

        if (count == min && countVsKeyListMap.get(count).size() == 0)
            min++;
        if (!countVsKeyListMap.containsKey(count + 1))
            countVsKeyListMap.put(count + 1, new LinkedHashSet<>());
        countVsKeyListMap.get(count + 1).add(key);

        return cache.get(key);
    }

    @Override
    public void put(K key, V value) {
        if (cap <= 0)
            return;

        if (cache.containsKey(key)) {
            cache.put(key, value);
            get(key);
            return;
        }

        if (cache.size() >= cap) {
            K evict = countVsKeyListMap.get(min).iterator().next();
            countVsKeyListMap.get(min).remove(evict);
            cache.remove(evict);
            countMap.remove(evict);
        }

        cache.put(key, value);
        countMap.put(key, 1);
        min = 1;
        countVsKeyListMap.get(1).add(key);
    }

    @Override
    public boolean containsKey(K key) {
        return this.cache.containsKey(key);
    }

    public static void main(String[] args) {
        LFUCache<Integer, Integer> cache = new LFUCache<>(2);
        cache.put(10, 20);
        cache.put(20, 30);

        System.out.println(cache.get(10));
        System.out.println(cache.get(10));

        cache.put(30, 40);
        System.out.println(cache.get(20));
    }
}
