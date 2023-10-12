package com.example.telegrambot;

import com.example.telegrambot.exceptions.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

import static com.example.telegrambot.Flag.*;

@Slf4j
@Service
@AllArgsConstructor
public class Bot extends TelegramLongPollingBot {
    private final VocabularyService service;
    private final BotConfig botConfig;
    public static Flag flag;

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
        if (update.hasCallbackQuery()) {
            String messageText;
            chatId = update.getCallbackQuery().getMessage().getChatId();
            log.info("Нажата кнопка {} пользователем {}",
                    update.getCallbackQuery().getData(), update.getCallbackQuery().getMessage().getFrom().getUserName());
            if (update.getCallbackQuery().getData().equals("add_dictionary")) {
                sendMessage(chatId, service.askName());
                flag = ADDNAME;
            }
            if (update.getCallbackQuery().getData().equals("view_dictionaries")) {
                sendMessage(chatId, service.checkVocabulary());
                flag = START;
            }
            if (update.getCallbackQuery().getData().equals("training")) {
                sendMessage(chatId, service.askName());
                flag = TRAINING;
            }
            if (update.getCallbackQuery().getData().equals("delete_dictionaries")) {
                flag = DELETE;
                sendMessage(chatId, service.askName());

            }
        }
        if (update.hasMessage()) {
            chatId = update.getMessage().getChatId();
            log.info("Получено сообщение: {} от {}", update.getMessage(), update.getMessage().getFrom().getUserName());
            if (update.getMessage().getText().equals("Старт")) {
                flag = START;
                sendWelcomeMessage(chatId);
                return;
            }
        }
        if (update.getMessage().hasText()) {
            String messageText;
            switch (flag) {
                case ADDNAME:
                    messageText = update.getMessage().getText();
                    sendMessage(chatId, service.addNameVocabulary(messageText));
                    flag = ADDWORDS;
                    break;
                case ADDWORDS:
                    messageText = update.getMessage().getText();
                    sendMessage(chatId, service.addWordsAndSaveVocabulary(service.stringToMap(messageText)));
                    flag = START;
                    break;
                case TRAINING:
                    messageText = update.getMessage().getText();
                    List<String> wordsForTraining = new ArrayList<>();
                    try {
                        wordsForTraining = service.trainVoca(messageText);
                    } catch (NotFoundException e) {
                        sendMessage(chatId, String.format("Словарь с названием %s не найден! \n напишите /start чтобы начать", messageText));
                        flag = START;
                    }
                    sendMessage(chatId, "Начинаем тренировку словаря с названием " + messageText + ", приготовьтесь!");
                    for (String word : wordsForTraining) {
                        sendMessage(chatId, word);
                        try {
                            Thread.sleep(2500);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                case DELETE:
                    messageText = update.getMessage().getText();
                    sendMessage(chatId, service.deleteVoca(messageText));
                default:
                    startMessage(chatId);
            }

        }
    }


    private InlineKeyboardMarkup createKeyboard() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(InlineKeyboardButton.builder().text("Добавить словарь").callbackData("add_dictionary").build());
        keyboard.add(row1);
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(InlineKeyboardButton.builder().text("Посмотреть словари").callbackData("view_dictionaries").build());
        keyboard.add(row2);
        List<InlineKeyboardButton> row3 = new ArrayList<>();
        row3.add(InlineKeyboardButton.builder().text("Тренировка").callbackData("training").build());
        keyboard.add(row3);
        List<InlineKeyboardButton> row4 = new ArrayList<>();
        row1.add(InlineKeyboardButton.builder().text("Удалить словарь").callbackData("delete_dictionaries").build());
        keyboard.add(row4);
        inlineKeyboardMarkup.setKeyboard(keyboard);
        return inlineKeyboardMarkup;
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

    private void sendWelcomeMessage(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Добро пожаловать! Выберите действие:");

        InlineKeyboardMarkup keyboardMarkup = createKeyboard();
        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }


    }

    private void startMessage(long chatId) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton("Старт"));
        keyboard.add(row);
        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        String text = "Старт";
        message.setText(String.format("Нажмите \"%s\", чтобы начать", text));
        message.setReplyMarkup(keyboardMarkup);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
