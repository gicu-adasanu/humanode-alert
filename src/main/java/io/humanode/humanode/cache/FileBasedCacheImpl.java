package io.humanode.humanode.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class FileBasedCacheImpl implements FileBasedCache {
    @Value("${cache.file}")
    private String CACHE_FILE;

    @Override
    public void put(String key, Object value) {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(CACHE_FILE))) {
            Map<String, Object> cache = loadCache();
            cache.put(key, value);
            outputStream.writeObject(cache);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public Object get(String key) {
        Map<String, Object> cache = loadCache();
        return cache.get(key);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> loadCache() {
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(CACHE_FILE))) {
            return (Map<String, Object>) inputStream.readObject();
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            return new HashMap<>();
        }
    }
}
