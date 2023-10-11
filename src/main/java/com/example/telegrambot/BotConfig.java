package com.example.telegrambot;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "telegrambot")
@EnableConfigurationProperties
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BotConfig {
    String botToken;
    String botName;
}
