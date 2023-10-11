package com.example.telegrambot;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
@Slf4j
@Service
@AllArgsConstructor
public class Bot extends TelegramLongPollingBot {
    private final BisinessLogic bisinessLogic;
    private final BotConfig botConfig;
    public static String flag;

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getBotToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        long chatId = 0;
//        startMessage(chatId);
        if (update.hasMessage()) {
            chatId = update.getMessage().getChatId();
            log.info("Получено сообщение: {} от {}",update.getMessage() ,update.getMessage().getFrom().getUserName());
            if (update.getMessage().getText().equals("/start")) {
                startMessage(chatId);
                return;
            }
            if (update.getMessage().getText().equals("Добавить словарь")) {
                flag = "Добавить словарь";
            }
            if (update.getMessage().getText().equals("Тренировка")) {
                flag = "Тренировка";
            }
            if (update.getMessage().getText().equals("Посмотреть словари")) {
                flag = "Посмотреть словари";
            }

        }

        if (update.getMessage().hasText()) {
            String messageText;
            switch (flag) {
                case "1":
                    messageText = update.getMessage().getText();
                    sendMessage(chatId,bisinessLogic.addNameVocabulary(messageText));
                    flag = "2";
                    break;
                case "2":
                    messageText = update.getMessage().getText();
                    sendMessage(chatId, bisinessLogic.addWordsAndSaveVocabulary(bisinessLogic.stringToMap(messageText)));
                    flag = "0";
                    break;
                case "Добавить словарь":
                    sendMessage(chatId,bisinessLogic.askName());
                    flag = "1";
                    break;
                case "Посмотреть словари":
                    sendMessage(chatId, bisinessLogic.checkVocabulary());
                    flag = "0";
                    break;
                case "Тренировка":
                    sendMessage(chatId, bisinessLogic.askName());
                    for (String word: bisinessLogic.trainVoca(update.getMessage().getText())) {
                        sendMessage(chatId, word);
                        try {
                            Thread.sleep(2500);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    break;
                default:
                    sendMessage(chatId, bisinessLogic.sayDefault());
            }

        }
    }

    private void startMessage(long chatId) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton("Добавить словарь"));
        row.add(new KeyboardButton("Посмотреть словари"));
        row.add(new KeyboardButton("Тренировка"));
        keyboard.add(row);
        keyboardMarkup.setKeyboard(keyboard);

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Добро пожаловать! Выберите действие:");
        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(long chatId, String text) {
        SendMessage message = SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text(text)
                .build();

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}
