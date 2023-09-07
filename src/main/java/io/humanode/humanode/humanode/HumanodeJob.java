package io.humanode.humanode.humanode;

import io.humanode.humanode.bot.JarvisTelegramBotAPI;
import io.humanode.humanode.dtos.BioAuthStatusDTO;
import io.humanode.humanode.exceptions.HumanodeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

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

    private final JarvisTelegramBotAPI jarvisTelegramBotAPI;
    private final HumanodeFeignClient client;

    @Scheduled(cron = "0 */1 * * * *")
    public void checkHumanodeHealthAndBioAuth() {
        log.info("Try to check Humanode health and bio auth");

        Date expiresAt = new Date(getBioAuthTime());

        Date now = new Date();

        long remaining = expiresAt.getTime() - now.getTime();

        if (remaining <= 0) {
            log.info("BioAuth has expired");
            jarvisTelegramBotAPI.sendMessage("BioAuth has expired");
        }

        long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(remaining);

        if (diffInMinutes <= 5) {
            log.info("Your BioAuth will expire soon. You have {} minutes left", diffInMinutes);
            jarvisTelegramBotAPI.sendMessage(String.format("Your BioAuth will expire soon. You have %s minutes left", diffInMinutes));
        }
    }

    @Scheduled(cron = "0 0 */4 * * *")
    public void bioAuthInformation() {

        log.info("Try to get bio auth information");
        Date expiresAt = new Date(getBioAuthTime());

        Date now = new Date();

        long remaining = expiresAt.getTime() - now.getTime();

        String hours = String.valueOf(TimeUnit.MILLISECONDS.toHours(remaining));

        String minutes = String.valueOf(TimeUnit.MILLISECONDS.toMinutes(remaining - TimeUnit.MILLISECONDS.toHours(remaining) * 60 * 60 * 1000));

        String remainingTime = (hours.length() == 1 ? "0" + hours : hours) + "h : " + (minutes.length() == 1 ? "0" + minutes : minutes) + "m";

        jarvisTelegramBotAPI.sendMessage(String.format("Next BioAuth on %s, remaining %s", expiresAt, remainingTime));
    }

    private Long getBioAuthTime() {
        try {
            BioAuthStatusDTO response = client.getBioAuthStatus(bioAuthBody);

            if (response == null || response.getResult() == null) {
                jarvisTelegramBotAPI.sendMessage("Can't get expire time");
                throw new HumanodeException("Can't get expire time");
            }

            if (response.getResult() instanceof String) {
                jarvisTelegramBotAPI.sendMessage("BioAuth has expired");
                throw new HumanodeException("BioAuth has expired !!!");
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
