package com.exemplo.clientecrm.integration.config;

import com.exemplo.clientecrm.model.Cliente;
import com.exemplo.clientecrm.service.ClienteApiService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@TestConfiguration
public class TestConfig {

    @Bean
    @Primary
    public ClienteApiService mockClienteApiService() {
        return new MockClienteApiService();
    }

    public static class MockClienteApiService extends ClienteApiService {
        private List<Cliente> mockClientes = Collections.emptyList();

        public MockClienteApiService() {
            super(WebClient.builder(), "https://xyz.net/api/v1/clientes");
        }

        @Override
        public Mono<List<Cliente>> obterClientesAlteradosNoDiaAnterior() {
            return Mono.just(mockClientes);
        }

        public void setMockClientes(List<Cliente> clientes) {
            this.mockClientes = clientes;
        }

        public void setMockClientesVazios() {
            this.mockClientes = Collections.emptyList();
        }

        public void setMock10Clientes() {
            this.mockClientes = gerarClientes(10);
        }

        public void setMock28Clientes() {
            this.mockClientes = gerarClientes(28);
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
} 