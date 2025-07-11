package com.example.Kafka_Practice_Task;

import lombok.Data;

@Data
public class WeatherData {

    private int temperature;
    private String condition;
    private String city;
    private String date;
}
