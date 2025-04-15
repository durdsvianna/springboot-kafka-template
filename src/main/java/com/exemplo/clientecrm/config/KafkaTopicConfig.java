package com.exemplo.clientecrm.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${app.kafka.topic.clientes:CLIENTES}")
    private String clientesTopic;
    
    @Value("${app.kafka.topic.produtos:PRODUTOS}")
    private String produtosTopic;
    
    @Bean
    public NewTopic clienteTopic() {
        return TopicBuilder.name(clientesTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }
    
    @Bean
    public NewTopic produtoTopic() {
        return TopicBuilder.name(produtosTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }
} 