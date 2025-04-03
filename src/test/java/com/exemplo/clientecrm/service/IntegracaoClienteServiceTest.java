package com.exemplo.clientecrm.service;

import com.exemplo.clientecrm.model.Cliente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IntegracaoClienteServiceTest {

    @Mock
    private ClienteApiService clienteApiService;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @InjectMocks
    private IntegracaoClienteService integracaoClienteService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(integracaoClienteService, "tamanhoGrupo", 15);
    }

    @Test
    void dividirEmGrupos_listaVazia_retornaListaVazia() {
        List<List<Cliente>> grupos = integracaoClienteService.dividirEmGrupos(Collections.emptyList(), 15);
        assertTrue(grupos.isEmpty());
    }

    @Test
    void dividirEmGrupos_listaMenorQueTamanhoGrupo_retornaUmGrupo() {
        List<Cliente> clientes = gerarClientes(10);
        List<List<Cliente>> grupos = integracaoClienteService.dividirEmGrupos(clientes, 15);
        
        assertEquals(1, grupos.size());
        assertEquals(10, grupos.get(0).size());
    }

    @Test
    void dividirEmGrupos_listaMaiorQueTamanhoGrupo_retornaDoisGrupos() {
        List<Cliente> clientes = gerarClientes(28);
        List<List<Cliente>> grupos = integracaoClienteService.dividirEmGrupos(clientes, 15);
        
        assertEquals(2, grupos.size());
        assertEquals(15, grupos.get(0).size());
        assertEquals(13, grupos.get(1).size());
    }

    @Test
    void processarClientesDoDiaAnterior_semClientes_naoEnviaMensagem() {
        when(clienteApiService.obterClientesAlteradosNoDiaAnterior()).thenReturn(Mono.just(Collections.emptyList()));
        
        integracaoClienteService.processarClientesDoDiaAnterior();
        
        verify(kafkaProducerService, never()).enviarClientesParaKafka(any());
    }

    @Test
    void processarClientesDoDiaAnterior_com10Clientes_enviaUmaMensagem() {
        List<Cliente> clientes = gerarClientes(10);
        when(clienteApiService.obterClientesAlteradosNoDiaAnterior()).thenReturn(Mono.just(clientes));
        when(kafkaProducerService.enviarClientesParaKafka(any())).thenReturn(CompletableFuture.completedFuture(mock(SendResult.class)));
        
        integracaoClienteService.processarClientesDoDiaAnterior();
        
        verify(kafkaProducerService, times(1)).enviarClientesParaKafka(any());
    }

    @Test
    void processarClientesDoDiaAnterior_com28Clientes_enviaDuasMensagens() {
        List<Cliente> clientes = gerarClientes(28);
        when(clienteApiService.obterClientesAlteradosNoDiaAnterior()).thenReturn(Mono.just(clientes));
        when(kafkaProducerService.enviarClientesParaKafka(any())).thenReturn(CompletableFuture.completedFuture(mock(SendResult.class)));
        
        integracaoClienteService.processarClientesDoDiaAnterior();
        
        verify(kafkaProducerService, times(2)).enviarClientesParaKafka(any());
    }

    private List<Cliente> gerarClientes(int quantidade) {
        List<Cliente> clientes = new ArrayList<>();
        LocalDateTime agora = LocalDateTime.now();
        
        for (int i = 1; i <= quantidade; i++) {
            Cliente cliente = new Cliente(
                    (long) i, 
                    "Cliente " + i,
                    "cliente" + i + "@exemplo.com", 
                    "(99) 9999-" + String.format("%04d", i),
                    "CPF" + String.format("%011d", i), 
                    agora.minusDays(1), 
                    agora.minusDays(1));
            clientes.add(cliente);
        }
        
        return clientes;
    }
} 