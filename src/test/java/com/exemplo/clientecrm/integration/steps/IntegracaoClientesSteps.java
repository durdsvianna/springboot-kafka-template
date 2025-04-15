package com.exemplo.clientecrm.integration.steps;

import com.exemplo.clientecrm.ClienteCrmApplication;
import com.exemplo.clientecrm.integration.config.KafkaTestListener;
import com.exemplo.clientecrm.integration.config.TestConfig;
import com.exemplo.clientecrm.model.Cliente;
import com.exemplo.clientecrm.service.IntegracaoClienteService;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.concurrent.TimeUnit;

@CucumberContextConfiguration
@SpringBootTest(classes = ClienteCrmApplication.class)
@ContextConfiguration(classes = TestConfig.class)
@EmbeddedKafka(partitions = 1, topics = {"CLIENTES", "PRODUTOS"})
@ActiveProfiles("test")
public class IntegracaoClientesSteps {

    @Autowired
    private IntegracaoClienteService integracaoClienteService;

    @Autowired
    private TestConfig.MockClienteApiService mockClienteApiService;

    @Autowired
    private KafkaTestListener kafkaTestListener;

    @Before
    public void setup() {
        kafkaTestListener.resetar();
    }

    @After
    public void cleanup() {
        kafkaTestListener.resetar();
    }

    @Given("que o microsserviço de integração de clientes está em execução")
    public void queMicrosservicoIntegracaoClientesEstaEmExecucao() {
        Assertions.assertNotNull(integracaoClienteService);
    }

    @Given("a API de clientes retorna uma lista de {int} clientes criados ou alterados no dia anterior")
    public void apiClientesRetornaListaClientes(int quantidadeClientes) {
        if (quantidadeClientes == 10) {
            mockClienteApiService.setMock10Clientes();
        } else if (quantidadeClientes == 28) {
            mockClienteApiService.setMock28Clientes();
        } else {
            // Por padrão, cria uma lista vazia para outros casos
            mockClienteApiService.setMockClientesVazios();
        }
    }

    @Given("a API de clientes retorna uma lista vazia de clientes criados ou alterados no dia anterior")
    public void apiClientesRetornaListaVaziaClientes() {
        mockClienteApiService.setMockClientesVazios();
    }

    @When("o microsserviço processa a lista de clientes")
    public void microsservicoProcessaListaClientes() throws InterruptedException {
        integracaoClienteService.processarClientesDoDiaAnterior();
        // Aguarda um tempo para processamento
        TimeUnit.SECONDS.sleep(2);
    }

    @Then("uma mensagem contendo a lista de {int} clientes é publicada no tópico {string} do Kafka")
    public void mensagemContendoListaClientesPublicadaTopico(int quantidadeClientes, String topico) throws InterruptedException {
        boolean mensagensRecebidas = kafkaTestListener.aguardarMensagens(5);
        Assertions.assertTrue(mensagensRecebidas, "As mensagens não foram recebidas no tempo esperado");
        
        Assertions.assertEquals(1, kafkaTestListener.getQuantidadeMensagens());
        
        List<Cliente> clientesNaMensagem = kafkaTestListener.getPrimeiraMensagem();
        Assertions.assertEquals(quantidadeClientes, clientesNaMensagem.size());
    }

    @Then("{int} mensagens são publicadas no tópico {string} do Kafka")
    public void mensagensSaoPublicadasNoTopico(int quantidadeMensagens, String topico) throws InterruptedException {
        boolean mensagensRecebidas = kafkaTestListener.aguardarMensagens(5);
        Assertions.assertTrue(mensagensRecebidas, "As mensagens não foram recebidas no tempo esperado");
        
        Assertions.assertEquals(quantidadeMensagens, kafkaTestListener.getQuantidadeMensagens());
    }

    @Then("a primeira mensagem contém um grupo de até {int} clientes")
    public void primeiraMensagemContemGrupoClientes(int tamanhoMaximoGrupo) {
        List<Cliente> primeiroGrupo = kafkaTestListener.getPrimeiraMensagem();
        Assertions.assertFalse(primeiroGrupo.isEmpty(), "O primeiro grupo não deveria estar vazio");
        Assertions.assertTrue(primeiroGrupo.size() <= tamanhoMaximoGrupo, 
                "O primeiro grupo deveria ter no máximo " + tamanhoMaximoGrupo + " clientes");
    }

    @Then("a segunda mensagem contém os {int} clientes restantes")
    public void segundaMensagemContemClientesRestantes(int quantidadeRestante) {
        List<Cliente> segundoGrupo = kafkaTestListener.getSegundaMensagem();
        Assertions.assertFalse(segundoGrupo.isEmpty(), "O segundo grupo não deveria estar vazio");
        Assertions.assertEquals(quantidadeRestante, segundoGrupo.size(), 
                "O segundo grupo deveria ter " + quantidadeRestante + " clientes");
    }

    @Then("nenhuma mensagem é publicada no tópico {string} do Kafka")
    public void nenhumaMensagemPublicadaNoTopico(String topico) throws InterruptedException {
        // Esperamos um pouco para garantir que nenhuma mensagem foi enviada
        TimeUnit.SECONDS.sleep(2);
        Assertions.assertTrue(kafkaTestListener.nenhumaMensagemRecebida(), 
                "Não deveriam haver mensagens publicadas no tópico");
    }
} 