package com.exemplo.clientecrm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AgendadorIntegracaoService {

    private final IntegracaoClienteService integracaoClienteService;
    private final IntegracaoProdutoService integracaoProdutoService;

    // Executa todos os dias às 01:00
    @Scheduled(cron = "0 0 1 * * ?")
    public void executarIntegracao() {
        log.info("Iniciando integração agendada de clientes");
        integracaoClienteService.processarClientesDoDiaAnterior();
    }
    
    // Executa todos os dias às 02:00
    @Scheduled(cron = "0 0 2 * * ?")
    public void executarIntegracaoProdutos() {
        log.info("Iniciando integração agendada de produtos");
        integracaoProdutoService.processarProdutosDoDiaAnterior();
    }
} 