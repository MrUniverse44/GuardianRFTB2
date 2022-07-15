package me.blueslime.guardianrftb.multiarena.storage.players;

import java.util.*;

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

    public List<V> getValues() {
        return new ArrayList<>(map.values());
    }

    @SuppressWarnings("unused")
    public List<K> getKeys() {
        return new ArrayList<>(map.keySet());
    }

    public Map<K, V> toMap() {
        return map;
    }

    public void clear() {
        map.clear();
    }

    public static <K, V> PluginStorage<K, V> initAsHash() {
        return new PluginStorage<>(new HashMap<>());
    }

    public static <K extends Enum<K>, V> PluginStorage<K, V> initAsEnum(Class<K> kClass) {
        return new PluginStorage<>(new EnumMap<>(kClass));
    }

}
