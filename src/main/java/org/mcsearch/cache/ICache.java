package org.mcsearch.cache;

public interface ICache<K, V> {

    void put(K key, V value);

    boolean containsKey(K key);

    V get(K key);

    void removeKey(K key);
}
