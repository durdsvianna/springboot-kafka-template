package com.exemplo.clientecrm.service;

import com.exemplo.clientecrm.model.Cliente;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class IntegracaoClienteService {

    private final ClienteApiService clienteApiService;
    private final KafkaProducerService kafkaProducerService;
    
    @Value("${app.integracao.tamanho-grupo:15}")
    private int tamanhoGrupo;
    
    public void processarClientesDoDiaAnterior() {
        log.info("Iniciando processamento de clientes do dia anterior");
        
        clienteApiService.obterClientesAlteradosNoDiaAnterior()
                .flatMapIterable(clientes -> dividirEmGrupos(clientes, tamanhoGrupo))
                .flatMap(grupo -> {
                    CompletableFuture<Void> future = kafkaProducerService.enviarClientesParaKafka(grupo)
                            .thenAccept(result -> log.info("Grupo de {} clientes enviado com sucesso", grupo.size()));
                    return reactor.core.publisher.Mono.fromFuture(future);
                })
                .doOnComplete(() -> log.info("Processamento de clientes finalizado com sucesso"))
                .doOnError(e -> log.error("Erro ao processar clientes: {}", e.getMessage()))
                .subscribe();
    }
    
    public List<List<Cliente>> dividirEmGrupos(List<Cliente> clientes, int tamanhoGrupo) {
        if (clientes.isEmpty()) {
            log.info("Nenhum cliente encontrado para processar");
            return List.of();
        }
        
        int totalClientes = clientes.size();
        log.info("Dividindo {} clientes em grupos de até {} clientes", totalClientes, tamanhoGrupo);
        
        List<List<Cliente>> grupos = new ArrayList<>();
        
        for (int i = 0; i < totalClientes; i += tamanhoGrupo) {
            int fim = Math.min(i + tamanhoGrupo, totalClientes);
            List<Cliente> grupo = new ArrayList<>(clientes.subList(i, fim));
            grupos.add(grupo);
            log.debug("Criado grupo {} com {} clientes", grupos.size(), grupo.size());
        }
        
        log.info("Criados {} grupos de clientes", grupos.size());
        return grupos;
    }
} 