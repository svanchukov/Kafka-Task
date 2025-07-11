package com.example.Kafka_Practice_Task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class WeatherProducerTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private WeatherProducer weatherProducer;

    @Test
    void testSendWeather() {
        weatherProducer.sendWeather();

        // Проверяем, что kafkaTemplate.send был вызван с правильным топиком и сообщением
        verify(kafkaTemplate).send(eq("weather-topic"), anyString());
    }
}
