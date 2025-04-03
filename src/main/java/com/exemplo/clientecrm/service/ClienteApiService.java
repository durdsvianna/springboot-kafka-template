package com.exemplo.clientecrm.service;

import com.exemplo.clientecrm.model.Cliente;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
public class ClienteApiService {

    private final WebClient webClient;
    private final String clienteApiUrl;

    public ClienteApiService(WebClient.Builder webClientBuilder, 
                       @Value("${api.clientes.url:https://xyz.net/api/v1/clientes}") String clienteApiUrl) {
        this.clienteApiUrl = clienteApiUrl;
        this.webClient = webClientBuilder.baseUrl(clienteApiUrl).build();
    }

    public Mono<List<Cliente>> obterClientesAlteradosNoDiaAnterior() {
        LocalDate ontem = LocalDate.now().minusDays(1);
        String dataFormatada = ontem.format(DateTimeFormatter.ISO_DATE);
        
        log.info("Buscando clientes alterados em: {}", dataFormatada);
        
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("dataAlteracao", dataFormatada)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Cliente>>() {})
                .doOnNext(clientes -> log.info("Foram encontrados {} clientes alterados no dia {}", clientes.size(), dataFormatada))
                .doOnError(error -> log.error("Erro ao buscar clientes: {}", error.getMessage()));
    }
} 