package io.humanode.humanode.bot;

import com.pengrad.telegrambot.Callback;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import io.humanode.humanode.cache.FileBasedCache;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static io.humanode.humanode.utils.HumanUtils.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class JarvisTelegramBot implements JarvisTelegramBotAPI {
    private static final String CHAT_ID_KEY = "CHAT_ID_KEY";
    private static TelegramBot bot = null;
    private final FileBasedCache fileBasedCache;
    @Value("${bot.token}")
    private String token;

    @PostConstruct
    public void start() {
        bot = new TelegramBot(token);

        bot.setUpdatesListener(updates -> {
            log.info("Received updates from bot {}", updates);
            for (Update u : updates) {
                if (isRegisterCommand(u)) {
                    fileBasedCache.put(CHAT_ID_KEY, u.message().chat().id());
                    sendMessage(SUCCESSFULLY_REGISTERED_MESSAGE);
                } else {
                    sendMessage(UNKNOWN_COMMAND);
                }
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        }, e -> {
            if (e.response() != null) {
                log.error(e.response().toString());
            } else {
                log.error(e.getMessage());
            }
        });

        log.info("Start bot with token: {}", token);
    }


    @Override
    public void sendMessage(String message) {
        if (bot == null) {
            throw new RuntimeException("The telegram bot is not initialized");
        }

        Long id = (Long) fileBasedCache.get(CHAT_ID_KEY);

        if (id == null) {
            log.warn(MISSING_CHAT_ID);
            return;
        }

        SendMessage request = new SendMessage(id, message);

        bot.execute(request, new Callback<SendMessage, SendResponse>() {
            @Override
            public void onResponse(SendMessage request, SendResponse response) {
                log.info("Successfully sent message {}", message);
            }

            @Override
            public void onFailure(SendMessage request, IOException e) {
                log.error("Failed sent message {}, cause {}", message, e.getMessage());
            }
        });
    }

    private boolean isRegisterCommand(Update update) {
        return update.message() != null && update.message().text().equals("/register");
    }
}
