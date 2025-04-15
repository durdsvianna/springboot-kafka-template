package com.exemplo.clientecrm.service;

import com.exemplo.clientecrm.model.Cliente;
import com.exemplo.clientecrm.model.Produto;
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
    
    @Value("${app.kafka.topic.clientes:CLIENTES}")
    private String clientesTopic;
    
    @Value("${app.kafka.topic.produtos:PRODUTOS}")
    private String produtosTopic;

    public CompletableFuture<SendResult<String, Object>> enviarClientesParaKafka(List<Cliente> clientes) {
        log.info("Enviando {} clientes para o tópico {}", clientes.size(), clientesTopic);
        
        // Create a new ArrayList to avoid sending a sublist or other ArrayList implementation
        ArrayList<Cliente> clientesParaEnviar = new ArrayList<>(clientes);
        
        return kafkaTemplate.send(clientesTopic, clientesParaEnviar)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Mensagem enviada com sucesso para o tópico {}: offset=[{}]", 
                                clientesTopic, result.getRecordMetadata().offset());
                    } else {
                        log.error("Erro ao enviar mensagem para o tópico " + clientesTopic, ex);
                    }
                });
    }
    
    public CompletableFuture<SendResult<String, Object>> enviarProdutosParaKafka(List<Produto> produtos) {
        log.info("Enviando {} produtos para o tópico {}", produtos.size(), produtosTopic);
        
        // Create a new ArrayList to avoid sending a sublist or other ArrayList implementation
        ArrayList<Produto> produtosParaEnviar = new ArrayList<>(produtos);
        
        return kafkaTemplate.send(produtosTopic, produtosParaEnviar)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Mensagem enviada com sucesso para o tópico {}: offset=[{}]", 
                                produtosTopic, result.getRecordMetadata().offset());
                    } else {
                        log.error("Erro ao enviar mensagem para o tópico " + produtosTopic, ex);
                    }
                });
    }
} 