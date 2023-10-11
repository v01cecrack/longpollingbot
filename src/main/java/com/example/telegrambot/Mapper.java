package com.example.telegrambot;

public class Mapper {
    public static Vocabulary toVocabularyMongo(VocabularyDto vocabulary) {
        return Vocabulary.builder()
                .name(vocabulary.getName())
                .words(vocabulary.getWords())
                .build();
    }
}
