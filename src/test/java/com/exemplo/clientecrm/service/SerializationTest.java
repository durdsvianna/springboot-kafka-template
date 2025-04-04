package com.exemplo.clientecrm.service;

import com.exemplo.clientecrm.model.Cliente;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {"TEST-SERIALIZATION"})
@ActiveProfiles("test")
public class SerializationTest {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Test
    void testSerialization() {
        // Create test client
        Cliente cliente = new Cliente(
            1L, 
            "Test Cliente", 
            "test@example.com", 
            "123-456-7890", 
            "12345678901", 
            LocalDateTime.now(), 
            LocalDateTime.now()
        );
        
        List<Cliente> clientes = List.of(cliente);
        
        // Verify serialization doesn't throw an exception
        assertDoesNotThrow(() -> {
            kafkaTemplate.send("TEST-SERIALIZATION", clientes).get();
            System.out.println("Successfully serialized and sent message to Kafka");
        }, "Failed to serialize Cliente object");
    }
} 