package io.humanode.humanode.cache;

public interface FileBasedCache {
    void put(String key, Object value);

    Object get(String key);
}
