package com.exemplo.clientecrm.service;

import com.exemplo.clientecrm.model.Cliente;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    @Value("${app.kafka.topic:CLIENTES}")
    private String topic;

    public CompletableFuture<SendResult<String, Object>> enviarClientesParaKafka(List<Cliente> clientes) {
        log.info("Enviando {} clientes para o tópico {}", clientes.size(), topic);
        
        // Create a new ArrayList to avoid sending a sublist or other ArrayList implementation
        ArrayList<Cliente> clientesParaEnviar = new ArrayList<>(clientes);
        
        return kafkaTemplate.send(topic, clientesParaEnviar)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Mensagem enviada com sucesso para o tópico {}: offset=[{}]", 
                                topic, result.getRecordMetadata().offset());
                    } else {
                        log.error("Erro ao enviar mensagem para o tópico " + topic, ex);
                    }
                });
    }
} 