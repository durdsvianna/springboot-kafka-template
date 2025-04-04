package com.exemplo.clientecrm.integration.config;

import com.exemplo.clientecrm.model.Cliente;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class KafkaTestListener {

    @Getter
    private final List<List<Cliente>> mensagensRecebidas = new ArrayList<>();
    private CountDownLatch latch = new CountDownLatch(1);

    @KafkaListener(topics = "${app.kafka.topic:CLIENTES}", groupId = "test-group")
    public void receberMensagem(List<Cliente> clientes) {
        log.info("Mensagem recebida com {} clientes", clientes.size());
        // Create a new ArrayList to avoid any reference issues
        mensagensRecebidas.add(new ArrayList<>(clientes));
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

    public List<Cliente> getPrimeiraMensagem() {
        if (mensagensRecebidas.isEmpty()) {
            return List.of();
        }
        return mensagensRecebidas.get(0);
    }

    public List<Cliente> getSegundaMensagem() {
        if (mensagensRecebidas.size() < 2) {
            return List.of();
        }
        return mensagensRecebidas.get(1);
    }
} 