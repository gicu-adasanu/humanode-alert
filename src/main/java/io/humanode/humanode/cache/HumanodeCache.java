package io.humanode.humanode.cache;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class HumanodeCache {
    private Long chatId;

    private String timeZoneId;

    @Builder.Default
    private boolean enableNotification = true;
}
