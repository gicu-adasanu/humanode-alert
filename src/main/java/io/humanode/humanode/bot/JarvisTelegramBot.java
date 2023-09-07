package io.humanode.humanode.bot;

import com.pengrad.telegrambot.Callback;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class JarvisTelegramBot implements JarvisTelegramBotAPI {
    @Value("${bot.token}")
    private String token;

    @Value("${chat.id}")
    private String chatId;

    private static TelegramBot bot = null;

    @PostConstruct
    public void start() {
        bot = new TelegramBot(token);

        bot.setUpdatesListener(updates -> UpdatesListener.CONFIRMED_UPDATES_ALL, e -> {
            log.error(e.getMessage());
        });

        log.info("Start bot with token: {}", token);
    }


    @Override
    public void sendMessage(String message) {
        if (bot == null) {
            throw new RuntimeException("The telegram bot is not initialized");
        }

        SendMessage request = new SendMessage(chatId, message);

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
}
