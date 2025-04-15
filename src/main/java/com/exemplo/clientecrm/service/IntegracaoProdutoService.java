package com.exemplo.clientecrm.service;

import com.exemplo.clientecrm.model.Produto;
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
public class IntegracaoProdutoService {

    private final ProdutoApiService produtoApiService;
    private final KafkaProducerService kafkaProducerService;
    
    @Value("${app.integracao.tamanho-grupo:15}")
    private int tamanhoGrupo;
    
    public void processarProdutosDoDiaAnterior() {
        log.info("Iniciando processamento de produtos do dia anterior");
        
        produtoApiService.obterProdutosAlteradosNoDiaAnterior()
                .flatMapIterable(produtos -> dividirEmGrupos(produtos, tamanhoGrupo))
                .flatMap(grupo -> {
                    CompletableFuture<Void> future = kafkaProducerService.enviarProdutosParaKafka(grupo)
                            .thenAccept(result -> log.info("Grupo de {} produtos enviado com sucesso", grupo.size()));
                    return reactor.core.publisher.Mono.fromFuture(future);
                })
                .doOnComplete(() -> log.info("Processamento de produtos finalizado com sucesso"))
                .doOnError(e -> log.error("Erro ao processar produtos: {}", e.getMessage()))
                .subscribe();
    }
    
    public List<List<Produto>> dividirEmGrupos(List<Produto> produtos, int tamanhoGrupo) {
        if (produtos.isEmpty()) {
            log.info("Nenhum produto encontrado para processar");
            return List.of();
        }
        
        int totalProdutos = produtos.size();
        log.info("Dividindo {} produtos em grupos de até {} produtos", totalProdutos, tamanhoGrupo);
        
        List<List<Produto>> grupos = new ArrayList<>();
        
        for (int i = 0; i < totalProdutos; i += tamanhoGrupo) {
            int fim = Math.min(i + tamanhoGrupo, totalProdutos);
            List<Produto> grupo = new ArrayList<>(produtos.subList(i, fim));
            grupos.add(grupo);
            log.debug("Criado grupo {} com {} produtos", grupos.size(), grupo.size());
        }
        
        log.info("Criados {} grupos de produtos", grupos.size());
        return grupos;
    }
} 