package com.example.telegrambot;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Getter
@Setter
@ToString
@Document
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class Vocabulary {
    @Id
    String name;
    Map<String, String> words;
}
