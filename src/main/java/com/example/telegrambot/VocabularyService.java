package com.example.telegrambot;

import com.example.telegrambot.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class VocabularyService {
    private final VocabularyRepository vocabularyRepository;
    private final VocabularyDto vocabulary = new VocabularyDto();


    public String askName() {
        return "Укажите название словаря";
    }

    public String addNameVocabulary(String name) {
        vocabulary.setName(name);
        return "Название добавлено \n Добавьте слова:";
    }

    public Map<String, String> stringToMap(String messageText) {
        String[] words = messageText.split(";");
        Map<String, String> translationMap = new HashMap<>();
        for (String pair : words) {
            String[] pars = pair.split(",");
            translationMap.put(pars[0].trim(), pars[1].trim());
        }
        return translationMap;
    }

    public String addWordsAndSaveVocabulary(Map<String, String> words) {
        vocabulary.setWords(words);
        saveVocabulary(Mapper.toVocabularyMongo(vocabulary));
        return "Слова добавлены";
    }

    public String checkVocabulary() {
        return vocabularyRepository.findAll().toString();
    }

    public void saveVocabulary(Vocabulary vocabularyMongo) {
        vocabularyRepository.save(vocabularyMongo);
    }

    public String sayDefault() {
        return "Кру!";
    }


    public List<String> trainVoca(String name) {
        Map<String, String> trainVoca = vocabularyRepository.findByName(name).orElseThrow(() -> new NotFoundException("Ничего не найдено")).getWords();
        List<String> result = new ArrayList<>();
        for (Map.Entry<String, String> word : trainVoca.entrySet()) {
            result.add(word.getValue());
            result.add("Правильный ответ - " + word.getKey());
        }
        return result;
    }


}
