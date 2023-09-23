package io.humanode.humanode.humanode;

import io.humanode.humanode.bot.JarvisTelegramBotAPI;
import io.humanode.humanode.cache.StaticCache;
import io.humanode.humanode.dtos.BioAuthStatusDTO;
import io.humanode.humanode.exceptions.HumanodeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class HumanodeJob {

    private static final String bioAuthBody = """
            {
                "jsonrpc": "2.0",
                "id": 1,
                "method": "bioauth_status",
                "params": []
            }""";
    private final StaticCache staticCache;
    private final JarvisTelegramBotAPI jarvisTelegramBotAPI;
    private final HumanodeFeignClient client;
    @Value("${humanode.path.auth.cmd}")
    private String authCmd;

    @Scheduled(cron = "0 */1 * * * *")
    public void checkHumanodeHealthAndBioAuth() {
        log.info("Try to check Humanode health and bio auth");

        LocalDateTime expiresAt = LocalDateTime.ofInstant(
                Instant.ofEpochSecond(getBioAuthTime() / 1000), staticCache.getTimeZoneId()
        );

        log.info("Humanode is up and running");

        LocalDateTime now = LocalDateTime.now(staticCache.getTimeZoneId());

        long remaining = now.until(expiresAt, ChronoUnit.MINUTES);

        if (remaining <= 5) {
            log.info("Your BioAuth will expire soon. You have {} minutes left", remaining);
            jarvisTelegramBotAPI.sendMessage(
                    String.format("Your BioAuth will expire soon. You have %s minutes left", remaining)
            );
        }
    }

    @Scheduled(cron = "0 0 12 * * *")
    public void bioAuthInformation() {
        if (!staticCache.isEnableNotification()) {
            return;
        }

        log.info("Try to get bio auth information");
        LocalDateTime expiresAt = LocalDateTime.ofInstant(
                Instant.ofEpochSecond(getBioAuthTime() / 1000), staticCache.getTimeZoneId()
        );

        LocalDateTime now = LocalDateTime.now(staticCache.getTimeZoneId());

        String remainingTime = getString(expiresAt, now);

        jarvisTelegramBotAPI.sendMessage(
                String.format(
                        "Next BioAuth on %s, remaining %s", expiresAt.format(
                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                        ),
                        remainingTime
                )
        );
        log.info("Next BioAuth on {}, remaining {}}", expiresAt, remainingTime);
    }

    private String getAuthUrl() {
        System.out.println(authCmd);
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("/bin/sh", "-c", authCmd);
        System.out.println(authCmd);
        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                log.info(line);
                output.append(line);
            }

            process.waitFor();
            return output.toString();
        } catch (IOException | InterruptedException e) {
            log.error(e.getMessage());
            return "Can't get url for authentication, check logs and path in application.properties";
        }
    }

    @NotNull
    private String getString(LocalDateTime expiresAt, LocalDateTime now) {
        long days = now.until(expiresAt, ChronoUnit.DAYS);
        long hours = now.until(expiresAt.minusDays(days), ChronoUnit.HOURS);
        long min = now.until(expiresAt.minusDays(days).minusHours(hours), ChronoUnit.MINUTES);
        long sec = now.until(expiresAt.minusDays(days).minusHours(hours).minusMinutes(min), ChronoUnit.SECONDS);

        return String.format("%sd and %sh:%sm:%ss", days, hours, min, sec);
    }

    private Long getBioAuthTime() {
        try {
            BioAuthStatusDTO response = client.getBioAuthStatus(bioAuthBody);

            if (response == null || response.getResult() == null) {
                jarvisTelegramBotAPI.sendMessage("Can't get expire time");
                throw new HumanodeException("Can't get expire time");
            }

            if (response.getResult() instanceof String) {
                String url = getAuthUrl();
                log.info("BioAuth has expired. " + url);
                jarvisTelegramBotAPI.sendMessage("BioAuth has expired. " + url);
                throw new HumanodeException("BioAuth has expired. " + url);
            } else {
                if (response.getResult() instanceof LinkedHashMap<?, ?> result) {

                    if (!result.isEmpty() && result.get("Active") instanceof LinkedHashMap<?, ?> bioAuth) {

                        if (!bioAuth.isEmpty()) {
                            return (Long) bioAuth.get("expires_at");
                        }
                    }
                }
                throw generateExpireException();
            }
        } catch (Exception e) {
            if (!(e instanceof HumanodeException)) {
                jarvisTelegramBotAPI.sendMessage("Humanode is down");
                throw new RuntimeException("Humanode is down", e);
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    private Exception generateExpireException() {
        jarvisTelegramBotAPI.sendMessage("Can't get expire time");
        throw new HumanodeException("Can't get expire time");
    }
}
