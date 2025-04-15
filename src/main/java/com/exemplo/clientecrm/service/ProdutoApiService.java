package com.exemplo.clientecrm.service;

import com.exemplo.clientecrm.model.Produto;
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
public class ProdutoApiService {

    private final WebClient webClient;
    private final String produtoApiUrl;

    public ProdutoApiService(WebClient.Builder webClientBuilder, 
                       @Value("${api.produtos.url:https://xyz.net/api/v1/produtos}") String produtoApiUrl) {
        this.produtoApiUrl = produtoApiUrl;
        this.webClient = webClientBuilder.baseUrl(produtoApiUrl).build();
    }

    public Mono<List<Produto>> obterProdutosAlteradosNoDiaAnterior() {
        LocalDate ontem = LocalDate.now().minusDays(1);
        String dataFormatada = ontem.format(DateTimeFormatter.ISO_DATE);
        
        log.info("Buscando produtos alterados em: {}", dataFormatada);
        
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("dataAlteracao", dataFormatada)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Produto>>() {})
                .doOnNext(produtos -> log.info("Foram encontrados {} produtos alterados no dia {}", produtos.size(), dataFormatada))
                .doOnError(error -> log.error("Erro ao buscar produtos: {}", error.getMessage()));
    }
} 