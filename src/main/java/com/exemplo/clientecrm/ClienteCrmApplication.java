package com.exemplo.clientecrm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ClienteCrmApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClienteCrmApplication.class, args);
    }
} 