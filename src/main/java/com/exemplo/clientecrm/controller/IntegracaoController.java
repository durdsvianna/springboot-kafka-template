package com.exemplo.clientecrm.controller;

import com.exemplo.clientecrm.service.IntegracaoClienteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/integracao")
@Slf4j
@RequiredArgsConstructor
public class IntegracaoController {

    private final IntegracaoClienteService integracaoClienteService;

    @PostMapping("/clientes")
    public ResponseEntity<String> executarIntegracao() {
        log.info("Recebida solicitação para integração manual de clientes");
        integracaoClienteService.processarClientesDoDiaAnterior();
        return ResponseEntity.accepted().body("Integração de clientes iniciada com sucesso");
    }
} 