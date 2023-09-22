package io.humanode.humanode.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.ZoneId;

@Slf4j
@Component
@RequiredArgsConstructor
public class StaticCacheImpl implements StaticCache {
    private final ObjectMapper objectMapper;
    private HumanodeCache humanodeCache;

    @Value("${cache.file.json}")
    private String cachePath;

    @PostConstruct
    public void loadCache() {
        try {
            File file = new File(cachePath);
            humanodeCache = objectMapper.readValue(file, HumanodeCache.class);
        } catch (Exception e) {
            log.error(e.getMessage());
            humanodeCache = new HumanodeCache();
        }
    }

    @Override
    public Long getChatId() {
        return humanodeCache.getChatId();
    }

    @Override
    public void setChatId(Long chatId) {
        humanodeCache.setChatId(chatId);
        persistCache();
    }

    @Override
    public void enable() {
        humanodeCache.setEnableNotification(true);
        persistCache();
    }

    @Override
    public void disable() {
        humanodeCache.setEnableNotification(false);
        persistCache();
    }

    @Override
    public boolean isEnableNotification() {
        return humanodeCache.isEnableNotification();
    }

    @Override
    public ZoneId getTimeZoneId() {
        if (StringUtils.hasText(humanodeCache.getTimeZoneId())) {
            try {
                return ZoneId.of(humanodeCache.getTimeZoneId());
            } catch (Exception e) {
                return ZoneId.systemDefault();
            }
        }
        return ZoneId.systemDefault();
    }

    @Override
    public void setTimeZoneId(String timeZoneId) {
        humanodeCache.setTimeZoneId(timeZoneId);
        persistCache();
    }

    private void persistCache() {
        try (FileWriter file = new FileWriter(cachePath)) {
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            file.write(ow.writeValueAsString(humanodeCache));
            file.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
