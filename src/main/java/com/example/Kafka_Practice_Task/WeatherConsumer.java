package com.example.Kafka_Practice_Task;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class WeatherConsumer {

    @KafkaListener(topics = "weather-topic", groupId = "weather-group")
    public void listen(String message) {
        System.out.println("Получено" + message);
    }
}
