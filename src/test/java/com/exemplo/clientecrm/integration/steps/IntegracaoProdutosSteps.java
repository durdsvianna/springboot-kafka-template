package com.exemplo.clientecrm.integration.steps;

import com.exemplo.clientecrm.ClienteCrmApplication;
import com.exemplo.clientecrm.integration.config.KafkaTestListener;
import com.exemplo.clientecrm.integration.config.TestConfig;
import com.exemplo.clientecrm.model.Produto;
import com.exemplo.clientecrm.service.IntegracaoProdutoService;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootTest(classes = ClienteCrmApplication.class)
@ContextConfiguration(classes = TestConfig.class)
@EmbeddedKafka(partitions = 1, topics = {"CLIENTES", "PRODUTOS"})
@ActiveProfiles("test")
public class IntegracaoProdutosSteps {

    @Autowired
    private IntegracaoProdutoService integracaoProdutoService;

    @Autowired
    private TestConfig.MockProdutoApiService mockProdutoApiService;

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

    @Given("que o microsserviço de integração de produtos está em execução")
    public void queMicrosservicoIntegracaoProdutosEstaEmExecucao() {
        Assertions.assertNotNull(integracaoProdutoService);
    }

    @Given("a API de produtos retorna uma lista de {int} produtos criados ou alterados no dia anterior")
    public void apiProdutosRetornaListaProdutos(int quantidadeProdutos) {
        if (quantidadeProdutos == 10) {
            mockProdutoApiService.setMock10Produtos();
        } else if (quantidadeProdutos == 28) {
            mockProdutoApiService.setMock28Produtos();
        } else {
            // Por padrão, cria uma lista vazia para outros casos
            mockProdutoApiService.setMockProdutosVazios();
        }
    }

    @Given("a API de produtos retorna uma lista vazia de produtos criados ou alterados no dia anterior")
    public void apiProdutosRetornaListaVaziaProdutos() {
        mockProdutoApiService.setMockProdutosVazios();
    }

    @When("o microsserviço processa a lista de produtos")
    public void microsservicoProcessaListaProdutos() throws InterruptedException {
        integracaoProdutoService.processarProdutosDoDiaAnterior();
        // Aguarda um tempo para processamento
        TimeUnit.SECONDS.sleep(2);
    }

    @Then("uma mensagem contendo a lista de {int} produtos é publicada no tópico {string} do Kafka")
    public void mensagemContendoListaProdutosPublicadaTopico(int quantidadeProdutos, String topico) throws InterruptedException {
        boolean mensagensRecebidas = kafkaTestListener.aguardarMensagens(5);
        Assertions.assertTrue(mensagensRecebidas, "As mensagens não foram recebidas no tempo esperado");
        
        Assertions.assertEquals(1, kafkaTestListener.getQuantidadeMensagens());
        
        List<Produto> produtosNaMensagem = kafkaTestListener.getPrimeiraMensagem();
        Assertions.assertEquals(quantidadeProdutos, produtosNaMensagem.size());
    }

    @Then("a primeira mensagem contém um grupo de até {int} produtos")
    public void primeiraMensagemContemGrupoProdutos(int tamanhoMaximoGrupo) {
        List<Produto> primeiroGrupo = kafkaTestListener.getPrimeiraMensagem();
        Assertions.assertFalse(primeiroGrupo.isEmpty(), "O primeiro grupo não deveria estar vazio");
        Assertions.assertTrue(primeiroGrupo.size() <= tamanhoMaximoGrupo, 
                "O primeiro grupo deveria ter no máximo " + tamanhoMaximoGrupo + " produtos");
    }

    @Then("a segunda mensagem contém os {int} produtos restantes")
    public void segundaMensagemContemProdutosRestantes(int quantidadeRestante) {
        List<Produto> segundoGrupo = kafkaTestListener.getSegundaMensagem();
        Assertions.assertFalse(segundoGrupo.isEmpty(), "O segundo grupo não deveria estar vazio");
        Assertions.assertEquals(quantidadeRestante, segundoGrupo.size(), 
                "O segundo grupo deveria ter " + quantidadeRestante + " produtos");
    }
} 