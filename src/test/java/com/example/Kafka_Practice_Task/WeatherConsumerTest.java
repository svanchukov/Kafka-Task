package com.example.Kafka_Practice_Task;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class WeatherConsumerTest {

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private WeatherConsumer weatherConsumer;

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Перенаправляем System.out для проверки вывода
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    void tearDown() {
        // Восстанавливаем System.out
        System.setOut(originalOut);
    }

    @Test
    void testListenWithRainyCondition() throws Exception {
        // Подготовка тестовых данных
        String message = "{\"temperature\": 20, \"condition\": \"дождь\", \"city\": \"Москва\"}";
        WeatherData weatherData = new WeatherData();
        weatherData.setCity("Москва");
        weatherData.setTemperature(20);
        weatherData.setCondition("дождь");

        // Настройка мока ObjectMapper
        when(objectMapper.readValue(message, WeatherData.class)).thenReturn(weatherData);

        // Вызов метода listen
        weatherConsumer.listen(message);

        // Проверка карт через геттеры
        assertEquals(1, weatherConsumer.getRainyDaysCount().get("Москва"), "Дождливый день должен быть учтён");
        assertEquals(20, weatherConsumer.getMaxTemperatureCity().get("Москва"), "Максимальная температура должна быть 20");
        assertEquals(20, weatherConsumer.getMinTemperatureCity().get("Москва"), "Минимальная температура должна быть 20");
    }

    @Test
    void testListenWithNonRainyCondition() throws Exception {
        // Подготовка тестовых данных
        String message = "{\"temperature\": 25, \"condition\": \"облачно\", \"city\": \"Питер\"}";
        WeatherData weatherData = new WeatherData();
        weatherData.setCity("Питер");
        weatherData.setTemperature(25);
        weatherData.setCondition("облачно");

        // Настройка мока ObjectMapper
        when(objectMapper.readValue(message, WeatherData.class)).thenReturn(weatherData);

        // Вызов метода listen
        weatherConsumer.listen(message);

        // Проверка карт через геттеры
        assertEquals(0, weatherConsumer.getRainyDaysCount().get("Питер"), "Для не-дождливой погоды счётчик должен быть 0");
        assertEquals(25, weatherConsumer.getMaxTemperatureCity().get("Питер"), "Максимальная температура должна быть 25");
        assertEquals(25, weatherConsumer.getMinTemperatureCity().get("Питер"), "Минимальная температура должна быть 25");
    }

    @Test
    void testPrintAnalytics() throws Exception {
        // Подготовка данных через вызов listen
        String message1 = "{\"temperature\": 30, \"condition\": \"дождь\", \"city\": \"Москва\"}";
        String message2 = "{\"temperature\": 10, \"condition\": \"дождь\", \"city\": \"Москва\"}";
        String message3 = "{\"temperature\": 25, \"condition\": \"облачно\", \"city\": \"Питер\"}";
        String message4 = "{\"temperature\": 15, \"condition\": \"дождь\", \"city\": \"Питер\"}";

        WeatherData data1 = new WeatherData();
        data1.setCity("Москва");
        data1.setTemperature(30);
        data1.setCondition("дождь");

        WeatherData data2 = new WeatherData();
        data2.setCity("Москва");
        data2.setTemperature(10);
        data2.setCondition("дождь");

        WeatherData data3 = new WeatherData();
        data3.setCity("Питер");
        data3.setTemperature(25);
        data3.setCondition("облачно");

        WeatherData data4 = new WeatherData();
        data4.setCity("Питер");
        data4.setTemperature(15);
        data4.setCondition("дождь");

        // Настройка мока ObjectMapper
        when(objectMapper.readValue(message1, WeatherData.class)).thenReturn(data1);
        when(objectMapper.readValue(message2, WeatherData.class)).thenReturn(data2);
        when(objectMapper.readValue(message3, WeatherData.class)).thenReturn(data3);
        when(objectMapper.readValue(message4, WeatherData.class)).thenReturn(data4);

        // Вызов метода listen для заполнения карт
        weatherConsumer.listen(message1);
        weatherConsumer.listen(message2);
        weatherConsumer.listen(message3);
        weatherConsumer.listen(message4);

        // Проверка состояния карт
        assertEquals(2, weatherConsumer.getRainyDaysCount().get("Москва"), "Москва должна иметь 2 дождливых дня");
        assertEquals(1, weatherConsumer.getRainyDaysCount().get("Питер"), "Питер должен иметь 1 дождливый день");
        assertEquals(30, weatherConsumer.getMaxTemperatureCity().get("Москва"), "Максимальная температура в Москве должна быть 30");
        assertEquals(25, weatherConsumer.getMaxTemperatureCity().get("Питер"), "Максимальная температура в Питере должна быть 25");
        assertEquals(10, weatherConsumer.getMinTemperatureCity().get("Москва"), "Минимальная температура в Москве должна быть 10");
        assertEquals(15, weatherConsumer.getMinTemperatureCity().get("Питер"), "Минимальная температура в Питере должна быть 15");

        // Вызов метода printAnalytics
        weatherConsumer.printAnalytics();

        // Проверка вывода в консоль
        String output = outputStream.toString();
        assertTrue(output.contains("Аналитика погоды:"));
        assertTrue(output.contains("Дождливых дней в: Москва - 2"));
        assertTrue(output.contains("Дождливых дней в: Питер - 1"));
        assertTrue(output.contains("Самый дождливый город: Москва с 2 дождливыми днями."));
        assertTrue(output.contains("Самая высокая температура: 30 в городе: Москва"));
        assertTrue(output.contains("Самая низкая температура: 10 в городе: Москва"));
    }
}