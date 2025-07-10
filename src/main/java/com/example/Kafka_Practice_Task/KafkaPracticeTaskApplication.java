package com.example.Kafka_Practice_Task;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class KafkaPracticeTaskApplication {

	public static void main(String[] args) {
		SpringApplication.run(KafkaPracticeTaskApplication.class, args);
	}

}
