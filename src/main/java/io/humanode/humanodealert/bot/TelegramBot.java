package io.humanode.humanodealert.bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.annotation.PostConstruct;
import java.io.*;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot implements TelegramBotAPI {

    @Value("${chat.id}")
    private String chatId;

    @Value("${bot.token}")
    private String token;

    @Override
    public String getBotUsername() {
        return "Humanode Bot";
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.getMessage().getText().equals("/register")) {
            String id =update.getChatMember().getChat().getId().toString();
            chatId = id;
            updateChatId(id);
        }
    }

    @PostConstruct
    public void start() throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(new TelegramBot());
        log.info("Start bot with token: {}", token);
    }


    @Override
    public void sendMessage(String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void updateChatId(String id) {
        File file = new File("/ser/humanode/application.properties");

        if (file.exists()) {
            StringBuilder oldContent = new StringBuilder();

            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));

                String line = reader.readLine();

                while (line != null) {
                    oldContent.append(line).append(System.lineSeparator());
                    line = reader.readLine();
                }

                String newContent = oldContent.toString().replaceAll("chat.id=\\w*", "chat.id=".concat(id));

                FileWriter writer = new FileWriter(file);
                writer.write(newContent);

                reader.close();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
