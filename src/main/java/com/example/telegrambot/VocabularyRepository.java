package com.example.telegrambot;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VocabularyRepository extends MongoRepository<Vocabulary, String> {
    List<Vocabulary> findAll();
    Optional<Vocabulary> findByName(String name);

}
