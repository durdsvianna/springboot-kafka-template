Feature: Integração de Produtos com CRM

Scenario: Integração bem-sucedida com poucos produtos
  Given que o microsserviço de integração de produtos está em execução
  And a API de produtos retorna uma lista de 10 produtos criados ou alterados no dia anterior
  When o microsserviço processa a lista de produtos
  Then uma mensagem contendo a lista de 10 produtos é publicada no tópico "PRODUTOS" do Kafka

Scenario: Integração bem-sucedida com mais de 15 produtos, dividindo em grupos
  Given que o microsserviço de integração de produtos está em execução
  And a API de produtos retorna uma lista de 28 produtos criados ou alterados no dia anterior
  When o microsserviço processa a lista de produtos
  Then 2 mensagens são publicadas no tópico "PRODUTOS" do Kafka
  And a primeira mensagem contém um grupo de até 15 produtos
  And a segunda mensagem contém os 13 produtos restantes

Scenario: Integração sem produtos para processar
  Given que o microsserviço de integração de produtos está em execução
  And a API de produtos retorna uma lista vazia de produtos criados ou alterados no dia anterior
  When o microsserviço processa a lista de produtos
  Then nenhuma mensagem é publicada no tópico "PRODUTOS" do Kafka 