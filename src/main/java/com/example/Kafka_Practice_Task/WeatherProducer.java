package com.example.Kafka_Practice_Task;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@RequiredArgsConstructor
public class WeatherProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final Random random = new Random();
    private final String[] conditions = {"солнечно", "облачно", "дождь"};

    @Scheduled(fixedRate = 2000)
    public void sendWeather() {
        int temperature = random.nextInt(36);
        final String[] cities = {"Москва", "Питер", "Казань", "Новосибирск"};
        String condition = conditions[random.nextInt(conditions.length)];
        String city = cities[random.nextInt(cities.length)];
        String message = String.format("{\"temperature\": %d, \"condition\": \"%s\", \"city\": \"%s\"}", temperature, condition, city);

        kafkaTemplate.send("weather-topic", message);
        System.out.println("Отправлено" + message);
    }
}
