package com.example.telegrambot;

public class Mapper {
    public static VocabularyMongo toVocabularyMongo(Vocabulary vocabulary) {
        return VocabularyMongo.builder()
                .name(vocabulary.getName())
                .words(vocabulary.getWords())
                .build();
    }
}
