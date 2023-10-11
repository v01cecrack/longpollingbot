package com.example.telegrambot;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface VocabularyRepository extends MongoRepository<VocabularyMongo, String> {
    List<VocabularyMongo> findAll();
    VocabularyMongo findByName(String name);

}
