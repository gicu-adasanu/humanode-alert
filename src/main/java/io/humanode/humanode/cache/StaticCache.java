package io.humanode.humanode.cache;

import java.time.ZoneId;

public interface StaticCache {
    Long getChatId();

    void setChatId(Long chatId);

    void enable();

    void disable();

    boolean isEnableNotification();

    ZoneId getTimeZoneId();

    void setTimeZoneId(String timeZoneId);
}
