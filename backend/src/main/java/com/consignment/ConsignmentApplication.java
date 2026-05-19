package com.consignment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ConsignmentApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConsignmentApplication.class, args);
    }
}
