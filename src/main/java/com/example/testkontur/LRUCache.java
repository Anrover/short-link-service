package com.example.testkontur;

import java.util.LinkedHashMap;
import java.util.Map;

public class LRUCache<K, V> {
    private Map<K, V> cacheMap;

    public LRUCache(int size) {
        cacheMap = new LinkedHashMap<K, V>(size, 0.75f, true);
    }

    public synchronized void put(K key, V value) {
        cacheMap.put(key, value);
    }

    public synchronized V get(K key) {
        return cacheMap.get(key);
    }
}
