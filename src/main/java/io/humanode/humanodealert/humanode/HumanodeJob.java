package io.humanode.humanodealert.humanode;

import io.humanode.humanodealert.bot.TelegramBot;
import io.humanode.humanodealert.dtos.BioAuthStatusDTO;
import io.humanode.humanodealert.exceptions.HumanodeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class HumanodeJob {
    private static final String bioAuthBody = "{\n" +
            "    \"jsonrpc\": \"2.0\",\n" +
            "    \"id\": 1,\n" +
            "    \"method\": \"bioauth_status\",\n" +
            "    \"params\": []\n" +
            "}";
    private final TelegramBot telegramBot;
    private final WebClient client = WebClient.create("http://localhost:9933");

    @Scheduled(cron = "0 */1 * * * *")
    public void checkHumanodeHealthAndBioAuth() {
        Date expiresAt = new Date(getBioAuthTime());

        Date now = new Date();

        long remaining = expiresAt.getTime() - now.getTime();

        if (remaining <= 0) {
            telegramBot.sendMessage("BioAuth has expired !!!");
        }

        long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(remaining);

        if (diffInMinutes <= 5) {
            telegramBot.sendMessage(String.format("Your BioAuth will expire soon. You have %s minutes left", diffInMinutes));
        }
    }

    @Scheduled(cron = "0 0 */4 * * *")
    public void bioAuthInformation() {
        Date expiresAt = new Date(getBioAuthTime());

        Date now = new Date();

        long remaining = expiresAt.getTime() - now.getTime();

        String hours = String.valueOf(TimeUnit.MILLISECONDS.toHours(remaining));

        String minutes = String.valueOf(TimeUnit.MILLISECONDS.toMinutes(remaining - TimeUnit.MILLISECONDS.toHours(remaining) * 60 * 60 * 1000));

        String remainingTime = (hours.length() == 1 ? "0" + hours : hours) + "h : " + (minutes.length() == 1 ? "0" + minutes : minutes) + "m";

        telegramBot.sendMessage(String.format("Next BioAuth on %s, remaining %s", expiresAt, remainingTime));
    }

    private Long getBioAuthTime() {
        try {
            BioAuthStatusDTO response = client
                    .post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(bioAuthBody)
                    .retrieve()
                    .bodyToMono(BioAuthStatusDTO.class)
                    .block();

            if (response == null || response.getResult() == null) {
                telegramBot.sendMessage("Can't get expire time !!!");
                throw new HumanodeException("Can't get expire time");
            }

            if (response.getResult() instanceof String) {
                telegramBot.sendMessage("BioAuth has expired !!!");
                throw new HumanodeException("BioAuth has expired !!!");
            } else if (response.getResult() instanceof LinkedHashMap<?, ?>) {
                LinkedHashMap<String, LinkedHashMap<String, Long>> result = (LinkedHashMap<String, LinkedHashMap<String, Long>>) response.getResult();

                if (result.size() > 0) {
                    LinkedHashMap<String, Long> bioAuth = result.get("Active");

                    if (bioAuth.size() > 0) {
                        return bioAuth.get("expires_at");
                    }
                }
                throw generateExpireException();
            } else {
                throw generateExpireException();
            }
        } catch (Exception e) {
            if (!(e instanceof HumanodeException)) {
                telegramBot.sendMessage("Humanode is down !!!");
                throw new RuntimeException("Humanode is down", e);
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    private Exception generateExpireException() {
        telegramBot.sendMessage("Can't get expire time !!!");
        throw new HumanodeException("Can't get expire time");
    }
}
