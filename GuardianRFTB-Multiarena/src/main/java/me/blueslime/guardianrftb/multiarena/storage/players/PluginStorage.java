package me.blueslime.guardianrftb.multiarena.storage.players;

import java.util.Map;

public class PluginStorage<K, V> {
    private final Map<K, V> map;

    public PluginStorage(Map<K, V> initialMap) {
        this.map = initialMap;
    }

    public void add(K key, V value) {
        map.computeIfAbsent(key, F -> value);
    }

    public void remove(K key) {
        map.remove(key);
    }

    public V get(K key) {
        return map.get(key);
    }

    public V get(K key, V value) {
        add(key, value);
        return map.get(key);
    }

    public void clear() {
        map.clear();
    }

}
