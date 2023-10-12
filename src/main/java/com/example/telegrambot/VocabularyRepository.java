package com.example.telegrambot;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VocabularyRepository extends MongoRepository<Vocabulary, String> {
    Optional<Vocabulary> findByName(String name);

}
