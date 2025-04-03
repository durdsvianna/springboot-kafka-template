Feature: Integração de Clientes com CRM

Scenario: Integração bem-sucedida com poucos clientes
  Given que o microsserviço de integração de clientes está em execução
  And a API de clientes retorna uma lista de 10 clientes criados ou alterados no dia anterior
  When o microsserviço processa a lista de clientes
  Then uma mensagem contendo a lista de 10 clientes é publicada no tópico "CLIENTES" do Kafka

Scenario: Integração bem-sucedida com mais de 15 clientes, dividindo em grupos
  Given que o microsserviço de integração de clientes está em execução
  And a API de clientes retorna uma lista de 28 clientes criados ou alterados no dia anterior
  When o microsserviço processa a lista de clientes
  Then 2 mensagens são publicadas no tópico "CLIENTES" do Kafka
  And a primeira mensagem contém um grupo de até 15 clientes
  And a segunda mensagem contém os 13 clientes restantes

Scenario: Integração sem clientes para processar
  Given que o microsserviço de integração de clientes está em execução
  And a API de clientes retorna uma lista vazia de clientes criados ou alterados no dia anterior
  When o microsserviço processa a lista de clientes
  Then nenhuma mensagem é publicada no tópico "CLIENTES" do Kafka 