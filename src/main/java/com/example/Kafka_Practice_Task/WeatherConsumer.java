package com.example.Kafka_Practice_Task;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class WeatherConsumer {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Map<String, Integer> rainyDaysCount = new HashMap<>();
    private final Map<String, Integer> maxTemperatureCity = new HashMap<>();
    private final Map<String, Integer> minTemperatureCity = new HashMap<>();

    @KafkaListener(topics = "weather-topic", groupId = "weather-group")
    public void listen(String message) {
        try {
            WeatherData data = objectMapper.readValue(message, WeatherData.class);

            System.out.println("Получено: " + message);

            String city = data.getCity();
            int temp = data.getTemperature();
            String condition = data.getCondition();

            rainyDaysCount.putIfAbsent(city, 0);
            // Подсчёт дождливых дней
            if (condition.equals("дождь")) {
                rainyDaysCount.merge(city, 1, Integer::sum);
            }

            maxTemperatureCity.merge(city, temp, Math::max);
            minTemperatureCity.merge(city, temp, Math::min);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    // Метод для вывода аналитики
    @Scheduled(fixedRate = 5000)
    public void printAnalytics() {
        System.out.println("Аналитика погоды: ");

        String rainiestCity = null;
        int maxRainDays = 0;
        String hottestCity = null;
        int maxTemperature = Integer.MIN_VALUE;
        String coldestCity = null;
        int coldestTemperature = Integer.MAX_VALUE;

        for (var entry : rainyDaysCount.entrySet()) {
            String city = entry.getKey();
            int rainDays = entry.getValue();
            int maxTemp = maxTemperatureCity.getOrDefault(city, Integer.MIN_VALUE);
            int minTemp = minTemperatureCity.getOrDefault(city, Integer.MAX_VALUE);

            System.out.println("Дождливых дней в: " + city + " - " + rainDays);

            // Поиск самого дождливого города
            if (rainDays > maxRainDays) {
                maxRainDays = rainDays;
                rainiestCity = city;
            }

            // Поиск самой высокой температуры
            if (maxTemp > maxTemperature) {
                maxTemperature = maxTemp;
                hottestCity = city;
            }

            // Поиск самой низкой температуры
            if (minTemp < coldestTemperature) {
                coldestTemperature = minTemp;
                coldestCity = city;
            }
        }

        // Выводим аналитику
        System.out.println("Самый дождливый город: " + rainiestCity + " с " + maxRainDays + " дождливыми днями.");
        System.out.println("Самая высокая температура: " + maxTemperature + " в городе: " + hottestCity);
        System.out.println("Самая низкая температура: " + coldestTemperature + " в городе: " + coldestCity);
    }


    public Map<String, Integer> getRainyDaysCount() {
        return rainyDaysCount; // Возвращаем реальную карту
    }

    public Map<String, Integer> getMaxTemperatureCity() {
        return maxTemperatureCity; // Возвращаем реальную карту
    }

    public Map<String, Integer> getMinTemperatureCity() {
        return minTemperatureCity; // Возвращаем реальную карту
    }
}
