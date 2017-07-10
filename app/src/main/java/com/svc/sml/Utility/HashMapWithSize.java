package com.svc.sml.Utility;

import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by himanshu on 5/2/16.
 */
public class HashMapWithSize<K, V> implements Map<K, V> {

    private Map<K, V> map;
    int MAX = 35;

    public HashMapWithSize() {
        map = new HashMap<K, V>();
    }
    public HashMapWithSize(int maxSize) {
        MAX = maxSize;
        map = new HashMap<K, V>();
    }

    @Override
    public void clear() {

    }

    @Override
    public boolean containsKey(Object key) {
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        return false;
    }

    @NonNull
    @Override
    public Set<Entry<K, V>> entrySet() {
        return null;
    }

    @Override
    public V get(Object key) {
        return null;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @NonNull
    @Override
    public Set<K> keySet() {
        return null;
    }

    @Override
    public V put(K key, V value) {
        return null;
    }

    public boolean putCheck(K key, V value) {
        if (map.size() >= MAX && !map.containsKey(key)) {
            map.remove(map.keySet().toArray()[0]);
            map.put(key,value);
            return false;
        } else {
            map.put(key,value);
            return true;
        }
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {

    }

    @Override
    public V remove(Object key) {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @NonNull
    @Override
    public Collection<V> values() {
        return null;
    }


}

