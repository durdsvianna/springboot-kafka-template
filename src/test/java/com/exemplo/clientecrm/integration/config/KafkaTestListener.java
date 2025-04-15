package com.exemplo.clientecrm.integration.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class KafkaTestListener {

    @Autowired
    private ObjectMapper objectMapper;

    @Getter
    private final List<List<?>> mensagensRecebidas = new ArrayList<>();
    private CountDownLatch latch = new CountDownLatch(1);

    @KafkaListener(topics = {"${app.kafka.topic.clientes:CLIENTES}", "${app.kafka.topic.produtos:PRODUTOS}"}, groupId = "test-group")
    public void receberMensagem(List<?> objetos) {
        log.info("Mensagem recebida com {} objetos", objetos.size());
        // Create a new ArrayList to avoid any reference issues
        mensagensRecebidas.add(new ArrayList<>(objetos));
        latch.countDown();
    }

    public void resetar() {
        this.mensagensRecebidas.clear();
        this.latch = new CountDownLatch(1);
    }

    public boolean aguardarMensagens(int segundos) throws InterruptedException {
        return latch.await(segundos, TimeUnit.SECONDS);
    }

    public int getQuantidadeMensagens() {
        return mensagensRecebidas.size();
    }

    public boolean nenhumaMensagemRecebida() {
        return mensagensRecebidas.isEmpty();
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getPrimeiraMensagem() {
        if (mensagensRecebidas.isEmpty()) {
            return List.of();
        }
        return (List<T>) mensagensRecebidas.get(0);
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getSegundaMensagem() {
        if (mensagensRecebidas.size() < 2) {
            return List.of();
        }
        return (List<T>) mensagensRecebidas.get(1);
    }
} 